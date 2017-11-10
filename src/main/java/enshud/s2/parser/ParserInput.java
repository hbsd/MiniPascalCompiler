package enshud.s2.parser;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import enshud.s1.lexer.LexedToken;


public class ParserInput
{
    private List<LexedToken> list;
    private int              index;
    
    public ParserInput(List<LexedToken> list)
    {
        this.list = Objects.requireNonNull(list);
        this.index = 0;
    }
    
    public List<LexedToken> getList()
    {
        return list;
    }
    
    public int size()
    {
        return list.size();
    }
    
    public boolean isEmpty()
    {
        return index >= list.size();
    }
    
    public LexedToken getFront()
    {
        if (!isEmpty())
        {
            return list.get(index);
        }
        else
        {
            throw new NoSuchElementException();
        }
    }
    
    public void popFront()
    {
        ++index;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public void setIndex(int index)
    {
        this.index = index;
    }
}
