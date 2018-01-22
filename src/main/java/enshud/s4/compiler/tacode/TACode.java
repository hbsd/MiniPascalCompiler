package enshud.s4.compiler.tacode;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import enshud.s4.compiler.tacode.jmp.IJump;
import enshud.s4.compiler.tacode.proc.Return;

public class TACode
{
    final List<ITAInst> code;
    
    public TACode(List<ITAInst> code)
    {
        this.code = code;
    }
    
    public TACode()
    {
        this(new ArrayList<>());
    }
    
    public boolean add(ITAInst inst)
    {
        return code.add(inst);
    }
    
    public int size()
    {
        return code.size();
    }
    
    public ITAInst get(int index)
    {
        return code.get(index);
    }
    
    public Stream<ITAInst> stream()
    {
        return code.stream();
    }
    
    OptionalInt findLabelToIndex(String label)
    {
        OptionalInt oi = IntStream.range(0, code.size())
            .filter(
                i -> code.get(i).getLabel()
                    .map(label::equals)
                    .getOrElse(false)
            )
            .findFirst();
        return oi;
    }
    
    List<TACode> splitByHeaders()
    {
        final List<Integer> headers = extractHeaders();
        
        return IntStream.range(0, headers.size() - 1)
            .mapToObj(i -> code.subList(headers.get(i), headers.get(i + 1)))
            .map(insts -> new TACode(new ArrayList<>(insts)))
            .collect(Collectors.toList());
    }

    private List<Integer> extractHeaders()
    {
        List<Integer> headers = new ArrayList<>();
        headers.add(0);
        headers.add(code.size());

        code.stream()
            .filter(inst -> inst instanceof IJump)
            .map(inst -> ((IJump)inst).getTo())
            .map(to -> findLabelToIndex(to))
            .filter(i -> i.isPresent())
            .mapToInt(i -> i.getAsInt())
            .forEach(headers::add);
        
        IntStream.rangeClosed(1, code.size())
            .filter(i -> (code.get(i - 1) instanceof IJump)
                      || (code.get(i - 1) instanceof Return))
            .forEach(headers::add);
        
        headers = headers.stream()
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        return headers;
    }
    
    @Override
    public String toString()
    {
        return code.stream()
                .map(i -> i.toString())
                .collect(Collectors.joining(System.lineSeparator()+"\t", "\t", System.lineSeparator()));
    }
}
