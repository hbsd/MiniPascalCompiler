package enshud.pascal.ast;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.Variable;
import enshud.s4.compiler.Casl2Instruction;
import enshud.s4.compiler.LabelGenerator;


public class PureVariable implements IVariable, ILiteral
{
    private final Identifier name;
    private IType            type;
    
    public PureVariable(Identifier name)
    {
        this.name = Objects.requireNonNull(name);
        type = UnknownType.UNKNOWN;
    }
    
    @Override
    public Identifier getName()
    {
        return name;
    }
    
    @Override
    public IType getType()
    {
        return type;
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
            final Optional<Variable> param = proc.getParam(nm);
            if (param.isPresent())
            {
                type = param.get().getType();
            }
            else
            {
                checkFuzzy(proc, checker);
            }
        }
        
        return type;
    }
    
    private void checkFuzzy(Procedure proc, Checker checker)
    {
        final String nm = getName().toString();
        List<Variable> vs = proc.searchForVarFuzzy(nm);
        
        String n = null;
        if (!vs.isEmpty())
        {
            n = vs.toString();
        }
        else
        {
            List<Variable> ps = proc.searchForParamFuzzy(nm);
            if (!ps.isEmpty())
            {
                n = ps.toString();
            }
        }
        
        checker.addErrorMessage(
            proc, getName(),
            "variable '" + nm + "' is not defined."
                    + ((n == null)? "": (" did you mean variable " + n + "?"))
        );
    }
    
    @Override
    public IConstant preeval(Procedure proc)
    {
        return null;
    }
    
    @Override
    public void compile(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        if (getType().isBasicType())
        {
            compileForData(code, proc, l_gen);
        }
        else
        {
            compileForAddr(code, proc, l_gen);
        }
    }
    
    @Override
    public void compileForData(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl("LD", code, proc, l_gen);
    }
    
    @Override
    public void compileForAddr(List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        compileImpl("LAD", code, proc, l_gen);
    }
    
    private void compileImpl(String inst, List<Casl2Instruction> code, Procedure proc, LabelGenerator l_gen)
    {
        Optional<Variable> var = proc.getLocalVar(getName().toString());
        if (var.isPresent())
        {
            compileForVariable(code, var.get(), inst, "GR5");
        }
        else
        {
            final Optional<Variable> param = proc.getParam(getName().toString());
            if (param.isPresent())
            {
                final int align = param.get().getAlignment();
                code.add(new Casl2Instruction(inst, "", "", "GR2", "" + (align + 1), "GR5"));
            }
            else
            {
                var = proc.getGlobalVar(getName().toString());
                compileForVariable(code, var.get(), inst, "GR4");
            }
        }
    }
    
    private void compileForVariable(List<Casl2Instruction> code, Variable var, String inst, String gr)
    {
        final int align = var.getAlignment();
        
        if (var.getType().isArrayType())
        {
            final int len = ((ArrayType)var.getType()).getSize();
            code.add(new Casl2Instruction(inst, "", "", "GR2", "" + (-align - 1 - len), gr));
            // array length
            if (inst.equals("LAD"))
            {
                code.add(new Casl2Instruction("LAD", "", "", "GR1", "" + var.getType().getSize()));
            }
        }
        else
        {
            code.add(new Casl2Instruction(inst, "", "", "GR2", "" + (-align - 2), gr));
        }
    }
    
    @Override
    public void printHead(String indent, String msg)
    {
        ILiteral.super.printHead(indent, msg);
    }
    
    @Override
    public String toString()
    {
        return name.toString();
    }
}

