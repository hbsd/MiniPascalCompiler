package enshud.pascal.ast;

import enshud.pascal.type.BasicType;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s4.compiler.LabelGenerator;

public enum PrefixOperator
{
    PLUS(BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int operand)
        {
            return new IntegerLiteral(operand);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            // Empty
        }
    },
    MINUS(BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int operand)
        {
            return new IntegerLiteral(-operand);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" LD GR1,GR2").append(System.lineSeparator());
            codebuilder.append(" XOR GR2,GR2").append(System.lineSeparator());
            codebuilder.append(" SUBA GR2,GR1").append(System.lineSeparator());
        }
    },
    NOT(BasicType.BOOLEAN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int operand)
        {
            return new BooleanLiteral(operand == 0);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" XOR GR2,=1").append(System.lineSeparator());
        }
    };
    
    private final IType operand_type;
    private final IType ret_type;
    
    private PrefixOperator(IType operand_type, IType ret_type)
    {
        this.operand_type = operand_type;
        this.ret_type = ret_type;
    }
    
    public static PrefixOperator getFromToken(LexedToken token)
    {
        switch(token.getType())
        {
        case SPLUS:  return PLUS;
        case SMINUS: return MINUS;
        case SNOT:   return NOT;
        default:
            assert false;
            return null;
        }
    }
    
    public IType getOperandType()
    {
        return operand_type;
    }
    
    public IType getReturnType()
    {
        return ret_type;
    }
    
    public abstract IConstant eval(int operand);
    
    public abstract void compile(StringBuilder codebuilder, LabelGenerator l_gen); // left->GR1,right->GR2
}
