package enshud.s3.checker;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import enshud.pascal.Procedure;
import enshud.pascal.QualifiedVariable;
import enshud.pascal.ast.IVisitor;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;
import enshud.pascal.type.BasicType;
import enshud.pascal.type.IType;
import enshud.pascal.type.StringType;
import enshud.pascal.type.UnknownType;


public class CheckVisitor implements IVisitor<IType, Procedure>
{
    private final Checker checker;
    
    public CheckVisitor(Checker checker)
    {
        this.checker = checker;
    }
    
    @Override
    public IType visit(Procedure node, Procedure proc)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public IType visit(BooleanLiteral node, Procedure proc)
    {
        return node.getType();
    }
    
    @Override
    public IType visit(CharLiteral node, Procedure option)
    {
        return node.getType();
    }
    
    @Override
    public IType visit(IndexedVariable node, Procedure proc)
    {
        final String nm = node.getName().toString();
        final Optional<QualifiedVariable> var = proc.findLocal(nm);
        node.setArrayType(
            var.map(v -> v.getType())
                .orElse(UnknownType.UNKNOWN)
        );
        
        if (!var.isPresent())
        {
            final List<QualifiedVariable> vs = proc.searchForLocalFuzzy(nm);
            final String msg = vs.isEmpty()? "": (" did you mean variable " + vs + "?");
            checker.addErrorMessage(
                proc, node.getName(),
                "variable '" + nm + "' is not defined." + msg
            );
        }
        else if (node.getArrayType().isBasicType())
        {
            checker.addErrorMessage(
                proc, node, "incompatible type: non-array type variable '" + nm + "' cannot have index."
            );
        }
        else
        {
            node.setVar(var.get());
        }
        
        checkIndex(node, proc);
        return node.getType();
    }
    
    private void checkIndex(IndexedVariable node, Procedure proc)
    {
        final IType idx_type = node.getIndex().accept(this, proc);
        if (!idx_type.isUnknown() && !idx_type.equals(BasicType.INTEGER))
        {
            checker.addErrorMessage(
                proc, node.getIndex(),
                "incompatible type: cannot use " + idx_type + " type as index of '" + node.getName()
                        + "'. must be INTEGER."
            );
        }
    }
    
    @Override
    public IType visit(InfixOperation node, Procedure proc)
    {
        final IType left_type = node.getLeft().accept(this, proc);
        final IType right_type = node.getRight().accept(this, proc);
        
        node.setType(node.getOp().checkType(proc, checker, node.getOpToken(), left_type, right_type));
        return node.getType();
    }
    
    @Override
    public IType visit(IntegerLiteral node, Procedure proc)
    {
        return node.getType();
    }
    
    @Override
    public IType visit(PrefixOperation node, Procedure proc)
    {
        final IType t = node.getOperand().accept(this, proc);
        node.setType(node.getOp().checkType(proc, checker, node.getOpToken(), t));
        return node.getType();
    }
    
    @Override
    public IType visit(PureVariable node, Procedure proc)
    {
        final String nm = node.getName().toString();
        
        final Optional<QualifiedVariable> param = proc.findParam(nm);
        
        if (param.isPresent())
        {
            node.setVar(param.get());
            node.setIsParam(true);
            node.setType(param.get().getType());
        }
        else
        {
            final Optional<QualifiedVariable> var = proc.findLocal(nm);
            node.setType(
                var.map(v -> v.getType())
                    .orElse(UnknownType.UNKNOWN)
            );
            if (var.isPresent())
            {
                node.setVar(var.get());
                node.setIsParam(false);
            }
            else
            {
                checkFuzzy(node, proc);
            }
        }
        
        return node.getType();
    }
    
    private void checkFuzzy(PureVariable node, Procedure proc)
    {
        final String nm = node.getName().toString();
        final List<QualifiedVariable> vs = proc.searchForParamFuzzy(nm);
        vs.addAll(proc.searchForLocalFuzzy(nm));
        
        final String msg = vs.isEmpty()? "": (" did you mean variable " + vs + "?");
        checker.addErrorMessage(
            proc, node.getName(),
            "variable '" + nm + "' is not defined." + msg
        );
    }
    
    @Override
    public IType visit(StringLiteral node, Procedure proc)
    {
        return node.getType();
    }
    
    @Override
    public IType visit(AssignStatement node, Procedure proc)
    {
        IType left_type = node.getLeft().accept(this, proc);
        IType right_type = node.getRight().accept(this, proc);
        
        if (left_type.isUnknown() && !right_type.isUnknown())
        {
            left_type = right_type;
        }
        else if (!left_type.isUnknown() && right_type.isUnknown())
        {
            right_type = left_type;
        }
        else
        {
            if (left_type == StringType.CHAR && right_type == BasicType.CHAR)
            {
                left_type = right_type;
            }
            if (right_type == StringType.CHAR && left_type == BasicType.CHAR)
            {
                right_type = left_type;
            }
        }
        
        final String err_msgl = "cannot assign to array type variable '" + node.getLeft().getName()
                + "' used as pure variable.";
        final String err_msgr = "incompatible type: cannot assign array type: " + right_type + ".";
        
        if (left_type.isBasicType())
        {
            if (right_type.isBasicType())
            {
                if (!left_type.equals(right_type))
                {
                    checker.addErrorMessage(
                        proc, node,
                        "incompatible type: cannot assign " + right_type + " type to " + left_type + " type."
                    );
                }
            }
            else
            {
                checker.addErrorMessage(proc, node.getRight(), err_msgr);
            }
        }
        else
        {
            checker.addErrorMessage(proc, node.getLeft(), err_msgl);
            if (!right_type.isBasicType())
            {
                checker.addErrorMessage(proc, node.getRight(), err_msgr);
            }
        }
        return null;
    }
    
