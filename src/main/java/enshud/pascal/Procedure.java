package enshud.pascal;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import enshud.pascal.ast.declaration.Parameter;
import enshud.pascal.ast.declaration.ProcedureDeclaration;
import enshud.pascal.ast.statement.CompoundStatement;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.Casl2Code;
import enshud.s4.compiler.LabelGenerator;


public class Procedure
{
    private Procedure                  parent;
    private final String               name;
    private final String               proc_id;                                 // for
                                                                                // label
    private final List<Procedure>      children    = new ArrayList<>();
    
    private final VariableDeclarations param_decls = new VariableDeclarations();
    private final VariableDeclarations var_decls   = new VariableDeclarations();
    
    private final CompoundStatement    body;
    
    private static final boolean       OPTIMIZE    = true;
    
    private Procedure(Checker checker, ProcedureDeclaration prg, Procedure parent, String proc_id)
    {
        this.parent = parent;
        name = prg.getName().toString();
        this.proc_id = proc_id;
        
        checkParams(prg.getParams(), checker);
        checkVarDecls(prg.getVars(), checker);
        
        createSubProc(checker, prg, proc_id);
        
        body = prg.getBody();
        body.check(this, checker);
    }
    
    private void createSubProc(Checker checker, ProcedureDeclaration prg, String proc_id)
    {
        int i = 0;
        for (final ProcedureDeclaration sub_decl: prg.getSubProcs())
        {
            checkIfSubProcAlreadyDefined(checker, sub_decl);
            final Procedure sub = new Procedure(checker, sub_decl, this, proc_id + i);
            children.add(sub);
            ++i;
        }
    }
    
