package enshud.s4.compiler;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class LabelGenerator implements Iterator<String>
{
    final StringBuilder builder;
    
    public LabelGenerator()
    {
        builder = new StringBuilder("A");
    }
    
    @Override
    public boolean hasNext()
    {
        return builder.length() != 8 || '9' != builder.charAt(builder.length() - 1);
    }
    
    @Override
    public String next()
    {
        final char c = builder.charAt(builder.length() - 1);
        if (!hasNext())
        {
            throw new NoSuchElementException();
        }
        if (c == '9')
        {
            builder.append('A');
        }
        else
        {
            final char next_c = ((c >= 'A' && c <= 'Y') || (c >= '0' && c <= '8'))? (char)(c + 1): c == 'Z'? '0': '_';
            builder.setCharAt(builder.length() - 1, next_c);
        }
        return builder.toString();
    }
    
    public void reset()
    {
        builder.delete(0, builder.length());
    }
    
    @Override
    public String toString()
    {
        return builder.toString();
    }
}
