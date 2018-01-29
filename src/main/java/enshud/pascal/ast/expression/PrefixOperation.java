package enshud.pascal.ast.expression;

import enshud.pascal.PrefixOperator;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s2.parser.node.TokenNode;


public class PrefixOperation implements IExpression
{
    private IExpression          operand;
    private final PrefixOperator op;
    private final LexedToken     op_token;
    private IType                type;
    
    public PrefixOperation(IExpression operand, LexedToken op_token)
    {
        this.operand = operand;
        this.op_token = op_token;
        this.op = PrefixOperator.getFromToken(op_token);
        this.type = null;
    }
    
    public PrefixOperation(IExpression operand, TokenNode op_token)
    {
        this(operand, op_token.getToken());
    }
    
    public PrefixOperation(IExpression operand, PrefixOperator op)
    {
        this.operand = operand;
        this.op_token = LexedToken.DUMMY;
        this.op = op;
        this.type = null;
    }
    
    public IExpression getOperand()
    {
        return operand;
    }
    
    public void setOperand(IExpression operand)
    {
        this.operand = operand;
    }
    
    public PrefixOperator getOp()
    {
        return op;
    }
    
    public LexedToken getOpToken()
    {
        return op_token;
    }
    
    @Override
    public int getLine()
    {
        return op_token.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return op_token.getColumn();
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
    
    @Override
    public IType getType()
    {
        return type;
    }
    
    public void setType(IType type)
    {
        this.type = type;
    }
    
    @Override
    public boolean equals(IExpression rexp)
    {
        if (rexp instanceof PrefixOperation)
        {
            PrefixOperation po = (PrefixOperation)rexp;
            return this == rexp || (this.op == po.op && this.operand.equals(po.operand));
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public String toString()
    {
        return getOp().toString();
    }
    
    @Override
    public String toOriginalCode(String indent)
    {
        final String e = getOperand() instanceof IConstant || getOperand() instanceof IVariable
                ? getOperand().toOriginalCode("")
                : "(" + getOperand().toOriginalCode("") + ")";
        return getOp() + e;
    }
    
    @Override
    public void printBodyln(String indent)
    {
        getOperand().println(indent + " |");
    }
}
