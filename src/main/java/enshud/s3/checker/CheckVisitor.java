package enshud.s3.checker;

import java.util.List;
import java.util.Optional;

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
    public IType visitBooleanLiteral(BooleanLiteral node, Procedure proc)
    {
        return node.getType();
    }

    @Override
    public IType visitIndexedVariable(IndexedVariable node, Procedure proc)
    {
        final String nm = node.getName().toString();
        final Optional<QualifiedVariable> var = proc.getVar(nm);
        node.setArrayType(
            var.map(v -> v.getType())
               .orElse(UnknownType.UNKNOWN)
        );
        
        if (!var.isPresent())
        {
            List<QualifiedVariable> vs = proc.searchForVarFuzzy(nm);
            checker.addErrorMessage(
                proc, node.getName(),
                "variable '" + nm + "' is not defined."
                        + (vs.isEmpty()? "": (" did you mean variable " + vs + "?"))
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
                "incompatible type: cannot use " + idx_type + " type as index of '" + node.getName() + "'. must be INTEGER."
            );
        }
    }

    @Override
    public IType visitInfixOperation(InfixOperation node, Procedure proc)
    {
        IType left_type = node.getLeft().accept(this, proc);
        IType right_type = node.getRight().accept(this, proc);
        
        node.setType(node.getOp().checkType(proc, checker, node.getOpToken(), left_type, right_type));
        return node.getType();
    }

    @Override
    public IType visitIntegerLiteral(IntegerLiteral node, Procedure proc)
    {
        return node.getType();
    }

    @Override
    public IType visitPrefixOperation(PrefixOperation node, Procedure proc)
    {
        final IType t = node.getOperand().accept(this, proc);
        node.setType(node.getOp().checkType(proc, checker, node.getOpToken(), t));
        return node.getType();
    }

    @Override
    public IType visitPureVariable(PureVariable node, Procedure proc)
    {
        final String nm = node.getName().toString();
        
        final Optional<QualifiedVariable> param = proc.getParam(nm);
        
        if (param.isPresent())
        {
            node.setVar(param.get());
            node.setIsParam(true);
            node.setType(param.get().getType());
        }
        else
        {
            final Optional<QualifiedVariable> var = proc.getVar(nm);
            node.setType(
                var.map(v -> v.getType())
                   .orElse(UnknownType.UNKNOWN)
            );
            if (var.isPresent())
            {
                node.setVar(var.get());
                node.setIsParam(false);
                node.setType(var.get().getType());
            }
            else
            {
                node.setType(UnknownType.UNKNOWN);
                checkFuzzy(node, proc);
            }
        }
        
        return node.getType();
    }
    private void checkFuzzy(PureVariable node, Procedure proc)
    {
        final String nm = node.getName().toString();
        final List<QualifiedVariable> vs = proc.searchForParamFuzzy(nm);
        vs.addAll(proc.searchForVarFuzzy(nm));
        
        checker.addErrorMessage(
            proc, node.getName(),
            "variable '" + nm + "' is not defined."
                    + (vs.isEmpty()? "": (" did you mean variable " + vs + "?"))
        );
    }

    @Override
    public IType visitStringLiteral(StringLiteral node, Procedure proc)
    {
        return node.getType();
    }

    @Override
    public IType visitAssignStatement(AssignStatement node, Procedure proc)
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
    public IType visitCompoundStatement(CompoundStatement node, Procedure proc)
    {
        node.forEach(stm -> stm.accept(this, proc));
        return null;
    }

    @Override
    public IType visitIfElseStatement(IfElseStatement node, Procedure proc)
    {
        visitIfStatement(node, proc);
        node.getElse().accept(this, proc);
        return null;
    }

    @Override
    public IType visitIfStatement(IfStatement node, Procedure proc)
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
    public IType visitProcCallStatement(ProcCallStatement node, Procedure proc)
    {
        node.setCalledProc(
            proc.getSubProc(node.getName().toString())
                .orElse(null)
        );
        
        if (node.getCalledProc() == null)
        {
            checkWhenNotFound(node, proc);
        }
        else if (node.getArgs().size() != node.getCalledProc().getParamLength())
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
        List<Procedure> p = proc.getSubProcFuzzy(node.getName().toString());
        checker.addErrorMessage(
            proc, node.getName(),
            "procedure '" + node.getName() + "' is not defined."
                    + (p.isEmpty()? "": (" did you mean procedure " + p + "?"))
        );
        
        node.getArgs().forEach(exp -> exp.check(proc, checker));
    }
    
    private void checkArgumentTypes(ProcCallStatement node, Procedure proc)
    {
        int i = 0;
        for (final IExpression exp: node.getArgs())
        {
            final IType ptype = node.getCalledProc().getParamType(i);
            final IType atype = exp.check(proc, checker);
            
            if (!ptype.equals(atype))
            {
                checker.addErrorMessage(
                    proc, node,
                    "incompatible type: cannot pass " + atype + " type to " + Checker.getOrderString(i + 1)
                            + " argument of procedure '" + node.getName() + "'. must be " + ptype + "."
                );
            }
            ++i;
        }
    }
    
    private void checkWhenInvalidLength(ProcCallStatement node, Procedure proc)
    {
        final String msg1 = node.getArgs().size() == 0? "no": "" + node.getArgs().size();
        final String msg2 = node.getCalledProc().getParamLength() == 0? "no": "" + node.getCalledProc().getParamLength();
        
        checker.addErrorMessage(
            proc, node, "cannot call procedure '" + node.getName() + "' by " + msg1 + " arguments. must be " + msg2 + " args."
        );
        
        node.getArgs().forEach(exp -> exp.accept(this, proc));
    }

    @Override
    public IType visitReadStatement(ReadStatement node, Procedure proc)
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
    public IType visitWhileStatement(WhileStatement node, Procedure proc)
    {
        final IType type = node.getCond().accept(this, proc);
        if (!type.equals(BasicType.BOOLEAN) && !type.isUnknown())
        {
            checker.addErrorMessage(
                proc, node.getCond(),
                "incompatible type: cannot use " + type + " type as condition of while-statement. must be BOOLEAN."
            );
        }
        node.getStatement().check(proc, checker);
        return null;
    }

    @Override
    public IType visitWriteStatement(WriteStatement node, Procedure proc)
    {
        int i = 1;
        for (final IExpression exp: node.getExpressions())
        {
            IType type = exp.accept(this, proc);
            
            if (!type.equals(BasicType.INTEGER) && !type.equals(BasicType.CHAR) && !type.isArrayOf(BasicType.CHAR))
            {
                checker.addErrorMessage(
                    proc, exp, "incompatible type: " + Checker.getOrderString(i)
                            + " argument of writeln must be INTEGER, CHAR, or array of CHAR, but is " + type + "."
                );
            }
            /*else if (type.isUnknown())
            {
                checker.addErrorMessage(
                    proc, exp, "cannot identify the type of " + Checker.getOrderString(i) + " argument of writeln."
                );
            }*/
            ++i;
        }
        return null;
    }

}
