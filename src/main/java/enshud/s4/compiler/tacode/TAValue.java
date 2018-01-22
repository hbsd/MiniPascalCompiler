package enshud.s4.compiler.tacode;

import java.util.function.Consumer;

import enshud.pascal.value.BooleanValue;
import enshud.pascal.value.CharValue;
import enshud.pascal.value.IValue;
import enshud.pascal.value.IntegerValue;
import enshud.pascal.value.StringValue;
import io.vavr.control.Either;


public class TAValue
{
    private final Either<String, IValue> etr;
    
    public void visit(Consumer<? super String> lf, Consumer<? super IValue> rf)
    {
        if(etr.isLeft())
        {
            lf.accept(etr.getLeft());
        }
        else
        {
            rf.accept(etr.get());
        }
    }
    
    public void whenVar(Consumer<? super String> lf)
    {
        etr.peekLeft(lf);
    }
    
    public void whenVal(Consumer<? super IValue> rf)
    {
        etr.peek(rf);
    }
    
    public TAValue(Either<String, IValue> etr)
    {
        this.etr = etr;
    }
    
    public static TAValue constant(IValue val)
    {
        return new TAValue(Either.right(val));
    }
    
    public static TAValue constant(int val)
    {
        return constant(IntegerValue.create(val));
    }
    
    public static TAValue constant(boolean val)
    {
        return constant(BooleanValue.create(val));
    }
    
    public static TAValue constant(char val)
    {
        return constant(CharValue.create(val));
    }
    
    public static TAValue constant(String val)
    {
        return constant(StringValue.create(val));
    }
    
    public static TAValue variable(String val)
    {
        return new TAValue(Either.left(val));
    }
    
    @Override
    public String toString()
    {
        return etr.map(r -> "Con(" + r + ")")
            .getOrElse(() -> "Var(" + etr.getLeft() + ")");
    }
}
