package enshud.pascal.ast.expression;

import enshud.pascal.type.BasicType;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s4.compiler.Casl2Code;
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
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("ADDA", "", "", "GR2", "GR1");
        }
    },
    SUB(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left - right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("SUBA", "", "", "GR1", "GR2");
            code.add("LD", "", "", "GR2", "GR1");
        }
    },
    MUL(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left * right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("CALL", "", "", "MULT");
        }
    },
    DIV(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left / right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("CALL", "", "", "DIV");
        }
    },
    MOD(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left % right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("CALL", "", "", "DIV");
            code.add("LD", "", "", "GR2", "GR1");
        }
    },
    
    OR(BasicType.BOOLEAN, BasicType.BOOLEAN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == 1 || right == 1);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("OR", "", "", "GR2", "GR1");
        }
    },
    AND(BasicType.BOOLEAN, BasicType.BOOLEAN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == 1 && right == 1);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("AND", "", "", "GR2", "GR1");
        }
    },
    
    EQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            final int label = l_gen.next();
            code.add("CPL", "", "; v =", "GR2", "GR1");
            code.add("JZE", "", "", "Z" + label);
            code.add("XOR", "", "", "GR2", "GR2");
            code.add("JUMP", "", "", "Q" + label);
            code.add("LAD", "Z" + label, "", "GR2", "1");
            code.add("NOP", "Q" + label, "; ^ =");
        }
    },
    NOTEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left != right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            final int label = l_gen.next();
            code.add("CPL", "", "; v <>", "GR2", "GR1");
            code.add("JNZ", "", "", "Z" + label);
            code.add("XOR", "", "", "GR2", "GR2");
            code.add("JUMP", "", "", "Q" + label);
            code.add("LAD", "Z" + label, "", "GR2", "1");
            code.add("NOP", "Q" + label, "; ^ <>");
        }
    },
    LESS(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left < right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("SUBA", "", "; <", "GR1", "GR2");
            code.add("LD", "", "", "GR2", "GR1");
            code.add("SRL", "", "", "GR2", "15");
        }
    },
    LESSEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left <= right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("SUBA", "", "; <=", "GR2", "GR1");
            code.add("SRL", "", "", "GR2", "15");
            code.add("XOR", "", "", "GR2", "=1");
        }
    },
    GREAT(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left > right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("SUBA", "", "; >", "GR2", "GR1");
            code.add("SRL", "", "", "GR2", "15");
        }
    },
    GREATEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left >= right);
        }
        
        @Override
        public void compile(Casl2Code code, LabelGenerator l_gen)
        {
            code.add("SUBA", "", "; >=", "GR1", "GR2");
            code.add("LD", "", "", "GR2", "GR1");
            code.add("SRL", "", "", "GR2", "15");
            code.add("XOR", "", "", "GR2", "=1");
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
        switch (token.getType())
        {
        case SPLUS:
            return InfixOperator.ADD;
        case SMINUS:
            return InfixOperator.SUB;
        case SSTAR:
            return InfixOperator.MUL;
        case SDIVD:
            return InfixOperator.DIV;
        case SMOD:
            return InfixOperator.MOD;
        case SOR:
            return InfixOperator.OR;
        case SAND:
            return InfixOperator.AND;
        case SEQUAL:
            return InfixOperator.EQUAL;
        case SNOTEQUAL:
            return InfixOperator.NOTEQUAL;
        case SLESS:
            return InfixOperator.LESS;
        case SLESSEQUAL:
            return InfixOperator.LESSEQUAL;
        case SGREAT:
            return InfixOperator.GREAT;
        case SGREATEQUAL:
            return InfixOperator.GREATEQUAL;
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
    
    public abstract void compile(Casl2Code code, LabelGenerator l_gen); // left->GR1,right->GR2
}
