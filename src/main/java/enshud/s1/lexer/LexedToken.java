package enshud.s1.lexer;

import java.util.Objects;


/**
 * Token with line number and column number
 */
public class LexedToken
{
    public static final LexedToken DUMMY = new LexedToken("", TokenType.SUNKNOWN, -1, -1);
    
    private final Token            token;
    private final int              line;
    private final int              column;
    
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
        this.token = Objects.requireNonNull(token);
        this.line = Objects.requireNonNull(line);
        this.column = Objects.requireNonNull(column);
    }
    
    public LexedToken(String str, TokenType type, int line, int column)
    {
        this(new Token(str, type), line, column);
    }
    
    public static LexedToken fromString(String str)
    {
        final String[] ss = str.split("\\t");
        return new LexedToken(
            ss[0], TokenType.getById(Integer.parseInt(ss[2])),
            Integer.parseInt(ss[3]), -1
        );
    }
    
    @Override
    public String toString()
    {
        return getString() + "\t" + getType() + "\t" + getType().getId() + "\t"
                + line/* + "\t" + column */;
    }
}
