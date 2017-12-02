package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.pascal.type.BasicType;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class IntegerLiteral implements IConstant
{
    private int              num;
    private final LexedToken token;
    
    public IntegerLiteral(LexedToken token)
    {
        this.num = Integer.parseInt(token.getString());
        this.token = Objects.requireNonNull(token);
    }
    
    IntegerLiteral(int num)
    {
        this.num = num;
        this.token = LexedToken.DUMMY;
    }
    
    @Override
    public int getInt()
    {
        return num;
    }
    
    @Override
    public IType getType()
    {
        return BasicType.INTEGER;
    }
    
    @Override
    public int getLine()
    {
        return token.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return token.getColumn();
    }
    
    @Override
    public String toString()
    {
        return token.getString();
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
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        if (getInt() == 0)
        {
            code.add(new Casl2Instruction("XOR", "", "", "GR2", "GR2"));
        }
        else
        {
            code.add(new Casl2Instruction("LAD", "", "", "GR2", "" + getInt()));
        }
    }
}

