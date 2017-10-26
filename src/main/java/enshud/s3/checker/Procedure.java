package enshud.s3.checker;

import java.util.List;
import java.util.Objects;

import enshud.pascal.ast.*;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.UnknownType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.ParameterDeclaration.Param;

import java.util.ArrayList;

import enshud.s3.checker.VariableDeclaration.Variable;
import enshud.s4.compiler.LabelGenerator;


public class Procedure
{
    final Procedure            parent;
    final String               name;
    final List<Procedure>      children    = new ArrayList<>();
    
    final ParameterDeclaration param_decls = new ParameterDeclaration();
    final VariableDeclaration  var_decls   = new VariableDeclaration();
    
    final StatementList        body;
    
    private static final boolean OPTIMIZE = true;
    
    public Procedure(Checker checker, Program prg)
    {
        parent = null;
        name = prg.getName().toString();
        
        Objects.requireNonNull(checker);
        checkVarDecls(prg.getVars().getList(), checker);
        
        for (final SubProgramDeclaration sub_decl: prg.getSubProcs().getList())
        {
            new Procedure(checker, sub_decl, this);
        }
        body = prg.getBody();
        body.check(this, checker);
        if(OPTIMIZE)
            precompute();
    }
    
    private Procedure(Checker checker, SubProgramDeclaration sub, Procedure parent)
    {
        this.parent = Objects.requireNonNull(parent);
        parent.children.add(this);
        name = sub.getName().toString();
        
        Objects.requireNonNull(checker);
        checkParams(sub.getParams().getList(), checker);
        checkVarDecls(sub.getVars().getList(), checker);
        body = sub.getBody();
        body.check(this, checker);
        if(OPTIMIZE)
            precompute();
    }
    
    public String getName()
    {
        return name;
    }
    
    public Param getParam(String name)
    {
        return param_decls.get(name);
    }
    
    public int getParamIndex(String name)
    {
        return param_decls.getIndex(name);
    }
    
    public BasicType getParamType(int index)
    {
        return param_decls.get(index).type;
    }
    
    public int getParamLength()
    {
        return param_decls.length();
    }
    
    
    public boolean existsVar(String name)
    {
        return getVar(name) != null;
    }
    
    public Variable getVar(String name)
    {
        final Variable var = var_decls.get(name);
        if (var == null && parent != null)
        {
            return parent.getVar(name);
        }
        else
        {
            return var;
        }
    }
    
    public IType getVarType(String name)
    {
        final Variable v = getVar(name);
        return v != null? v.type: UnknownType.UNKNOWN;
    }
    
    public Variable getLocalVar(String name)
    {
        return var_decls.get(name);
    }
    
    public Variable getGlobalVar(String name)
    {
        Procedure p = this;
        while (p.parent != null)
        {
            p = p.parent;
        }
        return p.getLocalVar(name);
    }
    
    
    public boolean existsSubProc(String name)
    {
        return getSubProc(name) != null;
    }
    
    public Procedure getSubProc(String name)
    {
        for (final Procedure sub: children)
        {
            if (sub.getName().equals(name))
            {
                return sub;
            }
        }
        return parent == null? null: parent.getSubProc(name);
    }
    
    public int getSubProcIndex(String name)
    {
        int i = 1;
        for (final Procedure sub: children)
        {
            if (sub.getName().equals(name))
            {
                return i;
            }
            ++i;
        }
        return parent == null? -1: parent.getSubProcIndex(name);
    }
    
    private void checkParams(List<Parameter> params, Checker checker)
    {
        // subprocs.add(scope);
        for (final Parameter p: params)
        {
            for (final Identifier id: p.getNames())
            {
                final String n = id.toString();
                final BasicType t = p.getType();
                if (var_decls.exists(n))
                {
                    checker.addErrorMessage(this, id, "'" + n + "' is already defined.");
                }
                else
                {
                    param_decls.add(n, t);
                    // subprocs.add(scope, name, pascal.type);
                }
            }
        }
    }
    
