package enshud.pascal.ast;


public enum CompareOperator implements ILiteral
{
    EQUAL {
        @Override
        boolean eval(int lval, int rval)
        {
            return lval == rval;
        }
    },
    NOTEQUAL {
        @Override
        boolean eval(int lval, int rval)
        {
            return lval != rval;
        }
    },
    LESS {
        @Override
        boolean eval(int lval, int rval)
        {
            return lval < rval;
        }
    },
    LESSEQUAL {
        @Override
        boolean eval(int lval, int rval)
        {
            return lval <= rval;
        }
    },
    GREAT {
        @Override
        boolean eval(int lval, int rval)
        {
            return lval > rval;
        }
    },
    GREATEQUAL {
        @Override
        boolean eval(int lval, int rval)
        {
            return lval >= rval;
        }
    };
    
    abstract boolean eval(int lval, int rval);
    
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
}

