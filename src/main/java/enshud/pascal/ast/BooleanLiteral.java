package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s1.lexer.LexedToken;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class BooleanLiteral implements IConstant
{
    private boolean          val;
    private final LexedToken token;
    
    BooleanLiteral(boolean val)
    {
        this.val = val;
        this.token = LexedToken.DUMMY;
    }
    
    public BooleanLiteral(LexedToken token)
    {
        this.token = Objects.requireNonNull(token);
        switch (token.getType())
        {
        case SFALSE:
            val = false;
            break;
        case STRUE:
            val = true;
            break;
        default:
            assert false;
        }
    }
    
    @Override
    public int getInt()
    {
        return getBool()? 1: 0;
    }
    
    public boolean getBool()
    {
        return val;
    }
    
    @Override
    public IType getType()
    {
        return BasicType.BOOLEAN;
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
        code.add(new Casl2Instruction("LAD", "", "", "GR2", getBool()? "1": "0"));
    }
    
    @Override
    public String toString()
    {
        return "" + getBool();
    }
}

