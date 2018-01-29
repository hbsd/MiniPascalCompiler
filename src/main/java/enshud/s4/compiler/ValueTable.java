package enshud.s4.compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import enshud.pascal.QualifiedVariable;
import enshud.pascal.ast.expression.IConstant;
import enshud.pascal.ast.expression.IExpression;
import enshud.pascal.ast.expression.IVariable;
import enshud.pascal.ast.expression.IndexedVariable;
import enshud.pascal.ast.expression.PureVariable;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.BasicType;


public class ValueTable
{
    private final Map<String, IExpression> tbl;
    
    public ValueTable()
    {
        tbl = new HashMap<>();
    }
    
    public ValueTable(ValueTable vtbl)
    {
        this.tbl = new HashMap<>(vtbl.tbl);
    }
    
    public void clear()
    {
        tbl.clear();
    }
    
    public void merge(ValueTable vtbl)
    {
        tbl.keySet().forEach(
            k -> {
                final IExpression v1 = this.tbl.get(k);
                final IExpression v2 = vtbl.tbl.get(k);
                if (v1 == null || v2 == null)
                {
                    tbl.put(k, null);
                }
                else if (v1.equals(v2))
                {
                    tbl.put(k, v1);
                }
                else
                {
                    tbl.put(k, null);
                }
            }
        );
    }
    
    public static List<String> calcName(IVariable v)
    {
        if (v instanceof PureVariable)
        {
            return Arrays.asList(v.getQualifiedName());
        }
        else if (v instanceof IndexedVariable)
        {
            final IndexedVariable iv = (IndexedVariable)v;
            final IExpression idx = iv.getIndex();
            if (idx.isConstant())
            {
                return Arrays.asList(iv.getQualifiedName() + "[" + ((IConstant)idx).getValue().getInt() + "]");
            }
            else if (iv.getArrayType() instanceof ArrayType)
            {
                return IntStream.rangeClosed(
                    ((ArrayType)iv.getArrayType()).getMin(),
                    ((ArrayType)iv.getArrayType()).getMax()
                )
                    .mapToObj(i -> iv.getQualifiedName() + "[" + i + "]")
                    .collect(Collectors.toList());
            }
        }
        throw new UnsupportedOperationException();
    }
    
    
    public static List<String> calcName(QualifiedVariable v)
    {
        if (v.getType() instanceof BasicType)
        {
            return Arrays.asList(v.getQualifiedName());
        }
        else if (v.getType() instanceof ArrayType)
        {
            return IntStream.rangeClosed(
                ((ArrayType)v.getType()).getMin(),
                ((ArrayType)v.getType()).getMax()
            )
                .mapToObj(i -> v.getQualifiedName() + "[" + i + "]")
                .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException();
    }
    
    public void put(IVariable v, IExpression val)
    {
        tbl.replaceAll((k, ov) -> v.equals(ov)? null: ov);
        
        final List<String> nms = calcName(v);
        if (nms.size() >= 2)
        {
            val = null;
        }
        else if (val instanceof IConstant)
        {
            // Empty
        }
        else if (val instanceof IVariable && calcName((IVariable)val).size() == 1)
        {
            // Empty
        }
        else
        {
            val = null;
        }
        for (final String n: nms)
        {
            // System.out.println(n + " <- " + val);
            tbl.put(n, val);
        }
    }
    
    public IExpression get(IVariable v)
    {
        // System.out.println(tbl);
        final IExpression e = getImpl(v, v);
        // System.out.println(v + " -> " + (e == v? null: e));
        return e == v? null: e;
    }
    
    // to avoid cycle
    private IExpression getImpl(IVariable v, final IVariable original)
    {
        final List<String> nms = calcName(v);
        if (nms.size() != 1)
        {
            return null;
        }
        
        final IExpression e = tbl.getOrDefault(nms.get(0), null);
        if (e == null)
        {
            return v;
        }
        else if (e instanceof IVariable)
        {
            return (e == original)? v: getImpl((IVariable)e, original);
        }
        else
        {
            return e;
        }
    }
}
