package enshud.s1.lexer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * Definition of tokens
 */
enum TokenType
{
    SAND       ( 0, "and"                 ),
    SARRAY     ( 1, "array"               ),
    SBEGIN     ( 2, "begin"               ),
    SBOOLEAN   ( 3, "boolean"             ),
    SCHAR      ( 4, "char"                ),
    SDIVD      ( 5, "(div|/)"             ),
    SDO        ( 6, "do"                  ),
    SELSE      ( 7, "else"                ),
    SEND       ( 8, "end"                 ),
    SFALSE     ( 9, "false"               ),
    SIF        (10, "if"                  ),
    SINTEGER   (11, "integer"             ),
    SMOD       (12, "mod"                 ),
    SNOT       (13, "not"                 ),
    SOF        (14, "of"                  ),
    SOR        (15, "or"                  ),
    SPROCEDURE (16, "procedure"           ),
    SPROGRAM   (17, "program"             ),
    SREADLN    (18, "readln"              ),
    STHEN      (19, "then"                ),
    STRUE      (20, "true"                ),
    SVAR       (21, "var"                 ),
    SWHILE     (22, "while"               ),
    SWRITELN   (23, "writeln"             ),
    SEQUAL     (24, "="                   ),
    SNOTEQUAL  (25, "<>"                  ),
    SLESSEQUAL (27, "<="                  ),
    SLESS      (26, "<"                   ),
    SGREATEQUAL(28, ">="                  ),
    SGREAT     (29, ">"                   ),
    SPLUS      (30, "\\+"                 ),
    SMINUS     (31, "-"                   ),
    SSTAR      (32, "\\*"                 ),
    SLPAREN    (33, "\\("                 ),
    SRPAREN    (34, "\\)"                 ),
    SLBRACKET  (35, "\\["                 ),
    SRBRACKET  (36, "\\]"                 ),
    SSEMICOLON (37, ";"                   ),
    SASSIGN    (40, ":="                  ),
    SCOLON     (38, ":"                   ),
    SRANGE     (39, "\\.\\."              ),
    SCOMMA     (41, ","                   ),
    SDOT       (42, "\\."                 ),
    SIDENTIFIER(43, "[a-zA-Z][a-zA-Z0-9]*"),
    SCONSTANT  (44, "[0-9]+"              ),
    SSTRING    (45, "'[^'$]+'"            ),

    SSPACE     (-1, "( |\\t|$)+"          ),
    SCOMMENTBEG(-2, "\\{"                 ),
    SCOMMENTEND(-3, "\\}"                 ),

    SUNKNOWN   (-4, "."                   );

    private int id;
    private Pattern pattern;

    TokenType(int id, String pattern)
    {
        this.id = id;
        this.pattern = Pattern.compile("^" + pattern);
    }

    int getId()
    {
        return this.id;
    }

    Token lex(String input)
    {
        Matcher m = pattern.matcher(input);
        if(m.find())
        {
            String str = m.group();
            return new Token(str, this);
        }
        else
        {
            return null;
        }
    }

    static Token lexOneToken(String input)
    {
        for(TokenType type: TokenType.values())
        {
            Token token = type.lex(input);
            if(token != null)
            {
                return token;
            }
        }
        return new Token(input.substring(0, 1), SUNKNOWN);
    }
}