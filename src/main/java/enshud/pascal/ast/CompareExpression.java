package enshud.pascal.ast;

import java.util.Objects;

import enshud.pascal.type.IType;
import enshud.pascal.type.RegularType;
import enshud.pascal.type.StringType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;

public class CompareExpression extends Expression
{
    final CompareOperator  op;
    final SimpleExpression right;

    public CompareExpression(SimpleExpression left, CompareOperator op, SimpleExpression right)
    {
        super(left);
        this.op = Objects.requireNonNull(op);
        this.right = Objects.requireNonNull(right);
        this.type = RegularType.BOOLEAN;
    }

    public SimpleExpression getRight()
    {
        return right;
    }

    public CompareOperator getOp()
    {
        return op;
    }

    @Override
    public void retype(IType new_type)
    {
        super.retype(new_type);
        right.retype(new_type);
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        IType left_type = getLeft().check(proc, checker);
        IType right_type = getRight().check(proc, checker);

        if( left_type.isUnknown() && !right_type.isUnknown() )
        {
            left_type = right_type;
            getLeft().retype(right_type);
        }
        else if( !left_type.isUnknown() && right_type.isUnknown() )
        {
            right_type = left_type;
            getRight().retype(left_type);
        }
        else if( left_type.isUnknown() && right_type.isUnknown() )
        {
        	return type;
        }
        else
        {
            if( left_type instanceof StringType && StringType.isCharOrCharArray(right_type) )
            {
                left_type = right_type;
                getLeft().retype(right_type);
            }
            if( right_type instanceof StringType && StringType.isCharOrCharArray(left_type) )
            {
                right_type = left_type;
                getRight().retype(left_type);
            }
        }

        if( !left_type.equals(right_type) )
        {
            checker.addErrorMessage(
                proc, this,
                "incompatible type: in compare operation, left is " + left_type + ". right is " + right_type + "."
            );
        }
        return type;
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        getLeft().compile(codebuilder, proc, l_gen);
        codebuilder.append(" PUSH 0,GR2").append(System.lineSeparator());

        getRight().compile(codebuilder, proc, l_gen);
        codebuilder.append(" POP GR1").append(System.lineSeparator());

        switch(getOp())
        {
        case EQUAL:
        {
            String label = l_gen.next();
            codebuilder.append(" CPL GR2,GR1   ; v =").append(System.lineSeparator());
            codebuilder.append(" JZE ").append("Z").append(label).append(System.lineSeparator());
            codebuilder.append(" XOR GR2,GR2").append(System.lineSeparator());
            codebuilder.append(" JUMP Q").append(label).append(System.lineSeparator());
            codebuilder.append("Z").append(label).append(" LAD GR2,1").append(System.lineSeparator());
            codebuilder.append("Q").append(label).append(" NOP    ; ^ =").append(System.lineSeparator());
            break;
        }
        case NOTEQUAL:
        {
            String label = l_gen.next();
            codebuilder.append(" CPL GR2,GR1   ; v <>").append(System.lineSeparator());
            codebuilder.append(" JZE ").append("Z").append(label).append(System.lineSeparator());
            codebuilder.append(" LAD GR2,1").append(System.lineSeparator());
            codebuilder.append(" JUMP Q").append(label).append(System.lineSeparator());
            codebuilder.append("Z").append(label).append(" XOR GR2,GR2").append(System.lineSeparator());
            codebuilder.append("Q").append(label).append(" NOP    ; ^ <>").append(System.lineSeparator());
            break;
        }
        case LESS:
            codebuilder.append(" SUBA GR1,GR2; <").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1  ;").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
            break;
        case LESSEQUAL:
            codebuilder.append(" SUBA GR2,GR1; <=").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
            codebuilder.append(" XOR GR2,=1  ;").append(System.lineSeparator());
            break;

        case GREAT:
            codebuilder.append(" SUBA GR2,GR1; >").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
            break;
        case GREATEQUAL:
            codebuilder.append(" SUBA GR1,GR2; >=").append(System.lineSeparator());
            codebuilder.append(" LD GR2,GR1  ;").append(System.lineSeparator());
            codebuilder.append(" SRL GR2,15  ;").append(System.lineSeparator());
            codebuilder.append(" XOR GR2,=1  ;").append(System.lineSeparator());
            break;
        }
    }

    @Override
    public void printBodyln(String indent)
    {
        left.println(indent + " |");
        op.println(indent + " |");
        right.println(indent + "  ");
    }
}