    public static Procedure create(Checker checker, ProcedureDeclaration prg)
    {
        final Procedure p = new Procedure(checker, prg, null, "P0");
        if (OPTIMIZE)
        {
            p.precompute();
        }
        return p;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getId()
    {
        return proc_id;
    }
    
    public String getQualifiedName()
    {
        return isRoot()
                ? getName()
                : getParent().getQualifiedName() + "." + getName();
    }
    
    private Procedure getParent()
    {
        return parent;
    }
    
    public int getDepth()
    {
        return isRoot()? 0: 1 + getParent().getDepth();
    }
    
    public boolean isRoot()
    {
        return getParent() == null;
    }
    
    
    public Optional<Variable> getParam(String name)
    {
        final Optional<Variable> param = param_decls.get(name);
        return (param.isPresent() || isRoot())
                ? param
                : getParent().getParam(name);
    }
    
    public List<Variable> searchForParamFuzzy(String name)
    {
        return param_decls.searchForFuzzy(name);
    }
    
    public IType getParamType(int index)
    {
        return param_decls.get(index).getType();
    }
    
    public int getParamLength()
    {
        return param_decls.length();
    }
    
    
    public Optional<Variable> getLocalVar(String name)
    {
        return var_decls.get(name);
    }
    
    public Optional<Variable> getOuterVar(String name)
    {
        if(isRoot())
        {
            return Optional.empty();
        }
        Optional<Variable> v = getParent().getLocalVar(name);
        return v.isPresent()
                ? v
                : getParent().getOuterVar(name);
    }
    
    public Optional<Variable> getVar(String name)
    {
        final Optional<Variable> var = getLocalVar(name);
        return (var.isPresent() || isRoot())
                ? var
                : getOuterVar(name);
    }
    
    public IType getVarType(String name)
    {
        return getVar(name)
            .map(v -> v.getType())
            .orElse(UnknownType.UNKNOWN);
    }
    
    public List<Variable> searchForVarFuzzy(String name)
    {
        final List<Variable> var = var_decls.searchForFuzzy(name);
        if (!isRoot())
        {
            var.addAll(getParent().searchForVarFuzzy(name));
        }
        return var;
    }
    
    
    public Optional<Procedure> getSubProc(String name)
    {
        if (getName().equals(name))
        {
            return Optional.of(this);
        }
        
        final Optional<Procedure> p = children
            .stream()
            .filter(sub -> sub.getName().equals(name))
            .findFirst();
        
        return (p.isPresent() || isRoot())
                ? p
                : getParent().getSubProc(name);
    }
    
    public List<Procedure> getSubProcFuzzy(String name)
    {
        List<Procedure> l = new ArrayList<>();
        if (Checker.isSimilar(getName(), name))
        {
            l.add(this);
        }
        
        children
            .stream()
            .filter(sub -> Checker.isSimilar(sub.getName(), name))
            .forEach(l::add);
        
        if (!isRoot())
        {
            l.addAll(getParent().getSubProcFuzzy(name));
        }
        
        return l;
    }
    
    private void checkParams(List<Parameter> params, Checker checker)
    {
        params.forEach(
            p -> {
                p.getNames().forEach(
                    id -> {
                        final String n = id.toString();
                        final BasicType t = p.getType();
                        if (getName().equals(n))
                        {
                            checker.addErrorMessage(this, id, "'" + n + "' is already defined as proc.");
                        }
                        else
                        {
                            param_decls.add(n, t, this);
                        }
                    }
                );
            }
        );
    }
    
    private void checkVarDecls(List<enshud.pascal.ast.declaration.VariableDeclaration> vars, Checker checker)
    {
        vars.forEach(
            decl -> {
                if (decl.getType() instanceof ArrayType)
                {
                    checkArrayType(checker, decl);
                }
                
                decl.getNames().forEach(
                    id -> {
                        final String n = id.toString();
                        if (param_decls.exists(n))
                        {
                            checker.addErrorMessage(this, id, "'" + n + "' is already defined as parameter.");
                        }
                        else if (var_decls.exists(n))
                        {
                            checker.addErrorMessage(this, id, "'" + n + "' is already defined as local variable.");
                        }
                        else
                        {
                            var_decls.add(n, decl.getType(), this);
                        }
                    }
                );
            }
        );
    }
    
    private void checkArrayType(Checker checker, enshud.pascal.ast.declaration.VariableDeclaration decl)
    {
        final ArrayType type = (ArrayType)decl.getType();
        
        if (type.getMin() < -32768 || type.getMin() > 32767)
        {
            checker.addErrorMessage(
                this, decl, "min index is out of bounds. " + "min(" + type.getMin() + ")."
            );
        }
        
        if (type.getMax() < -32768 || type.getMax() > 32767)
        {
            checker.addErrorMessage(
                this, decl, "max index is out of bounds. " + "max(" + type.getMax() + ")."
            );
        }
        
        if (type.getMin() > type.getMax())
        {
            checker.addErrorMessage(
                this, decl, "min index is larger than max index in array declaration. " + "min(" + type.getMin()
                        + ") > max(" + type.getMax() + ")."
            );
        }
    }
    
    private void checkIfSubProcAlreadyDefined(Checker checker, ProcedureDeclaration sub_decl)
    {
        final String sub_n = sub_decl.toString();
        final String err = getName().equals(sub_decl)
                ? "parent proc"
                : param_decls.exists(sub_n)
                        ? "parameter"
                        : var_decls.exists(sub_n)
                                ? "local variable"
                                : (children.stream().map(c -> c.getName()).anyMatch(sub_n::equals))
                                        ? "sibling proc"
                                        : null;
        if (err != null)
        {
            checker.addErrorMessage(
                this, sub_decl, "proc '" + sub_decl.toString() + "' is already defined as " + err + "."
            );
        }
    }
    
    
    public void precompute()
    {
        body.precompute(this);
        children.forEach(sub -> sub.precompute());
    }
    
    
    public Casl2Code compile(Casl2Code code)
    {
        compileProc(code);
        return code;
    }
    
    private void compileProc(Casl2Code code)
    {
        if (isRoot())
        {
            code.add("START", "PROGRAM", "; proc " + getQualifiedName());
            code.add("XOR", "", "; buffer length", "GR6", "GR6");
            code.add("LAD", "", "; buffer address", "GR7", "BUF");
            code.add("PUSH", proc_id, "; save parent's frame pointer", "0", "GR5");
        }
        else
        {
            code.add("START", proc_id, "; proc " + getQualifiedName());
            code.add("PUSH", "", "; save parent's frame pointer", "0", "GR5");
        }
        
        code.add("LAD", "", "; set my frame pointer", "GR5", "1", "GR8");
        
        if (var_decls.length() > 0)
        {
            code.add("LAD", "", "; reserve local variables", "GR8", "" + (-var_decls.getAllSize()), "GR8");
        }
        
        body.compile(code, this, new LabelGenerator());
        
        compileReturn(code);
        if (isRoot())
        {
            code.add("DS", "BUF", "; buffer for write", "256");
        }
        
        code.add("END", "", "; proc " + getQualifiedName());
        children.forEach(sub -> sub.compileProc(code));
    }
    
    public void compileReturn(Casl2Code code)
    {
        code.add("LD", "", "; point to return address", "GR8", "GR5");
        code.add("LD", "", "; restore parent's frame pointer", "GR5", "-1", "GR5");
        code.add("RET", "", "");
    }
}

