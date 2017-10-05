package enshud.s1.lexer;

/**
 * Simple token data
 */
@SuppressWarnings("serial")
public class IllegalCharException extends Exception
{
    LexedToken token;
    String line;
    IllegalCharException(String line, LexedToken token)
    {
        super();
        this.line = line;
        this.token = token;
    }

    public LexedToken getToken()
    {
        return token;
    }

    public String getLine()
    {
        return line;
    }

    @Override
    public String toString()
    {
        String str = "Illegal Character(" + getToken().getLine() + ","
                   + getToken().getColumn() + "): "
                   + getToken().getString() + System.lineSeparator();

        // indicate error column
        // (when include multibyte char,
        //  this output will be wrong position)
        str += getLine() + System.lineSeparator();
        for(int i = 1; i < getToken().getColumn(); ++i)
        {
            str += " ";
        }
        str += "^";

        return str;
    }
}

