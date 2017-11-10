package enshud.pascal.ast;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s1.lexer.LexedToken;
import enshud.s4.compiler.LabelGenerator;


public class BooleanLiteral implements IConstant
{
    BooleanValue     val;
    final LexedToken token;
    
    BooleanLiteral(boolean val)
    {
        this.val = val? BooleanValue.TRUE: BooleanValue.FALSE;
        this.token = LexedToken.DUMMY;
    }
    
    public BooleanLiteral(LexedToken token)
    {
        this.token = Objects.requireNonNull(token);
        switch (token.getType())
        {
        case SFALSE:
            val = BooleanValue.FALSE;
            break;
        case STRUE:
            val = BooleanValue.TRUE;
            break;
        default:
            val = null;
            assert false;
        }
    }
    
    public BooleanValue getValue()
    {
        return val;
    }
    
    public boolean getBool()
    {
        return val.getBool();
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
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        codebuilder.append(" LAD GR2,").append(getBool()? "1": "0").append(System.lineSeparator());
    }
}

