package enshud.s4.compiler;

import java.util.NoSuchElementException;


public class LabelGenerator
{
    private int num;
    
    public LabelGenerator()
    {
        num = 0;
    }
    
    public boolean hasNext()
    {
        return num < Integer.MAX_VALUE;
    }
    
    public int next()
    {
        if(!hasNext())
        {
            throw new NoSuchElementException("exhaust all label");
        }
        return num++;
    }
}
