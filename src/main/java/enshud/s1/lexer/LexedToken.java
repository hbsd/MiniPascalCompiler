package enshud.s1.lexer;

/**
 * Token with line number and column number
 */
public class LexedToken
{
	private final Token token;
    private final int line;
    private final int column;

    public String getString()
    {
        return token.str;
    }
    public TokenType getType()
    {
        return token.type;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    public LexedToken(Token token, int line, int column)
    {
        this.token = token;
        this.line = line;
        this.column = column;
    }

    public LexedToken(String str, TokenType type, int line, int column)
    {
        this.token = new Token(str, type);
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
