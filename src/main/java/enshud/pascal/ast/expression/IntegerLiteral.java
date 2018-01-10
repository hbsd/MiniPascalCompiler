package enshud.pascal.ast.expression;

import java.util.Objects;

import enshud.s3.checker.Checker;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.type.BasicType;
import enshud.s4.compiler.Casl2Code;
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
    
    public IntegerLiteral(int num)
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
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visitIntegerLiteral(this, option);
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
}

