package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.Variable;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class IndexedVariable implements IVariable, ILiteral
{
    private final Identifier name;
    private final ITyped     index;
    private IType            type;
    
    public IndexedVariable(Identifier name, ITyped index)
    {
        this.name = Objects.requireNonNull(name);
        this.index = Objects.requireNonNull(index);
        type = UnknownType.UNKNOWN;
    }
    
    @Override
    public Identifier getName()
    {
        return name;
    }
    
    public ITyped getIndex()
    {
        return index;
    }
    
    @Override
    public IType getType()
    {
        return type.getBasicType();
    }
    
    @Override
    public int getLine()
    {
        return name.getLine();
    }
    
    @Override
    public int getColumn()
    {
        return name.getColumn();
    }
    
    @Override
    public void retype(IType new_type)
    {
        if (getType().isUnknown())
        {
            type = new_type;
        }
    }
    
    @Override
    public IType check(Procedure proc, Checker checker)
    {
        final String nm = getName().toString();
        type = proc.getVarType(nm);
        
        if (type == UnknownType.UNKNOWN)
        {
            List<Variable> vs = proc.searchForVarFuzzy(nm);
            checker.addErrorMessage(
                proc, getName(),
                "variable '" + nm + "' is not defined."
                        + (vs.isEmpty()? "": (" did you mean variable " + vs + "?"))
            );
        }
        else if (type.isBasicType())
        {
            checker.addErrorMessage(
                proc, this, "incompatible type: non-array type variable '" + nm + "' cannot have index."
            );
        }
        
        checkIndex(proc, checker);
        return type.getBasicType();
    }
    
    private void checkIndex(Procedure proc, Checker checker)
    {
        final IType idx_type = getIndex().check(proc, checker);
        if (idx_type.isUnknown())
        {
            getIndex().retype(BasicType.INTEGER);
        }
        
        if (!idx_type.equals(BasicType.INTEGER))
        {
            checker.addErrorMessage(
                proc, getIndex(),
                "incompatible type: cannot use " + idx_type + " type as index of '" + getName() + "'. must be INTEGER."
            );
        }
    }
    
    @Override
    public IConstant preeval(Procedure proc)
    {
        return null;
    }
    
    @Override
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        compileForData(code, proc, l_gen);
    }
    
    @Override
    public void compileForData(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl(code, proc, l_gen);
        code.add(new Casl2Instruction("LD", "", "", "GR2", "0", "GR1"));
    }
    
    @Override
    public void compileForAddr(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl(code, proc, l_gen);
        code.add(new Casl2Instruction("LD", "", "", "GR2", "GR1"));
    }
    
    private void compileImpl(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        // code.append("; idx v").append(System.lineSeparator());
        getIndex().compile(code, proc, l_gen);
        
        final String nm = getName().toString();
        final Optional<Variable> var = proc.getLocalVar(nm);
        if (var.isPresent())
        {
            compileImpl2(code, var.get(), "GR5");
        }
        else
        {
            compileImpl2(code, proc.getGlobalVar(nm).get(), "GR4");
        }
        code.add(new Casl2Instruction("ADDL", "", "; add index", "GR1", "GR2"));
    }
    
    private void compileImpl2(List<Casl2Instruction> code, Variable var, String gr)
    {
        final int align = var.getAlignment();
        final int max = ((ArrayType)var.getType()).getMax();
        code.add(new Casl2Instruction("LAD", "",  "", "GR1", "" + (-align - 2 - max), gr));
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
    
    @Override
    public String toString()
    {
        return "" + name;
    }
    
    @Override
    public void printBodyln(String indent)
    {
        index.println(indent + "  ");
    }
}

