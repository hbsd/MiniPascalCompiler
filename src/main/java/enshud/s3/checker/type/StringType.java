package enshud.s3.checker.type;

public class StringType implements IType
{
    public static final StringType CHAR = new StringType(1);
    
    final int size;

    public StringType(int size)
    {
        this.size = size;
    }
    
    public static boolean isCharOrCharArray(IType type)
    {
        return type == RegularType.CHAR || type.isArrayOf(RegularType.CHAR);
    }

    public int getSize()
    {
        return size;
    }

    @Override
    public RegularType getRegularType()
    {
        return RegularType.CHAR;
    }

    @Override
    public boolean isArrayOf(RegularType rtype)
    {
        return rtype == RegularType.CHAR;
    }

    @Override
    public boolean isArrayType()
    {
        return true;
    }

    @Override
    public boolean isRegularType()
    {
        return false;
    }

    @Override
    public boolean isUnknown()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "STRING";
    }
}
