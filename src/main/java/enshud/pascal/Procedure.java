package enshud.pascal;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

import enshud.pascal.ast.IAcceptable;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.declaration.LocalDeclaration;
import enshud.pascal.ast.declaration.ParameterDeclaration;
import enshud.pascal.ast.declaration.ProcedureDeclaration;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.CheckVisitor;
import enshud.s3.checker.Checker;
import enshud.s4.compiler.OptimizeVisitor;


public class Procedure extends ProcedureBase implements IAcceptable
{
    private static final boolean OPTIMIZE = true;
    //private static final boolean OPTIMIZE = false;
    
    private Procedure(Checker checker, ProcedureDeclaration prg, Procedure parent, String proc_id)
    {
        setParent(parent);
        setName(prg.getName().toString());
        setId(proc_id);
        
        checkParams(prg.getParams(), checker);
        checkVarDecls(prg.getVars(), checker);
        
        createSubProc(checker, prg, proc_id);
        
        setBody(prg.getBody());
        getBody().accept(new CheckVisitor(checker), this);
    }
    
    private void createSubProc(Checker checker, ProcedureDeclaration prg, String proc_id)
    {
        IntStream.range(0, prg.getSubProcs().size()).forEachOrdered(
            i -> {
                checkIfSubProcAlreadyDefined(checker, prg.getSubProcs().get(i));
                final Procedure sub = new Procedure(checker, prg.getSubProcs().get(i), this, proc_id + i);
                getChildren().add(sub);
            }
        );
    }
    
    public static Procedure create(Checker checker, ProcedureDeclaration prg)
    {
        final Procedure p = new Procedure(checker, prg, null, "P0");
        p.optimize();
        return p;
    }
    
    public Optional<QualifiedVariable> findParam(String name)
    {
        final Optional<QualifiedVariable> param = getParams().get(name);
        return (param.isPresent() || isRoot())
                ? param
                : getParent().findParam(name);
    }
    
    public List<QualifiedVariable> searchForParamFuzzy(String name)
    {
        return getParams().searchForFuzzy(name);
    }
    
    
    public Optional<QualifiedVariable> findLocal(String name)
    {
        Optional<QualifiedVariable> v = getLocals().get(name);
        return v.isPresent()? v
                : isRoot()? Optional.empty()
                        : getParent().findLocal(name);
    }
    
    public List<QualifiedVariable> searchForLocalFuzzy(String name)
    {
        final List<QualifiedVariable> var = getLocals().searchForFuzzy(name);
        if (!isRoot())
        {
            var.addAll(getParent().searchForLocalFuzzy(name));
        }
        return var;
    }
    
    
    public Optional<Procedure> findSubProc(String name)
    {
        if (!isRoot() && getName().equals(name))
        {
            return Optional.of(this);
        }
        
        final Optional<Procedure> p = getChildren()
            .stream()
            .filter(sub -> sub.getName().equals(name))
            .findFirst();
        
        return (p.isPresent() || isRoot())
                ? p
                : getParent().findSubProc(name);
    }
    
    public List<Procedure> searchForSubProcFuzzy(String name)
    {
        List<Procedure> l = new ArrayList<>();
        if (!isRoot() && Checker.isSimilar(getName(), name))
        {
            l.add(this);
        }
        
        getChildren()
            .stream()
            .filter(sub -> Checker.isSimilar(sub.getName(), name))
            .forEach(l::add);
        
        if (!isRoot())
        {
            l.addAll(getParent().searchForSubProcFuzzy(name));
        }
        
        return l;
    }
    
    private void checkParams(List<ParameterDeclaration> params, Checker checker)
    {
        params.forEach(
            param -> {
                param.getNames().forEach(
                    id -> {
                        final String n = id.toString();
                        final BasicType t = param.getType();
                        if (getName().equals(n))
                        {
                            checker.addErrorMessage(this, id, "param '" + n + "' conflicts it's proc.");
                        }
                        else
                        {
                            getParams().add(n, t, this);
                        }
                    }
                );
            }
        );
    }
    
    private void checkVarDecls(List<LocalDeclaration> vars, Checker checker)
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
                        if (getParams().exists(n))
                        {
                            checker.addErrorMessage(this, id, "'" + n + "' is already defined as parameter.");
                        }
                        else if (getLocals().exists(n))
                        {
                            checker.addErrorMessage(this, id, "'" + n + "' is already defined as local variable.");
                        }
                        else
                        {
                            getLocals().add(n, decl.getType(), this);
                        }
                    }
                );
            }
        );
    }
    
    private void checkArrayType(Checker checker, LocalDeclaration decl)
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
        final String err = checkProcError(sub_decl);
        if (err != null)
        {
            checker.addErrorMessage(
                this, sub_decl, "proc '" + sub_decl.toString() + "' is already defined as " + err + "."
            );
        }
    }
    
    private String checkProcError(ProcedureDeclaration sub_decl)
    {
        final String sub_n = sub_decl.toString();
        if (getName().equals(sub_decl))
        {
            return "parent proc";
        }
        else if (getParams().exists(sub_n))
        {
            return "parameter";
        }
        else if (getLocals().exists(sub_n))
        {
            return "local variable";
        }
        else if (getChildren().stream().map(c -> c.getName()).anyMatch(sub_n::equals))
        {
            return "sibling proc";
        }
        else
        {
            return null;
        }
    }
    
    public void optimize()
    {
        if (OPTIMIZE)
        {
            accept(new OptimizeVisitor(), this);
        }
    }
    
    @Override
    public <T, U> T accept(IVisitor<T, U> visitor, U option)
    {
        return visitor.visit(this, option);
    }
}