    private void checkVarDecls(List<enshud.pascal.ast.VariableDeclaration> vars, Checker checker)
    {
        for (final enshud.pascal.ast.VariableDeclaration decl: vars)
        {
            if (decl.getType() instanceof ArrayType)
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
            
            for (final Identifier id: decl.getNames())
            {
                final String n = id.toString();
                if (var_decls.exists(n) || param_decls.exists(n))
                {
                    checker.addErrorMessage(this, id, "'" + n + "' is already defined.");
                }
                else
                {
                    var_decls.add(n, decl.getType());
                }
            }
        }
    }
    
    public void precompute()
    {
        body.precompute(this, null);
        for(Procedure sub: children)
        {
            sub.precompute();
        }
    }
    
    public void compile(StringBuilder codebuilder)
    {
        codebuilder.append("PMAIN").append(" START").append(System.lineSeparator());
        codebuilder.append(" XOR GR6,GR6").append(System.lineSeparator());
        codebuilder.append(" LAD GR7,BUF").append(System.lineSeparator());
        
        // save parent frame
        codebuilder.append(" PUSH 0,GR5; save parent's frame pointer").append(System.lineSeparator());
        codebuilder.append(" LAD GR4,1,GR8; set program frame pointer").append(System.lineSeparator());
        codebuilder.append(" LD GR5,GR4; set my frame pointer").append(System.lineSeparator());
        
        final int locals = var_decls.getAllSize() - param_decls.length();
        if (locals > 0)
        {
            codebuilder.append(" LAD GR8,").append(-locals).append(",GR8; reserve local variables").append(
                System.lineSeparator()
            );
        }
        
        codebuilder.append("; program's body begins").append(System.lineSeparator());
        body.compile(codebuilder, this, new LabelGenerator());
        
        codebuilder.append(" LD GR8,GR5; point to return address").append(System.lineSeparator());
        codebuilder.append(" LD GR5,-1,GR5; restore parent's frame pointer").append(System.lineSeparator());
        codebuilder.append(" RET").append(System.lineSeparator());
        codebuilder.append("BUF DS 256").append(System.lineSeparator());
        codebuilder.append(" END");
        
        int i = 1;
        for (final Procedure c: children)
        {
            codebuilder.append(System.lineSeparator());
            c.compileSubProgram(codebuilder, i);
            ++i;
        }
    }
    
    private void compileSubProgram(StringBuilder codebuilder, int proc_idx)
    {
        codebuilder.append("PSUB").append(proc_idx).append(" START").append(System.lineSeparator());
        
        // save parent frame
        codebuilder.append(" PUSH 0,GR5; save parent's frame pointer").append(System.lineSeparator());
        codebuilder.append(" LAD GR5,1,GR8; set my frame pointer").append(System.lineSeparator());
        
        // copy arguments
        if (param_decls.length() > 0)
        {
            codebuilder.append("; copy arguments").append(System.lineSeparator());
            for (int i = param_decls.length(); i >= 0; --i)
            {
                codebuilder.append(" LD GR3,").append(i).append(",GR7").append(System.lineSeparator());
                codebuilder.append(" PUSH 0,GR3").append(System.lineSeparator());
            }
        }
        
        final int locals = var_decls.getAllSize() - param_decls.length();
        if (locals > 0)
        {
            codebuilder.append(" LAD GR8,").append(-locals).append(",GR8; reserve local variables").append(
                System.lineSeparator()
            );
        }
        
        codebuilder.append("; procedure's body begins").append(System.lineSeparator());
        body.compile(codebuilder, this, new LabelGenerator());
        
        codebuilder.append(" LD GR8,GR5; point to return address").append(System.lineSeparator());
        codebuilder.append(" LD GR5,-1,GR5; restore parent's frame pointer").append(System.lineSeparator());
        codebuilder.append(" RET").append(System.lineSeparator());
        codebuilder.append(" END");
    }
}

