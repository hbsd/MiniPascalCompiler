package enshud.s2.parser;

import java.util.List;
import java.util.Objects;

import enshud.pascal.PascalParser;
import enshud.pascal.ast.Program;
import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.Lexer;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.parsers.IParser;


public class Parser
{
    /**
     * サンプルmainメソッド． 単体テストの対象ではないので自由に改変しても良い．
     */
    public static void main(final String[] args)
    {
        // normalの確認
        // new Parser().run("data/ts/normal01.ts");
        // new Parser().run("data/ts/normal02.ts");
        
        // synerrの確認
        // new Parser().run("data/ts/synerr01.ts");
        new Parser().run("data/ts/synerr03.ts");
    }
    
    /**
     * TODO
     * 
     * 開発対象となるParser実行メソッド． 以下の仕様を満たすこと．
     * 
     * 仕様: 第一引数で指定されたtsファイルを読み込み，構文解析を行う． 構文が正しい場合は標準出力に"OK"を，正しくない場合は"Syntax
     * error: line"という文字列とともに， 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Syntax error:
     * line 1"）． 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを出力すること．
     * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
     * 
     * @param inputFileName
     *            入力tsファイル名
     */
    public void run(final String inputFileName)
    {
        final List<LexedToken> tokens = Lexer.importLexedFile(inputFileName);
        if (tokens != null)
        {
            final INode root = parse(tokens);
            if (root != null)
            {
                System.out.println("OK");
            }
        }
    }
    
    INode   root;
    IParser parser;
    
    public Parser(IParser parser)
    {
        this.parser = Objects.requireNonNull(parser);
    }
    
    public Parser()
    {
        this(PascalParser.getFullParser());
    }
    
    public Program parse(ParserInput input)
    {
        final INode root = parser.parse(input);
        if (root.isSuccess())
        {
            return (Program)root;
        }
        else
        {
            // root.println();
            System.err.println("Syntax error: line " + root.getLine());
            /*
             * final LexedToken t = ((IParserNode)root).getToken();
             * 
             * // indicate error column // (when include multibyte char, // this
             * output will be wrong position) if( t.getLine() >= 2 ) {
             * System.out.println(lines.get(t.getLine() - 2)); }
             * System.out.println(lines.get(t.getLine() - 1)); for(int i = 1; i
             * < t.getColumn(); ++i) { System.out.print(" "); }
             * System.out.println("^");
             * 
             * System.out.println("Parser Failure!");
             */
            return null;
        }
    }
    
    public Program parse(List<LexedToken> input)
    {
        return parse(new ParserInput(input));
    }
    
    public INode getRoot()
    {
        return root;
    }
}
