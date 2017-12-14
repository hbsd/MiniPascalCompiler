package enshud.pascal.ast.expression;

import enshud.pascal.PrefixOperator;
import enshud.pascal.Procedure;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s2.parser.node.basic.TokenNode;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


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
    
    public IExpression getOperand()
    {
        return operand;
    }
    
    public PrefixOperator getOp()
    {
        return op;
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
    public IType check(Procedure proc, Checker checker)
    {
        final IType t = getOperand().check(proc, checker);
        type = getOp().checkType(proc, checker, op_token, t);
        return type;
    }
    
    @Override
    public IConstant preeval(Procedure proc)
    {
        IConstant res = getOperand().preeval(proc);
        if (res == null)
        {
            return null;
        }
        
        operand = res;
        
        return getOp().eval(res.getInt());
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        getOperand().compile(code, proc, l_gen);
        getOp().compile(code, l_gen);
    }
    
    @Override
    public IType getType()
    {
        return type;
    }
    
    @Override
    public String toString()
    {
        return getOp().toString();
    }
    
    @Override
    public void printBodyln(String indent)
    {
        getOperand().println(indent + " |");
    }
}
