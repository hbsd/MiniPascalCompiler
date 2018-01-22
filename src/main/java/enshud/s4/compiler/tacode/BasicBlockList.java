package enshud.s4.compiler.tacode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import enshud.s4.compiler.tacode.jmp.JumpFalse;
import enshud.s4.compiler.tacode.jmp.Jump;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;


public class BasicBlockList
{
    private List<BasicBlock>     blocks   = new ArrayList<>();
    private Set<Set<BasicBlock>> nat_loop = new HashSet<>();
    
    private BasicBlockList(List<BasicBlock> blocks)
    {
        this.blocks = blocks;
    }
    
    public BasicBlock getEnterBlock()
    {
        return blocks.get(0);
    }
    
    int size()
    {
        final BasicBlock b = blocks.get(blocks.size() - 1);
        return b.offset + b.code.size();
    }
    
    ITAInst getInst(int index)
    {
        return blocks.stream()
            .filter(b -> index < b.offset + b.code.size())
            .map(b -> b.code.get(index - b.offset))
            .findFirst()
            .orElse(null);
    }
    
    void setInst(int index, ITAInst inst)
    {
        blocks.stream()
            .filter(b -> index < b.offset + b.code.size())
            .limit(1)
            .forEachOrdered(b -> b.code.code.set(index - b.offset, inst));
    }
    
    public void refresh()
    {
        setOffsets();
        linkSuccs();
        linkPreds();
        removeDeadBlock();
        calcDominators();
        findNaturalLoop();
        calcSets();
    }
    
    public static BasicBlockList fromInsts(TACode code)
    {
        final BasicBlockList blocks = new BasicBlockList(splitToBlocks(code));
        blocks.refresh();
        return blocks;
    }
    
    /*private void calcDie()
    {
        for(final BasicBlock b: blocks)
        {
            if(b.succ.isEmpty())
            {
                Set<String> new_die = new HashSet<>(b.in);
            }
            else
            {
                
            }
        }
    }*/
    
    private void calcSets()
    {
        _calcSets1();
        _calcSets2();
    }
    
    private void _calcSets1()
    {
        blocks.forEach(
            b -> IntStream.range(0, b.code.size())
                .filter(i -> b.code.get(i).getAssigned().isPresent())
                .forEach(i -> b.gen.put(b.code.get(i).getAssigned().get(), i + b.offset))
        );
        
        blocks.forEach(
            b -> {
                blocks.stream()
                    .filter(b1 -> b1 != b)
                    .flatMap(b1 -> b1.gen.entrySet().stream())
                    .filter(e -> b.gen.keySet().contains(e.getKey()))
                    .forEach(e -> b.kill.put(e.getKey(), e.getValue()));
            }
        );
        
        blocks.forEach(
            b -> {
                b.use.clear();
                IntStream.range(0, b.code.size())
                    .map(i -> b.code.size() - i - 1)
                    .forEach(i -> {
                        b.code.get(i).getAssigned()
                            .ifPresent(b.use::remove);
                        b.code.get(i).getRefered()
                            .forEach(v -> b.use.put(v, b.offset + i));
                    });
            }
        );
    }
    
    private void _calcSets2()
    {
        while (true)
        {
            boolean f = false;
            for (final BasicBlock b: blocks)
            {
                final Map<String, Integer> new_in = new HashMap<>();
                b.pred.stream()
                    .map(p -> p.out)
                    .forEach(new_in::putAll);
                
                if (!new_in.equals(b.in))
                {
                    f = true;
                    b.in.clear();
                    b.in.putAll(new_in);
                }
                
                final Map<String, Integer> new_out = new HashMap<>(b.in);
                new_out.keySet().removeAll(b.kill.keySet());
                new_out.putAll(b.gen);
                
                if (!new_out.equals(b.out))
                {
                    f = true;
                    b.out.clear();
                    b.out.putAll(new_out);
                }
                
                final Map<String, Integer> new_live = new HashMap<>();
                
            }
            if (!f)
            {
                break;
            }
        }
    }
    
