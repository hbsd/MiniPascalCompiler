package enshud.pascal.type;

public class StringType implements IType
{
    public static final StringType CHAR = new StringType(1);
    
    final int                      size;
    
    public StringType(int size)
    {
        this.size = size;
    }
    
    public static boolean isCharOrCharArray(IType bype)
    {
        return bype == BasicType.CHAR || bype.isArrayOf(BasicType.CHAR);
    }
    
    @Override
    public int getSize()
    {
        return size;
    }
    
    @Override
    public BasicType getBasicType()
    {
        return BasicType.CHAR;
    }
    
    @Override
    public boolean isArrayOf(BasicType rtype)
    {
        return rtype == BasicType.CHAR;
    }
    
    @Override
    public boolean isArrayType()
    {
        return true;
    }
    
    @Override
    public boolean isBasicType()
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
