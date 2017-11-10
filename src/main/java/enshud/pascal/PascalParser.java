package enshud.pascal;

import java.util.List;
import java.util.Set;

import enshud.pascal.ast.*;
import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.TokenType;

import static enshud.s1.lexer.TokenType.*;

import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.basic.*;
import enshud.s2.parser.parsers.IParser;

import static enshud.s2.parser.parsers.basic.Parsers.*;


public enum PascalParser implements IParser
{
    // to avoid circle reference, override the method
    PROGRAM(1) {
        @Override
        protected IParser rule()
        {
            return end(
                seq(
                    tok(SPROGRAM), NAME,
                    tok(SLPAREN), NAME_LIST, tok(SRPAREN), tok(SSEMICOLON),
                    optseq(tok(SVAR), VAR_DECL_LIST),
                    SUBPROGRAM_DECL_LIST, COMPOUND_STATEMENT,
                    tok(SDOT)
                )
            );
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            
            return new Program(
                (Identifier)n.get(1),
                (NameList)n.get(3),
                n.get(6) instanceof EmptyNode? new VariableDeclarationList()
                        : (VariableDeclarationList)n.getAsSeq(6).get(1),
                (SubProgramDeclarationList)n.get(7),
                (StatementList)n.get(8)
            );
        }
    },
    NAME_LIST(2) {
        @Override
        protected IParser rule()
        {
            return seq(NAME, rep0seq(tok(SCOMMA), NAME));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            
            final NameList list = new NameList((Identifier)n.get(0));
            
            for (final INode c: n.getAsSeq(1).getChildren())
            {
                list.add((Identifier)((SequenceNode)c).get(1));
            }
            return list;
        }
    },
    VAR_DECL_LIST(3) {
        @Override
        protected IParser rule()
        {
            return rep1(VAR_DECL);
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            final VariableDeclarationList list = new VariableDeclarationList();
            
            for (final INode c: n.getChildren())
            {
                list.add((VariableDeclaration)c);
            }
            return list;
        }
    },
    VAR_DECL(4) {
        @Override
        protected IParser rule()
        {
            return seq(NAME_LIST, tok(SCOLON), TYPE, tok(SSEMICOLON));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new VariableDeclaration((NameList)n.get(0), (TypeLiteral)n.get(2));
        }
    },
    TYPE(5) {
        @Override
        protected IParser rule()
        {
            return firstsel(BASIC_TYPE, ARRAY_TYPE);
        }
    },
    BASIC_TYPE(6) {
        @Override
        protected IParser rule()
        {
            return firstsel(tok(SINTEGER), tok(SCHAR), tok(SBOOLEAN));
        }
        
        @Override
        protected INode success(INode node)
        {
            final LexedToken token = ((TokenNode)node).getToken();
            return new TypeLiteral(token);
        }
        
        @Override
        protected INode failure(INode node)
        {
            return new FailureNode(((FailureNode)node).getChild(), "RegularType expected.");
        }
    },
    ARRAY_TYPE(7) {
        @Override
        protected IParser rule()
        {
            return seq(
                tok(SARRAY), tok(SLBRACKET), INTEGER, tok(SRANGE), INTEGER, tok(SRBRACKET), tok(SOF), BASIC_TYPE
            );
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new TypeLiteral(
                ((TypeLiteral)n.get(7)).getRegularType(),
                (SignedInteger)n.get(2),
                (SignedInteger)n.get(4),
                ((TokenNode)n.get(0)).getToken()
            );
        }
    },
    INTEGER(8) {
        @Override
        protected IParser rule()
        {
            return seq(opt(SIGN), UNSIGNED_INT);
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new SignedInteger(
                n.get(0) instanceof EmptyNode? SignLiteral.NONE: (SignLiteral)n.get(0),
                (IntegerLiteral)n.get(1)
            );
        }
    },
    SIGN(9) {
        @Override
        protected IParser rule()
        {
            return firstsel(tok(SPLUS), tok(SMINUS));
        }
        
        @Override
        protected INode success(INode node)
        {
            return new SignLiteral(((TokenNode)node).getToken());
        }
        
        @Override
        protected INode failure(INode node)
        {
            return new FailureNode(((FailureNode)node).getChild(), "Sign expected.");
        }
    },
    SUBPROGRAM_DECL_LIST(10) {
        @Override
        protected IParser rule()
        {
            return rep0(SUBPROGRAM_DECL);
        }
        
        @Override
        protected INode success(INode node)
        {
            final SubProgramDeclarationList list = new SubProgramDeclarationList();
            
            for (final INode c: ((SequenceNode)node).getChildren())
            {
                list.add((SubProgramDeclaration)c);
            }
            return list;
        }
    },
    SUBPROGRAM_DECL(11) {
        @Override
        protected IParser rule()
        {
            return seq(
                tok(SPROCEDURE), NAME,
                optseq(tok(SLPAREN), PARAMETER_LIST, tok(SRPAREN)), tok(SSEMICOLON),
                optseq(tok(SVAR), VAR_DECL_LIST),
                COMPOUND_STATEMENT,
                tok(SSEMICOLON)
            );
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new SubProgramDeclaration(
                (Identifier)n.get(1),
                n.get(2) instanceof EmptyNode? new ParameterList(): (ParameterList)n.getAsSeq(2).get(1),
                n.get(4) instanceof EmptyNode? new VariableDeclarationList()
                        : (VariableDeclarationList)n.getAsSeq(4).get(1),
                (StatementList)n.get(5)
            );
        }
    },
    PARAMETER_LIST(12) {
        @Override
        protected IParser rule()
        {
            return seq(PARAMETER, rep0seq(tok(SSEMICOLON), PARAMETER));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            
            final ParameterList list = new ParameterList((Parameter)n.get(0));
            
            for (final INode c: n.getAsSeq(1).getChildren())
            {
                list.add((Parameter)((SequenceNode)c).get(1));
            }
            return list;
        }
    },
    PARAMETER(13) {
        @Override
        protected IParser rule()
        {
            return seq(NAME_LIST, tok(SCOLON), BASIC_TYPE);
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new Parameter((NameList)n.get(0), (TypeLiteral)n.get(2));
        }
    },
    COMPOUND_STATEMENT(14) {
        @Override
        protected IParser rule()
        {
            return seq(tok(SBEGIN), STATEMENT_LIST, tok(SEND));
        }
        
        @Override
        protected INode success(INode node)
        {
            return ((SequenceNode)node).get(1);
        }
    },
    STATEMENT_LIST(15) {
        @Override
        protected IParser rule()
        {
            return seq(STATEMENT, rep0seq(tok(SSEMICOLON), STATEMENT));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            
            final StatementList list = new StatementList((IStatement)n.get(0));
            
            for (final INode c: n.getAsSeq(1).getChildren())
            {
                list.add((IStatement)((SequenceNode)c).get(1));
            }
            return list;
        }
    },
    STATEMENT(16) {
        @Override
        protected IParser rule()
        {
            return firstsel(
                IF_STATEMENT, // if
                WHILE_STATEMENT, // while
                RW_STATEMENT,
                COMPOUND_STATEMENT, // begin
                looksel(
                    pair(seq(tok(SIDENTIFIER), firstsel(tok(SASSIGN), tok(SLBRACKET))), ASSIGN_STATEMENT),
                    pair(tok(SIDENTIFIER), PROCCALL_STATEMENT)
                )
            );
        }
    },
    IF_STATEMENT(17) {
        @Override
        protected IParser rule()
        {
            return seq(tok(SIF), EXPRESSION, tok(STHEN), COMPOUND_STATEMENT, optseq(tok(SELSE), COMPOUND_STATEMENT));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            if (n.get(4) instanceof EmptyNode)
            {
                return new IfStatement((ITyped)n.get(1), (StatementList)n.get(3));
            }
            else
            {
                return new IfElseStatement(
                    (ITyped)n.get(1), (StatementList)n.get(3), (StatementList)n.getAsSeq(4).get(1)
                );
            }
        }
    },
    WHILE_STATEMENT(18) {
        @Override
        protected IParser rule()
        {
            return seq(tok(SWHILE), EXPRESSION, tok(SDO), STATEMENT);
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new WhileStatement((ITyped)n.get(1), (IStatement)n.get(3));
        }
    },
    ASSIGN_STATEMENT(19) {
        @Override
        protected IParser rule()
        {
            return seq(VARIABLE, tok(SASSIGN), EXPRESSION);
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new AssignStatement((IVariable)n.get(0), (ITyped)n.get(2));
        }
    },
    VARIABLE(20) {
        @Override
        protected IParser rule()
        {
            return looksel(
                pair(seq(tok(SIDENTIFIER), tok(SLBRACKET)), INDEXED_VARIABLE),
                pair(seq(tok(SIDENTIFIER)), PURE_VARIABLE)
            );
        }
    },
    PURE_VARIABLE(21) {
        @Override
        protected IParser rule()
        {
            return NAME;
        }
        
        @Override
        protected INode success(INode node)
        {
            return new PureVariable((Identifier)node);
        }
    },
    INDEXED_VARIABLE(22) {
        @Override
        protected IParser rule()
        {
            return seq(NAME, tok(SLBRACKET), EXPRESSION, tok(SRBRACKET));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new IndexedVariable((Identifier)n.get(0), (ITyped)n.get(2));
        }
    },
    PROCCALL_STATEMENT(23) {
        @Override
        protected IParser rule()
        {
            return seq(NAME, optseq(tok(SLPAREN), EXPRESSION_LIST, tok(SRPAREN)));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new ProcCallStatement(
                (Identifier)n.get(0),
                n.get(1) instanceof EmptyNode? new ExpressionList(): (ExpressionList)n.getAsSeq(1).get(1)
            );
        }
    },
    EXPRESSION_LIST(24) {
        @Override
        protected IParser rule()
        {
            return seq(EXPRESSION, rep0seq(tok(SCOMMA), EXPRESSION));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            
            final ExpressionList list = new ExpressionList((ITyped)n.get(0));
            
            for (final INode c: n.getAsSeq(1).getChildren())
            {
                list.add((ITyped)((SequenceNode)c).get(1));
            }
            return list;
        }
    },
    EXPRESSION(25) {
        @Override
        protected IParser rule()
        {
            return seq(SIMPLE_EXPRESSION, optseq(COMP_OP, SIMPLE_EXPRESSION));
        }
        
        @Override
        protected INode success(INode node)
        {
            /*final SequenceNode n = (SequenceNode)node;
            
            if (n.get(1) instanceof EmptyNode)
            {
                return new Expression((SimpleExpression)n.get(0));
            }
            else
            {
                final SequenceNode right = n.getAsSeq(1);
                return new CompareExpression(
                    (SimpleExpression)n.get(0),
                    (CompareOperator)right.get(0),
                    (SimpleExpression)right.get(1)
                );
            }*/
            
            ITyped left = (ITyped)((SequenceNode)node).get(0);
            final INode right = ((SequenceNode)node).get(1);
            
            if(right instanceof EmptyNode)
            {
                return left;
            }
            else
            {
                final SequenceNode sn = (SequenceNode)right;
                return new InfixOperation(left, (ITyped)sn.get(1), (TokenNode)sn.get(0));
            }
        }
    },
    SIMPLE_EXPRESSION(26) {
        @Override
        protected IParser rule()
        {
            return seq(opt(SIGN), TERM, rep0seq(ADD_OP, TERM));
        }
        
        @Override
        protected INode success(INode node)
        {
            
            final SequenceNode n = (SequenceNode)node;
            ITyped head = (ITyped)n.get(1);
            final SequenceNode tail = n.getAsSeq(2);
            
            if(!(n.get(0) instanceof EmptyNode))
            {
                head = new PrefixOperation(head, ((SignLiteral)n.get(0)).getToken());
            }
            
            if(tail.isEmpty())
            {
                return head;
            }

            for (final INode c: tail.getChildren())
            {
                final SequenceNode sn = (SequenceNode)c;
                head = new InfixOperation(head, (ITyped)sn.get(1), (TokenNode)sn.get(0));
            }

            return head;
        }
    },
    TERM(27) {
        @Override
        protected IParser rule()
        {
            return seq(FACTOR, rep0seq(MUL_OP, FACTOR));
        }
        
        @Override
        protected INode success(INode node)
        {
            ITyped head = (ITyped)((SequenceNode)node).get(0);
            final SequenceNode tail = ((SequenceNode)node).getAsSeq(1);
            
            if(tail.isEmpty())
            {
                return head;
            }

            for (final INode c: tail.getChildren())
            {
                final SequenceNode sn = (SequenceNode)c;
                head = new InfixOperation(head, (ITyped)sn.get(1), (TokenNode)sn.get(0));
            }

            return head;
        }
    },
    FACTOR(28) {
        @Override
        protected IParser rule()
        {
            return firstsel(
                VARIABLE,
                CONSTANT,
                NOT,
                seq(tok(SLPAREN), EXPRESSION, tok(SRPAREN))
            );
        }
        
        @Override
        protected INode success(INode node)
        {
            if (node instanceof SequenceNode)
            {
                final SequenceNode n = (SequenceNode)node;
                return n.get(1);
            }
            else
            {
                return node;
            }
        }
    },
    NOT(29) {
        @Override
        protected IParser rule()
        {
            return seq(tok(SNOT), FACTOR);
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new PrefixOperation((ITyped)n.get(1), (TokenNode)n.get(0));
        }
    },
    COMP_OP(30) {
        @Override
        protected IParser rule()
        {
            return firstsel(
                tok(SEQUAL), tok(SNOTEQUAL),
                tok(SLESS), tok(SLESSEQUAL),
                tok(SGREAT), tok(SGREATEQUAL)
            );
        }
        
        @Override
        protected INode failure(INode node)
        {
            return new FailureNode(((FailureNode)node).getChild(), "CompareOperator expected.");
        }
        
    },
    ADD_OP(31) {
        @Override
        protected IParser rule()
        {
            return firstsel(tok(SPLUS), tok(SMINUS), tok(SOR));
        }
        
        @Override
        protected INode failure(INode node)
        {
            return new FailureNode(((FailureNode)node).getChild(), "AddOperator expected.");
        }
    },
    MUL_OP(32) {
        @Override
        protected IParser rule()
        {
            return firstsel(tok(SSTAR), tok(SDIVD), tok(SMOD), tok(SAND));
        }
        
        @Override
        protected INode failure(INode node)
        {
            return new FailureNode(((FailureNode)node).getChild(), "MultiplyOperator expected.");
        }
    },
    RW_STATEMENT(33) {
        
        @Override
        protected IParser rule()
        {
            return firstsel(READ_STATEMENT, WRITE_STATEMENT);
        }
    },
    READ_STATEMENT(34) {
        @Override
        protected IParser rule()
        {
            return seq(tok(SREADLN), optseq(tok(SLPAREN), VARIABLE_LIST, tok(SRPAREN)));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new ReadStatement(
                n.get(1) instanceof EmptyNode? new VariableList(): (VariableList)n.getAsSeq(1).get(1)
            );
        }
    },
    WRITE_STATEMENT(35) {
        @Override
        protected IParser rule()
        {
            return seq(tok(SWRITELN), optseq(tok(SLPAREN), EXPRESSION_LIST, tok(SRPAREN)));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new WriteStatement(
                n.get(1) instanceof EmptyNode? new ExpressionList(): (ExpressionList)n.getAsSeq(1).get(1)
            );
        }
    },
    VARIABLE_LIST(36) {
        @Override
        protected IParser rule()
        {
            return seq(VARIABLE, rep0seq(tok(SCOMMA), VARIABLE));
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            
            final VariableList list = new VariableList((IVariable)n.get(0));
            
            for (final INode c: n.getAsSeq(1).getChildren())
            {
                list.add((IVariable)((SequenceNode)c).get(1));
            }
            return list;
        }
    },
    CONSTANT(37) {
        @Override
        protected IParser rule()
        {
            return firstsel(UNSIGNED_INT, STRING, BOOLEAN);
        }
        
        @Override
        protected INode failure(INode node)
        {
            return new FailureNode(((FailureNode)node).getChild(), "Constant expected.");
        }
    },
    BOOLEAN(38) {
        @Override
        protected IParser rule()
        {
            return firstsel(tok(SFALSE), tok(STRUE));
        }
        
        @Override
        protected INode success(INode node)
        {
            return new BooleanLiteral(((TokenNode)node).getToken());
        }
        
        @Override
        protected INode failure(INode node)
        {
            return new FailureNode(((FailureNode)node).getChild(), "BooleanLiteral expected.");
        }
    },
    UNSIGNED_INT(39) {
        @Override
        protected IParser rule()
        {
            return tok(SCONSTANT);
        }
        
        @Override
        protected INode success(INode node)
        {
            return new IntegerLiteral(((TokenNode)node).getToken());
        }
    },
    STRING(40) {
        @Override
        protected IParser rule()
        {
            return tok(SSTRING);
        }
        
        @Override
        protected INode success(INode node)
        {
            return new StringLiteral((TokenNode)node);
        }
    },
    NAME(41) {
        @Override
        protected IParser rule()
        {
            return tok(SIDENTIFIER);
        }
        
        @Override
        protected INode success(INode node)
        {
            return new Identifier((TokenNode)node);
        }
    };
    
    private IParser parser_memo;
    
    PascalParser(int id)
    {
        parser_memo = null;
    }
    
    private IParser getRule()
    {
        if (parser_memo == null)
        {
            parser_memo = rule();
        }
        return parser_memo;
    }
    
    public final static IParser getFullParser()
    {
        return PROGRAM;
    }
    
    public final static INode fullparse(List<LexedToken> input)
    {
        return getFullParser().parse(new ParserInput(input));
    }
    
    @Override
    public Set<TokenType> getFirstSet()
    {
        return getRule().getFirstSet();
    }
    
    @Override
    public final INode parse(ParserInput input)
    {
        begin();
        final INode node = getRule().parse(input);
        INode res = node.isSuccess()? success(node): failure(node);
        return res;
    }
    
    protected abstract IParser rule();
    
    protected void begin()
    {
        // Empty
    }
    
    protected INode success(INode node)
    {
        return node;
    }
    
    protected INode failure(INode node)
    {
        node.println();
        System.out.println();
        if (node instanceof FailureNode)
        {
            return new FailureNode(node, "Found in " + toString() + ".");
        }
        else
        {
            assert false;
            return null;
        }
    }
}
