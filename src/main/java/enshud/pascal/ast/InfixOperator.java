package enshud.pascal.ast;

import enshud.pascal.type.BasicType;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s4.compiler.LabelGenerator;


public enum InfixOperator
{
    ADD(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left + right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" ADDA GR2,GR1").append(System.lineSeparator());
        }
    },
    SUB(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left - right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" SUBA GR1,GR2").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1").append(System.lineSeparator());
        }
    },
    MUL(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left * right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" CALL MULT").append(System.lineSeparator());
        }
    },
    DIV(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left / right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" CALL DIV").append(System.lineSeparator());
        }
    },
    MOD(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left % right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" CALL DIV").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1").append(System.lineSeparator());
        }
    },
    
    OR(BasicType.BOOLEAN, BasicType.BOOLEAN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == 1 || right == 1);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" OR GR2,GR1").append(System.lineSeparator());
        }
    },
    AND(BasicType.BOOLEAN, BasicType.BOOLEAN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == 1 && right == 1);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" AND GR2,GR1").append(System.lineSeparator());
        }
    },
    
    EQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            final String label = l_gen.next();
            codebuilder.append(" CPL GR2,GR1   ; v =").append(System.lineSeparator());
            codebuilder.append(" JZE ").append("Z").append(label).append(System.lineSeparator());
            codebuilder.append(" XOR GR2,GR2").append(System.lineSeparator());
            codebuilder.append(" JUMP Q").append(label).append(System.lineSeparator());
            codebuilder.append("Z").append(label).append(" LAD GR2,1").append(System.lineSeparator());
            codebuilder.append("Q").append(label).append(" NOP    ; ^ =").append(System.lineSeparator());
        }
    },
    NOTEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left != right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            final String label = l_gen.next();
            codebuilder.append(" CPL GR2,GR1   ; v <>").append(System.lineSeparator());
            codebuilder.append(" JZE ").append("Z").append(label).append(System.lineSeparator());
            codebuilder.append(" LAD GR2,1").append(System.lineSeparator());
            codebuilder.append(" JUMP Q").append(label).append(System.lineSeparator());
            codebuilder.append("Z").append(label).append(" XOR GR2,GR2").append(System.lineSeparator());
            codebuilder.append("Q").append(label).append(" NOP    ; ^ <>").append(System.lineSeparator());
        }
    },
    LESS(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left < right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" SUBA GR1,GR2; <").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1  ;").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
        }
    },
    LESSEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left <= right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" SUBA GR2,GR1; <=").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
            codebuilder.append(" XOR GR2,=1  ;").append(System.lineSeparator());
        }
    },
    GREAT(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left > right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" SUBA GR2,GR1; >").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
        }
    },
    GREATEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left >= right);
        }
        
        @Override
        public void compile(StringBuilder codebuilder, LabelGenerator l_gen)
        {
            codebuilder.append(" SUBA GR1,GR2; >=").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1  ;").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
            codebuilder.append(" XOR GR2,=1  ;").append(System.lineSeparator());
        }
    };
    
    private final IType left_type;
    private final IType right_type;
    private final IType ret_type;
    
    private InfixOperator(IType left_type, IType right_type, IType ret_type)
    {
        this.left_type = left_type;
        this.right_type = right_type;
        this.ret_type = ret_type;
    }
    
    public static InfixOperator getFromToken(LexedToken token)
    {
        switch(token.getType())
        {
        case SPLUS:       return InfixOperator.ADD;
        case SMINUS:      return InfixOperator.SUB;
        case SSTAR:       return InfixOperator.MUL;
        case SDIVD:       return InfixOperator.DIV;
        case SMOD:        return InfixOperator.MOD;
        case SOR:         return InfixOperator.OR;
        case SAND:        return InfixOperator.AND;
        case SEQUAL:      return InfixOperator.EQUAL;
        case SNOTEQUAL:   return InfixOperator.NOTEQUAL;
        case SLESS:       return InfixOperator.LESS;
        case SLESSEQUAL:  return InfixOperator.LESSEQUAL;
        case SGREAT:      return InfixOperator.GREAT;
        case SGREATEQUAL: return InfixOperator.GREATEQUAL;
        default:
            assert false;
            return null;
        }
    }
    
    public IType getLeftType()
    {
        return left_type;
    }
    
    public IType getRightType()
    {
        return right_type;
    }
    
    public IType getReturnType()
    {
        return ret_type;
    }
    
    public abstract IConstant eval(int left, int right);
    
    public abstract void compile(StringBuilder codebuilder, LabelGenerator l_gen); // left->GR1,right->GR2
}
