package enshud.pascal.ast.expression;


import enshud.pascal.InfixOperator;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.TokenNode;


public class InfixOperation implements IExpression
{
    private IExpression         left;
    private IExpression         right;
    private final InfixOperator op;
    private final LexedToken    op_token;
    private IType               type;
    
    public InfixOperation(IExpression left, IExpression right, LexedToken op_token)
    {
        this.left = left;
        this.right = right;
        this.op_token = op_token;
        this.op = InfixOperator.getFromToken(op_token);
        type = null;
    }
    
    public InfixOperation(IExpression left, IExpression right, TokenNode op_token)
    {
        this(left, right, op_token.getToken());
    }
    
    public InfixOperation(IExpression left, IExpression right, InfixOperator op)
    {
        this.left = left;
        this.right = right;
        this.op_token = LexedToken.DUMMY;
        this.op = op;
        type = null;
    }
    
    public IExpression getLeft()
    {
        return left;
    }
    
    public void setLeft(IExpression left)
    {
        this.left = left;
    }
    
    public IExpression getRight()
    {
        return right;
    }
    
    public void setRight(IExpression right)
    {
        this.right = right;
    }
    
    public InfixOperator getOp()
    {
        return op;
    }
    
    public void setType(IType type)
    {
        this.type = type;
    }
    
    public LexedToken getOpToken()
    {
        return op_token;
    }
    
    @Override
    public int getLine()
    {
        return getLeft().getLine();
    }
    
    @Override
    public int getColumn()
    {
        return getLeft().getColumn();
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
    
    @Override
    public boolean equals(IExpression rexp)
    {
        if (rexp instanceof InfixOperation)
        {
            final InfixOperation io = (InfixOperation)rexp;
            return this == rexp || (this.op == io.op && this.left.equals(io.left) && this.right.equals(io.right));
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public IType getType()
    {
        return type;
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public void printBodyln(String indent)
    {
        getLeft().println(indent + " |");
        INode.printIndent(indent + " |");
        INode.printBlueln("-" + getOp().toString());
        getRight().println(indent + " |");
    }
}
