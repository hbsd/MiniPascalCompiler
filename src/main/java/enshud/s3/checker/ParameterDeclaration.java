package enshud.s3.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import enshud.s3.checker.type.RegularType;


public class ParameterDeclaration
{
    final List<Param> params = new ArrayList<>();

    public class Param
    {
        final String      name;
        final RegularType type;
        final int alignment;

        Param(String name, RegularType type, int alignment)
        {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
            this.alignment = alignment;
        }
        
        public String getName() {
			return name;
		}
        
        public RegularType getType() {
			return type;
		}

		public int getAlignment() {
			return alignment;
		}
    }

    void add(String name, RegularType type)
    {
        params.add(new Param(name, type, params.size()));
    }

    Param get(int num)
    {
        return params.get(num);
    }

    Param get(String name)
    {
        for(final Param p: params)
        {
            if( p.name.equals(name) )
            {
                return p;
            }
        }
        return null;
    }

    int getIndex(String name)
    {
    	int i = 0;
        for(final Param p: params)
        {
            if( p.name.equals(name) )
            {
                return i;
            }
            ++i;
        }
        return -1;
    }

    int length()
    {
        return params.size();
    }

    boolean exists(String name)
    {
        return params.contains(name);
    }
}