    @Override
    public IType visit(CompoundStatement node, Procedure proc)
    {
        node.forEach(stm -> stm.accept(this, proc));
        return null;
    }
    
    @Override
    public IType visit(IfElseStatement node, Procedure proc)
    {
        visit(node.getIfPart(), proc);
        node.getElse().accept(this, proc);
        return null;
    }
    
    @Override
    public IType visit(IfStatement node, Procedure proc)
    {
        final IType type = node.getCond().accept(this, proc);
        if (!type.equals(BasicType.BOOLEAN) && !type.isUnknown())
        {
            checker.addErrorMessage(
                proc, node.getCond(),
                "incompatible type: cannot use " + type + " type as condition of if-statement. must be BOOLEAN."
            );
        }
        
        node.getThen().accept(this, proc);
        return null;
    }
    
    @Override
    public IType visit(ProcCallStatement node, Procedure proc)
    {
        node.setCalledProc(
            proc.findSubProc(node.getName().toString())
                .orElse(null)
        );
        
        if (node.getCalledProc() == null)
        {
            checkWhenNotFound(node, proc);
        }
        else if (node.getArgs().size() != node.getCalledProc().getParams().length())
        {
            checkWhenInvalidLength(node, proc);
        }
        else
        {
            checkArgumentTypes(node, proc);
        }
        return null;
    }
    
    private void checkWhenNotFound(ProcCallStatement node, Procedure proc)
    {
        final List<Procedure> p = proc.searchForSubProcFuzzy(node.getName().toString());
        checker.addErrorMessage(
            proc, node.getName(),
            "procedure '" + node.getName() + "' is not defined."
                    + (p.isEmpty()? "": (" did you mean procedure " + p + "?"))
        );
        
        node.getArgs().forEach(exp -> exp.accept(this, proc));
    }
    
    private void checkArgumentTypes(ProcCallStatement node, Procedure proc)
    {
        IntStream.range(0, node.getArgs().size()).forEachOrdered(
            i -> {
                final IType ptype = node.getCalledProc().getParams().get(i).getType();
                final IType atype = node.getArgs().get(i).accept(this, proc);
                
                if (!ptype.equals(atype))
                {
                    checker.addErrorMessage(
                        proc, node,
                        "incompatible type: cannot pass " + atype + " type to " + Checker.getOrderString(i + 1)
                                + " argument of procedure '" + node.getName() + "'. must be " + ptype + "."
                    );
                }
            }
        );
    }
    
    private void checkWhenInvalidLength(ProcCallStatement node, Procedure proc)
    {
        final String msg1 = node.getArgs().size() == 0? "no": "" + node.getArgs().size();
        final int len = node.getCalledProc().getParams().length();
        final String msg2 = len == 0? "no": "" + len;
        
        checker.addErrorMessage(
            proc, node,
            "cannot call procedure '" + node.getName() + "' by " + msg1 + " arguments. must be " + msg2 + " args."
        );
        
        node.getArgs().forEach(exp -> exp.accept(this, proc));
    }
    
    @Override
    public IType visit(ReadStatement node, Procedure proc)
    {
        int i = 1;
        for (final IVariable var: node.getVariables())
        {
            final IType type = var.accept(this, proc);
            
            if (type.isUnknown())
            {
                checker.addErrorMessage(
                    proc, var, "cannot identify the type of " + Checker.getOrderString(i) + " argument of readln."
                );
            }
            else if (type != BasicType.INTEGER && type != BasicType.CHAR && !type.isArrayOf(BasicType.CHAR))
            {
                checker.addErrorMessage(
                    proc, var, "incompatible type: " + Checker.getOrderString(i)
                            + " argument of readln must be INTEGER, CHAR, or array of CHAR, but is " + type
                );
            }
            ++i;
        }
        return null;
    }
    
    @Override
    public IType visit(WhileStatement node, Procedure proc)
    {
        final IType type = node.getCond().accept(this, proc);
        if (!type.equals(BasicType.BOOLEAN) && !type.isUnknown())
        {
            checker.addErrorMessage(
                proc, node.getCond(),
                "incompatible type: cannot use " + type + " type as condition of while-statement. must be BOOLEAN."
            );
        }
        node.getStatement().accept(this, proc);
        return null;
    }
    
    @Override
    public IType visit(WriteStatement node, Procedure proc)
    {
        int i = 1;
        for (final IExpression exp: node.getExpressions())
        {
            final IType type = exp.accept(this, proc);
            
            if (!type.equals(BasicType.INTEGER) && !type.equals(BasicType.CHAR) && !type.isArrayOf(BasicType.CHAR))
            {
                checker.addErrorMessage(
                    proc, exp, "incompatible type: " + Checker.getOrderString(i)
                            + " argument of writeln must be INTEGER, CHAR, or array of CHAR, but is " + type + "."
                );
            }
            ++i;
        }
        return null;
    }
    
}
