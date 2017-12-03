package enshud.s4.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Casl2Code
{
    private final List<Casl2Instruction> list = new ArrayList<>();
    
    public Stream<Casl2Instruction> stream()
    {
        return list.stream();
    }

    private boolean add(Casl2Instruction inst)
    {
        return list.add(inst);
    }
    
    public boolean add(String mnem, String label, String comment, String... operands)
    {
        return add(new Casl2Instruction(mnem, label, comment, operands));
    }
    
    
    public boolean addLoadImm(String gr, int num, String label, String comment)
    {
        if(num == 0)
        {
            return add(new Casl2Instruction("XOR", label, comment, gr, gr));
        }
        else
        {
            return add(new Casl2Instruction("LAD", label, comment, gr, "" + num));
        }
    }
    
    public boolean addLoadImm(String gr, int num)
    {
        return addLoadImm(gr, num, "", "");
    }
    
    public boolean addAddlImm(String gr, int num)
    {
        if(num != 0)
        {
            return add(new Casl2Instruction("LAD", "", "", gr, "" + num, gr));
        }
        else
        {
            return true;
        }
    }
}
