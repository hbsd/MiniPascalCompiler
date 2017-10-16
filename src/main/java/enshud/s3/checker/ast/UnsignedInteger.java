package enshud.s3.checker.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s2.parser.node.basic.TokenNode;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;
import enshud.s4.compiler.LabelGenerator;


public class UnsignedInteger implements IConstant
{
    final TokenNode num;

    public UnsignedInteger(TokenNode num)
    {
        this.num = Objects.requireNonNull(num);
    }

    public int getInt()
    {
        return Integer.parseInt(num.getString());
    }
    
    @Override
    public IType getType()
    {
        return RegularType.INTEGER;
    }

    @Override
    public int getLine()
    {
        return num.getLine();
    }

    @Override
    public int getColumn()
    {
        return num.getColumn();
    }

    @Override
    public String toString()
    {
        return num.getString();
    }
    
    @Override
    public void retype(IType new_type)
    {}

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        return getType();
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        if(getInt() == 0)
        {
            codebuilder.append(" XOR GR2,GR2").append(System.lineSeparator());
        }
        else
        {
            codebuilder.append(" LAD GR2,").append(getInt()).append(System.lineSeparator());
        }
    }
}


