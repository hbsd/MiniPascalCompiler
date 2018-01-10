package enshud.pascal.ast.statement;

import java.util.List;
import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.Procedure;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.NodeList;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class WriteStatement implements IStatement
{
    private final NodeList<IExpression> exps;
    
    public WriteStatement(NodeList<IExpression> exps)
    {
        this.exps = Objects.requireNonNull(exps);
    }
    
    public List<IExpression> getExpressions()
    {
        return exps;
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visitWriteStatement(this, option);
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        int i = 1;
        for (final IExpression exp: getExpressions())
        {
            IType type = exp.check(proc, checker);
            
            if (!type.equals(BasicType.INTEGER) && !type.equals(BasicType.CHAR) && !type.isArrayOf(BasicType.CHAR))
            {
                checker.addErrorMessage(
                    proc, exp, "incompatible type: " + Checker.getOrderString(i)
                            + " argument of writeln must be INTEGER, CHAR, or array of CHAR, but is " + type + "."
                );
            }
            /*else if (type.isUnknown())
            {
                checker.addErrorMessage(
                    proc, exp, "cannot identify the type of " + Checker.getOrderString(i) + " argument of writeln."
                );
            }*/
            ++i;
        }
        return null;
    }
    
    @Override
    public IStatement precompute(Procedure proc)
    {
        exps.forEach(e -> e.preeval(proc));
        return this;
    }
    
    @Override
    public void compile(Casl2Code code, Procedure proc, LabelGenerator l_gen)
    {
        for (final IExpression e: getExpressions())
        {
            e.compile(code, proc, l_gen);
            
            if (e.getType() == BasicType.CHAR)
            {
                code.add("CALL", "", "", "WRTCH");
            }
            else if (e.getType() == BasicType.INTEGER)
            {
                code.add("CALL", "", "", "WRTINT");
            }
            else if (e.getType().isArrayOf(BasicType.CHAR))
            {
                code.add("CALL", "", "", "WRTSTR");
            }
            else
            {
                assert false: "type error: (" + e.getLine() + "," + e.getColumn() + ")" + e.getType();
            }
        }
        code.add("CALL", "", "", "WRTLN");
    }
    
    @Override
    public String toString()
    {
        return "";
    }
    
    @Override
    public int getLine()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getColumn()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void printBodyln(String indent)
    {
        exps.println(indent + "  ");
    }
}

