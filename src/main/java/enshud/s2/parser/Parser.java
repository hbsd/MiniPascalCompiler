package enshud.s2.parser;

import java.util.List;
import java.util.Optional;

import enshud.pascal.PascalParser;
import enshud.pascal.ast.ProcedureDeclaration;
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
        Parser.fromLexedFile(inputFileName)
            .flatMap(parser -> parser.getProgram())
            .ifPresent(a -> System.out.println("OK"));
    }
    
    public static Optional<Parser> fromLexedFile(String input_file)
    {
        return Lexer.importLexedFile(input_file)
                .map(Parser::parse);
    }
    
    private INode         root;
    private final IParser parser;
    
    public static Parser parse(List<LexedToken> input)
    {
        Parser p = new Parser();
        p.parse_(input);
        return p;
    }
    
    @Deprecated
    public Parser()
    {
        parser = PascalParser.getFullParser();
    }
    
    private void parse_(List<LexedToken> input)
    {
        root = parser.parse(new ParserInput(input));
        if (root.isFailure())
        {
            // root.println();
            System.err.println("Syntax error: line " + root.getLine());
        }
    }
    
    public INode getRoot()
    {
        return root;
    }
    
    public Optional<ProcedureDeclaration> getProgram()
    {
        if (getRoot().isSuccess())
        {
            return Optional.of((ProcedureDeclaration)getRoot());
        }
        else
        {
            return Optional.empty();
        }
    }
}
