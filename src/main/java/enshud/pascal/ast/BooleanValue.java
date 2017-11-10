package enshud.pascal.ast;

import enshud.pascal.type.IType;
import enshud.pascal.type.BasicType;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;
import enshud.s4.compiler.LabelGenerator;


public enum BooleanValue implements IConstant
{
    FALSE {
        @Override
        public boolean getBool()
        {
            return false;
        }
        
        @Override
        public BooleanValue not()
        {
            return TRUE;
        }
        
        @Override
        public int getInt()
        {
            return 0;
        }
    },
    TRUE {
        @Override
        public boolean getBool()
        {
            return true;
        }
        
        @Override
        public BooleanValue not()
        {
            return FALSE;
        }
        
        @Override
        public int getInt()
        {
            return 1;
        }
    };
    
    @Override
    public IType getType()
    {
        return BasicType.BOOLEAN;
    }
    
    public abstract boolean getBool();
    
    public abstract int getInt();
    
    public abstract BooleanValue not();
    
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
}

