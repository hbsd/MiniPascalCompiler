package enshud.pascal;


import enshud.pascal.ast.expression.BooleanLiteral;
import enshud.pascal.ast.expression.IConstant;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.ast.expression.IntegerLiteral;
import enshud.pascal.ast.expression.PrefixOperation;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public enum PrefixOperator
{
    PLUS {
        @Override
        public IType checkType(Procedure proc, Checker checker, LexedToken op_tok, IType given)
        {
            return check_(proc, checker, op_tok, given, BasicType.INTEGER, BasicType.INTEGER);
        }
        
        @Override
        protected IConstant eval(int operand)
        {
            return IntegerLiteral.create(operand);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            // Empty
        }
    },
    MINUS {
        @Override
        public IType checkType(Procedure proc, Checker checker, LexedToken op_tok, IType given)
        {
            return check_(proc, checker, op_tok, given, BasicType.INTEGER, BasicType.INTEGER);
        }
        
        @Override
        public IExpression eval(PrefixOperation prefix)
        {
            if (prefix.getOperand() instanceof PrefixOperation)
            {
                final PrefixOperation po = (PrefixOperation)prefix.getOperand();
                if (po.getOp() == MINUS)
                {
                    return po.getOperand();
                }
            }
            return super.eval(prefix);
        }
        
        @Override
        protected IConstant eval(int operand)
        {
            return IntegerLiteral.create(-operand);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("LD", "", "", "GR1", "GR2");
            code.add("XOR", "", "", "GR2", "GR2");
            code.add("SUBA", "", "", "GR2", "GR1");
        }
    },
    NOT {
        @Override
        public IType checkType(Procedure proc, Checker checker, LexedToken op_tok, IType given)
        {
            return check_(proc, checker, op_tok, given, BasicType.BOOLEAN, BasicType.BOOLEAN);
        }
        
        @Override
        public IExpression eval(PrefixOperation prefix)
        {
            if (prefix.getOperand() instanceof PrefixOperation)
            {
                final PrefixOperation po = (PrefixOperation)prefix.getOperand();
                if (po.getOp() == NOT)
                {
                    return po.getOperand();
                }
            }
            return super.eval(prefix);
        }
        
        @Override
        protected IConstant eval(int operand)
        {
            return BooleanLiteral.create(operand == 0);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("XOR", "", "", "GR2", "=1");
        }
    };
    
    public static PrefixOperator getFromToken(LexedToken token)
    {
        switch (token.getType())
        {
        case SPLUS:
            return PLUS;
        case SMINUS:
            return MINUS;
        case SNOT:
            return NOT;
        default:
            assert false;
            return null;
        }
    }
    
    public abstract IType checkType(Procedure proc, Checker checker, LexedToken op_tok, IType given);
    
    protected IType check_(
        Procedure proc, Checker checker, LexedToken op_tok, IType given, IType expected, IType result
    )
    {
        if (!given.equals(expected))
        {
            checker.addErrorMessage(
                proc, op_tok,
                "incompatible type: cannot use " + given + " type as operand of " + this + " operator. must be "
                        + expected + "."
            );
        }
        return result;
    }
    
    public IExpression eval(PrefixOperation prefix)
    {
        final IExpression opd = prefix.getOperand();
        return (opd.isConstant())
                ? eval(((IConstant)opd).getValue().getInt())
                : null;
    }
    
    protected abstract IConstant eval(int operand);
    
    public abstract void compile(Casl2Code code, LabelGenerator l_gen); // left->GR1,right->GR2
}
