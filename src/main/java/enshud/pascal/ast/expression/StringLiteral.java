package enshud.pascal.ast.expression;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.pascal.type.IType;
import enshud.pascal.Procedure;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.StringType;
import enshud.s2.parser.node.basic.TokenNode;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class StringLiteral implements IConstant
{
    private final TokenNode str;
    private IType           type;
    
    public StringLiteral(TokenNode str)
    {
        this.str = Objects.requireNonNull(str);
        type = new StringType(length());
    }
    
    @Override
    public int getInt()
    {
        if(getType() == BasicType.CHAR)
        {
            return (int)toString().charAt(1);
        }
        else
        {
            throw new UnsupportedOperationException("not CHAR type");
        }
    }
    
    @Override
    public String toString()
    {
        return str.getString();
    }
    
    public int length()
    {
        return toString().length() - 2;
    }
    
    @Override
    public IType getType()
    {
        return type;
    }
    
    @Override
    public int getLine()
    {
        return str.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return str.getColumn();
    }
    
    @Override
    public void retype(IType new_type)
    {
        type = new_type;
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        return type;
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        if (type == BasicType.CHAR)
        {
            code.addLoadImm("GR2", (int)toString().charAt(1));
        }
        else
        {
            code.add("LAD", "", "", "GR2", "=" + toString());
            code.addLoadImm("GR1", length());
        }
    }
}

