package enshud.pascal.ast;

import java.util.List;

import enshud.pascal.type.BasicType;
import enshud.pascal.type.IType;
import enshud.s1.lexer.LexedToken;
import enshud.s4.compiler.Casl2Instruction;
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
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("ADDA", "", "", "GR2", "GR1"));
        }
    },
    SUB(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left - right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("SUBA", "", "", "GR1", "GR2"));
            code.add(new Casl2Instruction("LD", "", "", "GR2", "GR1"));
        }
    },
    MUL(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left * right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("CALL", "", "", "MULT"));
        }
    },
    DIV(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left / right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("CALL", "", "", "DIV"));
        }
    },
    MOD(BasicType.INTEGER, BasicType.INTEGER, BasicType.INTEGER) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new IntegerLiteral(left % right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("CALL", "", "", "DIV"));
            code.add(new Casl2Instruction("LD", "", "", "GR2", "GR1"));
        }
    },
    
    OR(BasicType.BOOLEAN, BasicType.BOOLEAN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == 1 || right == 1);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("OR", "", "", "GR2", "GR1"));
        }
    },
    AND(BasicType.BOOLEAN, BasicType.BOOLEAN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == 1 && right == 1);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("AND", "", "", "GR2", "GR1"));
        }
    },
    
    EQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left == right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            final int label = l_gen.next();
            code.add(new Casl2Instruction("CPL", "", "; v =", "GR2", "GR1"));
            code.add(new Casl2Instruction("JZE", "",      "", "Z" + label));
            code.add(new Casl2Instruction("XOR", "",      "", "GR2", "GR2"));
            code.add(new Casl2Instruction("JUMP", "",      "", "Q" + label));
            code.add(new Casl2Instruction("LAD", "Z" + label, "", "GR2", "1"));
            code.add(new Casl2Instruction("NOP", "Q" + label, "; ^ ="));
        }
    },
    NOTEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left != right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            final int label = l_gen.next();
            code.add(new Casl2Instruction("CPL", "", "; v <>", "GR2", "GR1"));
            code.add(new Casl2Instruction("JNZ", "",       "", "Z" + label));
            code.add(new Casl2Instruction("XOR", "",       "", "GR2", "GR2"));
            code.add(new Casl2Instruction("JUMP", "",       "", "Q" + label));
            code.add(new Casl2Instruction("LAD", "Z" + label, "", "GR2", "1"));
            code.add(new Casl2Instruction("NOP", "Q" + label, "; ^ <>"));
        }
    },
    LESS(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left < right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("SUBA", "", "; <", "GR1", "GR2"));
            code.add(new Casl2Instruction("LD", "",      "", "GR2", "GR1"));
            code.add(new Casl2Instruction("SRL", "",     "", "GR2", "15"));
        }
    },
    LESSEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left <= right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("SUBA", "", "; <=", "GR2", "GR1"));
            code.add(new Casl2Instruction("SRL", "",      "", "GR2", "15"));
            code.add(new Casl2Instruction("XOR", "",      "", "GR2", "=1"));
        }
    },
    GREAT(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left > right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("SUBA", "", "; >", "GR2", "GR1"));
            code.add(new Casl2Instruction("SRL", "",     "", "GR2", "15"));
        }
    },
    GREATEQUAL(BasicType.UNKNOWN, BasicType.UNKNOWN, BasicType.BOOLEAN) {
        @Override
        public IConstant eval(int left, int right)
        {
            return new BooleanLiteral(left >= right);
        }
        
        @Override
        public void compile(List<Casl2Instruction> code, LabelGenerator l_gen)
        {
            code.add(new Casl2Instruction("SUBA", "", "; >=", "GR1", "GR2"));
            code.add(new Casl2Instruction("LD", "",       "", "GR2", "GR1"));
            code.add(new Casl2Instruction("SRL", "",      "", "GR2", "15"));
            code.add(new Casl2Instruction("XOR", "",      "", "GR2", "=1"));
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
    
    public abstract void compile(List<Casl2Instruction> code, LabelGenerator l_gen); // left->GR1,right->GR2
}
