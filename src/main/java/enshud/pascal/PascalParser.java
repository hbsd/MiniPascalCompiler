package enshud.pascal;

import java.util.List;
import java.util.Set;

import enshud.pascal.ast.*;
import enshud.pascal.ast.declaration.*;
import enshud.pascal.ast.expression.*;
import enshud.pascal.ast.statement.*;
import enshud.pascal.type.BasicType;
import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.TokenType;

import static enshud.s1.lexer.TokenType.*;
import static enshud.s2.parser.parsers.Parsers.*;

import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.EmptyNode;
import enshud.s2.parser.node.FailureNode;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.SequenceNode;
import enshud.s2.parser.node.TokenNode;
import enshud.s2.parser.parsers.IParser;


@SuppressWarnings("unchecked")
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
            
            return new ProcedureDeclaration(
                (Identifier)n.get(1),
                new NodeList<>(),
                n.get(6) instanceof EmptyNode? new NodeList<>()
                        : (NodeList<LocalDeclaration>)n.getAsSeq(6).get(1),
                (NodeList<ProcedureDeclaration>)n.get(7),
                (CompoundStatement)n.get(8)
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
            
            final NodeList<Identifier> list = new NodeList<>((Identifier)n.get(0));
            
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
            final NodeList<LocalDeclaration> list = new NodeList<>();
            
            for (final INode c: n.getChildren())
            {
                list.add((LocalDeclaration)c);
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
            return new LocalDeclaration((NodeList<Identifier>)n.get(0), (TypeLiteral)n.get(2));
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
                (BasicType)((TypeLiteral)n.get(7)).getType(),
                (IntegerLiteral)n.get(2),
                (IntegerLiteral)n.get(4),
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
            
            if (n.get(0) instanceof EmptyNode)
            {
                return n.get(1);
            }
            else
            {
                switch (((TokenNode)n.get(0)).getType())
                {
                case SPLUS:
                    return n.get(1);
                case SMINUS: {
                    int num = ((IntegerLiteral)n.get(1)).getValue().getInt();
                    return IntegerLiteral.create(-num);
                }
                default:
                    assert false;
                    return null;
                }
                
            }
            
            /*
             * return new SignedInteger( n.get(0) instanceof EmptyNode?
             * SignLiteral.NONE: (SignLiteral)n.get(0), (IntegerLiteral)n.get(1)
             * );
             */
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
            return node;// new SignLiteral(((TokenNode)node).getToken());
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
            final NodeList<ProcedureDeclaration> list = new NodeList<>();
            
            for (final INode c: ((SequenceNode)node).getChildren())
            {
                list.add((ProcedureDeclaration)c);
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
                SUBPROGRAM_DECL_LIST,
                COMPOUND_STATEMENT,
                tok(SSEMICOLON)
            );
        }
        
        @Override
        protected INode success(INode node)
        {
            final SequenceNode n = (SequenceNode)node;
            return new ProcedureDeclaration(
                (Identifier)n.get(1),
                n.get(2) instanceof EmptyNode? new NodeList<>(): (NodeList<ParameterDeclaration>)n.getAsSeq(2).get(1),
                n.get(4) instanceof EmptyNode? new NodeList<>()
                        : (NodeList<LocalDeclaration>)n.getAsSeq(4).get(1),
                (NodeList<ProcedureDeclaration>)n.get(5), // TODO: let procedure
                                                          // have child
                                                          // procedures
                (CompoundStatement)n.get(6)
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
            
            final NodeList<ParameterDeclaration> list = new NodeList<>((ParameterDeclaration)n.get(0));
            
            for (final INode c: n.getAsSeq(1).getChildren())
            {
                list.add((ParameterDeclaration)((SequenceNode)c).get(1));
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
            return new ParameterDeclaration((NodeList<Identifier>)n.get(0), (TypeLiteral)n.get(2));
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
            
            final CompoundStatement list = new CompoundStatement((IStatement)n.get(0));
            
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
                return new IfStatement((IExpression)n.get(1), (CompoundStatement)n.get(3));
            }
            else
            {
                return new IfElseStatement(
                    (IExpression)n.get(1), (CompoundStatement)n.get(3), (CompoundStatement)n.getAsSeq(4).get(1)
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
            return new WhileStatement((IExpression)n.get(1), (IStatement)n.get(3));
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
            return new AssignStatement((IVariable)n.get(0), (IExpression)n.get(2));
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
            return new IndexedVariable((Identifier)n.get(0), (IExpression)n.get(2));
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
                n.get(1) instanceof EmptyNode? new NodeList<>(): (NodeList<IExpression>)n.getAsSeq(1).get(1)
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
            
            final NodeList<IExpression> list = new NodeList<>((IExpression)n.get(0));
            
            for (final INode c: n.getAsSeq(1).getChildren())
            {
                list.add((IExpression)((SequenceNode)c).get(1));
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
            /*
             * final SequenceNode n = (SequenceNode)node;
             * 
             * if (n.get(1) instanceof EmptyNode) { return new
             * Expression((SimpleExpression)n.get(0)); } else { final
             * SequenceNode right = n.getAsSeq(1); return new CompareExpression(
             * (SimpleExpression)n.get(0), (CompareOperator)right.get(0),
             * (SimpleExpression)right.get(1) ); }
             */
            
            IExpression left = (IExpression)((SequenceNode)node).get(0);
            final INode right = ((SequenceNode)node).get(1);
            
            if (right instanceof EmptyNode)
            {
                return left;
            }
            else
            {
                final SequenceNode sn = (SequenceNode)right;
                return new InfixOperation(left, (IExpression)sn.get(1), (TokenNode)sn.get(0));
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
            IExpression head = (IExpression)n.get(1);
            final SequenceNode tail = n.getAsSeq(2);
            
            if (!(n.get(0) instanceof EmptyNode))
            {
                head = new PrefixOperation(head, (TokenNode)n.get(0));
            }
            
            if (tail.isEmpty())
            {
                return head;
            }
            
            for (final INode c: tail.getChildren())
            {
                final SequenceNode sn = (SequenceNode)c;
                head = new InfixOperation(head, (IExpression)sn.get(1), (TokenNode)sn.get(0));
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
            IExpression head = (IExpression)((SequenceNode)node).get(0);
            final SequenceNode tail = ((SequenceNode)node).getAsSeq(1);
            
            if (tail.isEmpty())
            {
                return head;
            }
            
            for (final INode c: tail.getChildren())
            {
                final SequenceNode sn = (SequenceNode)c;
                head = new InfixOperation(head, (IExpression)sn.get(1), (TokenNode)sn.get(0));
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
            return new PrefixOperation((IExpression)n.get(1), (TokenNode)n.get(0));
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
                n.get(1) instanceof EmptyNode? new NodeList<>(): (NodeList<IVariable>)n.getAsSeq(1).get(1)
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
                n.get(1) instanceof EmptyNode? new NodeList<>(): (NodeList<IExpression>)n.getAsSeq(1).get(1)
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
            
            final NodeList<IVariable> list = new NodeList<>((IVariable)n.get(0));
            
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
            final TokenNode tok = (TokenNode)node;
            return (tok.getString().length() == 3)
                    ? new CharLiteral(tok)
                    : new StringLiteral(tok);
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
        // node.println();
        // System.out.println();
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
