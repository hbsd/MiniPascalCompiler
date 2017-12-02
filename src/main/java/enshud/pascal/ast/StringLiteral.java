package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.StringType;
import enshud.s2.parser.node.basic.TokenNode;
import enshud.s4.compiler.Casl2Instruction;
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
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        if (type == BasicType.CHAR)
        {
            code.add(new Casl2Instruction("LAD", "", "", "GR2", "" + (int)toString().charAt(1)));
        }
        else
        {
            code.add(new Casl2Instruction("LAD", "", "", "GR2", "=" + toString()));
            if (length() == 0)
            {
                code.add(new Casl2Instruction("XOR", "", "", "GR1", "GR1"));
            }
            else
            {
                code.add(new Casl2Instruction("LAD", "", "", "GR1", "" + length()));
            }
        }
    }
}

