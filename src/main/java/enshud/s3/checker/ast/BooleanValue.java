package enshud.s3.checker.ast;

import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s3.checker.type.IType;
import enshud.s3.checker.type.RegularType;
import enshud.s4.compiler.LabelGenerator;


public enum BooleanValue implements IConstant
{
    FALSE {
        @Override
        public boolean getBool()
        {
            return false;
        }
    },
    TRUE {
        @Override
        public boolean getBool()
        {
            return true;
        }
    };
    
    @Override
    public IType getType()
    {
        return RegularType.BOOLEAN;
    }

    @Override
    public int getLine()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumn()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void retype(IType new_type)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public IType check(Procedure proc, Checker checker)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void compile(StringBuilder codebuilder, Procedure proc, LabelGenerator l_gen)
    {
        throw new UnsupportedOperationException();
    }

    public abstract boolean getBool();
}


