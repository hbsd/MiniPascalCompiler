package enshud.s1.lexer;

/**
 * Token with line number and column number
 */
public class LexedToken
{
    private final Token token;
    private final int line;
    private final int column;

    String getString()
    {
        return token.str;
    }
    TokenType getType()
    {
        return token.type;
    }

    int getLine()
    {
        return line;
    }

    int getColumn()
    {
        return column;
    }

    LexedToken(Token token, int line, int column)
    {
        this.token = token;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString()
    {
        return getString() + "\t" + getType() + "\t" + getType().getId()
             + "\t" + line/* + "\t" + column*/;
    }
}
