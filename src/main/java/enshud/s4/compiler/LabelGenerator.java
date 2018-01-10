package enshud.s4.compiler;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;


public class LabelGenerator implements PrimitiveIterator.OfInt
{
    private int num;
    
    public LabelGenerator()
    {
        num = 0;
    }
    
    @Override
    public boolean hasNext()
    {
        return num < Integer.MAX_VALUE;
    }
    
    @Override
    public int nextInt()
    {
        if(!hasNext())
        {
            throw new NoSuchElementException("exhaust all label");
        }
        return num++;
    }
}