    public void findNaturalLoop()
    {
        nat_loop.clear();
        blocks.stream()
            .flatMap(
                block -> block.succ.stream()
                    .filter(block.domin::contains)
                    .map(b -> calcNaturalLoop(block, b))
            )
            .forEach(nat_loop::add);
    }
    
    private static Set<BasicBlock> calcNaturalLoop(BasicBlock n, BasicBlock d)
    {
        final Deque<BasicBlock> stack = new ArrayDeque<>();
        final Set<BasicBlock> loop = new HashSet<>();
        loop.add(d);
        if (!d.equals(n))
        {
            loop.add(n);
            stack.push(n);
        }
        while (!stack.isEmpty())
        {
            stack.pop().pred.stream()
                .filter(p -> !loop.contains(p))
                .forEach(
                    p -> {
                        loop.add(p);
                        stack.push(p);
                    }
                );
        }
        return loop;
    }
    
    private void setOffsets()
    {
        int i = 0;
        for (final BasicBlock b: blocks)
        {
            b.offset = i;
            i += b.code.size();
        }
    }
    
    private static List<BasicBlock> splitToBlocks(TACode code)
    {
        final List<TACode> codes = code.splitByHeaders();
        return IntStream.range(0, codes.size())
            .mapToObj(i -> new BasicBlock("B" + i, codes.get(i)))
            .collect(Collectors.toList());
    }
    
    private void removeDeadBlock()
    {
        if (blocks.size() >= 2)
        {
            blocks.subList(1, blocks.size())
                .removeIf(b -> b.pred.isEmpty());
        }
    }
    
    private void calcDominators()
    {
        if (blocks.size() == 0)
        {
            return;
        }
        blocks.get(0).domin.clear();
        blocks.get(0).domin.add(blocks.get(0));
        if (blocks.size() == 1)
        {
            return;
        }
        blocks.subList(1, blocks.size())
            .forEach(
                b -> {
                    b.domin.clear();
                    b.domin.addAll(blocks);
                }
            );
        while (true)
        {
            boolean change = false;
            for (final BasicBlock block: blocks.subList(1, blocks.size()))
            {
                final Set<BasicBlock> new_d = new HashSet<>();
                block.pred.stream()
                    .map(b -> b.domin)
                    .forEach(
                        dom -> {
                            if (new_d.isEmpty())
                                new_d.addAll(dom);
                            else
                                new_d.retainAll(dom);
                        }
                    );
                new_d.add(block);
                
                if (!new_d.equals(block.domin))
                {
                    block.domin.clear();
                    block.domin.addAll(new_d);
                    change = true;
                }
            }
            if (!change)
            {
                break;
            }
        }
    }
    
    private void linkSuccs()
    {
        blocks.forEach(b -> b.succ.clear());
        IntStream.range(0, blocks.size())
            .forEach(
                i -> {
                    final BasicBlock b = blocks.get(i);
                    final ITAInst inst = b.code.get(b.code.size() - 1);
                    if (inst instanceof Jump)
                    {
                        b.succ.add(findLabelToBlock(((Jump)inst).getTo()));
                    }
                    else
                    {
                        if (inst instanceof JumpFalse)
                        {
                            b.succ.add(findLabelToBlock(((JumpFalse)inst).getTo()));
                        }
                        if (i < blocks.size() - 1)
                        {
                            b.succ.add(blocks.get(i + 1));
                        }
                    }
                }
            );
    }
    
    private void linkPreds()
    {
        blocks.forEach(b -> b.pred.clear());
        blocks.forEach(
            block -> blocks.stream()
                .filter(b -> b.succ.contains(block))
                .forEach(b -> block.pred.add(b))
        );
    }
    
    private BasicBlock findLabelToBlock(String label)
    {
        return blocks.stream()
            .filter(block -> block.code.findLabelToIndex(label).isPresent())
            .findFirst()
            .get(); // assert null
    }
    
    @Override
    public String toString()
    {
        return blocks.stream()
            .map(b -> b.toString())
            .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }
}
