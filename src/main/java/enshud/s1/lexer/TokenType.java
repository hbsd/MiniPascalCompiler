package enshud.s1.lexer;

import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;


/**
 * Definition of tokens
 */
public enum TokenType
{
    SAND(0, "and(?![a-zA-Z0-9])"),
    SARRAY(1, "array(?![a-zA-Z0-9])"),
    SBEGIN(2, "begin(?![a-zA-Z0-9])"),
    SBOOLEAN(3, "boolean(?![a-zA-Z0-9])"),
    SCHAR(4, "char(?![a-zA-Z0-9])"),
    SDIVD(5, "(div(?![a-zA-Z0-9])|/)"),
    SDO(6, "do(?![a-zA-Z0-9])"),
    SELSE(7, "else(?![a-zA-Z0-9])"),
    SEND(8, "end(?![a-zA-Z0-9])"),
    SFALSE(9, "false(?![a-zA-Z0-9])"),
    SIF(10, "if(?![a-zA-Z0-9])"),
    SINTEGER(11, "integer(?![a-zA-Z0-9])"),
    SMOD(12, "mod(?![a-zA-Z0-9])"),
    SNOT(13, "not(?![a-zA-Z0-9])"),
    SOF(14, "of(?![a-zA-Z0-9])"),
    SOR(15, "or(?![a-zA-Z0-9])"),
    SPROCEDURE(16, "procedure(?![a-zA-Z0-9])"),
    SPROGRAM(17, "program(?![a-zA-Z0-9])"),
    SREADLN(18, "readln(?![a-zA-Z0-9])"),
    STHEN(19, "then(?![a-zA-Z0-9])"),
    STRUE(20, "true(?![a-zA-Z0-9])"),
    SVAR(21, "var(?![a-zA-Z0-9])"),
    SWHILE(22, "while(?![a-zA-Z0-9])"),
    SWRITELN(23, "writeln(?![a-zA-Z0-9])"),
    SEQUAL(24, "="),
    SNOTEQUAL(25, "<>"),
    SLESSEQUAL(27, "<="),
    SLESS(26, "<"),
    SGREATEQUAL(28, ">="),
    SGREAT(29, ">"),
    SPLUS(30, "\\+"),
    SMINUS(31, "-"),
    SSTAR(32, "\\*"),
    SLPAREN(33, "\\("),
    SRPAREN(34, "\\)"),
    SLBRACKET(35, "\\["),
    SRBRACKET(36, "\\]"),
    SSEMICOLON(37, ";"),
    SASSIGN(40, ":="),
    SCOLON(38, ":"),
    SRANGE(39, "\\.\\."),
    SCOMMA(41, ","),
    SDOT(42, "\\."),
    SIDENTIFIER(43, "[a-zA-Z][a-zA-Z0-9]*"),
    SCONSTANT(44, "[0-9]+"),
    SSTRING(45, "'[^'$]+'"),
    
    SSPACE(-1, "( |\\t|$)+"),
    SCOMMENTBEG(-2, "\\{"),
    SCOMMENTEND(-3, "\\}"),
    
    SUNKNOWN(-4, ".");
    
    private int     id;
    private Pattern pattern;
    
    TokenType(int id, String pattern)
    {
        this.id = id;
        this.pattern = Pattern.compile("^(" + pattern + ")");
    }
    
    int getId()
    {
        return id;
    }
    
    private Token lex_nullable(String input)
    {
        final Matcher m = pattern.matcher(input);
        return m.find()
                ? new Token(m.group(), this)
                : null;
    }
    
    Optional<Token> lex(String input)
    {
        return Optional.ofNullable(lex_nullable(input));
    }
    
    static Token lexOneToken(String input)
    {
        return Stream.of(TokenType.values())
                .map(type -> type.lex_nullable(input))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> new Token(input.substring(0, 1), SUNKNOWN));
    }
    
    static Optional<TokenType> getById(int id)
    {
        return Stream.of(TokenType.values())
                .filter(type -> type.getId() == id)
                .findFirst();
    }
}

