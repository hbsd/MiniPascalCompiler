package enshud.s4.compiler;


public class Casl2Instruction
{
    private String   label;
    private String   mnem;
    private String[] operands;
    private String   comment;
    
    public Casl2Instruction(String mnem, String label, String comment, String... operands)
    {
        this.label = label;
        this.mnem = mnem;
        this.operands = operands;
        this.comment = comment;
    }
    
    @Override
    public String toString()
    {
        if(mnem.equals(""))
        {
            return comment;
        }
        else
        {
            return String.format("%-8s %-5s%s%s", label, mnem, " " + String.join(",", operands), " " + comment);
        }
    }
}
