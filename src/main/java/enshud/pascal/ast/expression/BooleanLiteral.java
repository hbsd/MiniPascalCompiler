package enshud.pascal.ast.expression;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.pascal.type.IType;
import enshud.pascal.Procedure;
import enshud.pascal.type.BasicType;
import enshud.s1.lexer.LexedToken;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class BooleanLiteral implements IConstant
{
    private boolean          val;
    private final LexedToken token;
    
    public BooleanLiteral(boolean val)
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
    public IType check(Procedure proc, Checker checker)
    {
        return getType();
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        code.addLoadImm("GR2", getInt());
    }
    
    @Override
    public String toString()
    {
        return "" + getBool();
    }
}

