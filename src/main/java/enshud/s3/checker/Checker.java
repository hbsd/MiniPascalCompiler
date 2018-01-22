package enshud.s3.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.text.similarity.LevenshteinDistance;

import enshud.pascal.Procedure;
import enshud.pascal.ast.declaration.ProcedureDeclaration;
import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.Lexer;
import enshud.s2.parser.Parser;
import enshud.s2.parser.node.INode;


public class Checker
{
    /**
     * サンプルmainメソッド． 単体テストの対象ではないので自由に改変しても良い．
     */
    public static void main(final String[] args)
    {
        // normalの確認
        // new Checker().run("data/ts/normal03.ts");
        // new Checker().run("data/ts/normal05.ts");
        
        // synerrの確認
        // new Checker().run("data/ts/synerr01.ts");
        // new Checker().run("data/ts/synerr02.ts");
        
        // semerrの確認
        // new Checker().run("data/ts/semerr01.ts");
        // new Checker().run("data/ts/semerr04.ts");
        IntStream.rangeClosed(1, 8).forEachOrdered(
            i -> {
                System.err.println("semerr" + i);
                new Lexer().run("data/pas/semerr0" + i + ".pas", "tmp/out.ts");
                new Checker().run("tmp/out.ts");
            }
        );
    }
    
    /**
     * TODO
     * 
     * 開発対象となるChecker実行メソッド． 以下の仕様を満たすこと．
     * 
     * 仕様: 第一引数で指定されたtsファイルを読み込み，意味解析を行う． 意味的に正しい場合は標準出力に"OK"を，正しくない場合は"Semantic
     * error: line"という文字列とともに， 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Semantic error:
     * line 6"）． また，構文的なエラーが含まれる場合もエラーメッセージを表示すること（例： "Syntax error: line 1"）．
     * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを出力すること． 入力ファイルが見つからない場合は標準エラーに"File
     * not found"と出力して終了すること．
     * 
     * @param inputFileName
     *            入力tsファイル名
     */
    public void run(final String inputFileName)
    {
        fromLexedFile(inputFileName)
            .filter(checker -> checker.isSuccess())
            .ifPresent(a -> System.out.println("OK"));
    }
    
    public static Optional<Checker> fromLexedFile(String input_file)
    {
        return Parser.fromLexedFile(input_file)
            .flatMap(parser -> parser.getProgram())
            .map(Checker::check);
    }
    
    public static Checker check(ProcedureDeclaration prg)
    {
        Checker c = new Checker();
        c.check_(prg);
        return c;
    }
    
    private static final boolean DETAIL_ERROR_MSG = false;
    private static final int     NUMBER_TO_PRINT  = 1;  // < 0 : print all
    
    
    private static final LevenshteinDistance DISTANCE = new LevenshteinDistance();
    
    public static final boolean isSimilar(CharSequence s1, CharSequence s2)
    {
        final double threshold = 0.3;
        return DISTANCE.apply(s1, s2) < threshold * (s1.length() + s2.length());
    }
    
    private Procedure          program = null;
    private final List<String> errors  = new ArrayList<>();
    
    public void addErrorMessage(String proc_name, int line, int column, String msg)
    {
        if (DETAIL_ERROR_MSG)
        {
            errors.add(
                String.format(
                    "(near line %4d, col %4d in %8s): %s", line, column, proc_name, msg
                )
            );
        }
        else
        {
            errors.add("Semantic error: line " + line);
        }
    }
    
    public void addErrorMessage(Procedure proc, INode node, String msg)
    {
        addErrorMessage(proc.getQualifiedName(), node.getLine(), node.getColumn(), msg);
    }
    
    public void addErrorMessage(Procedure proc, LexedToken token, String msg)
    {
        addErrorMessage(proc.getQualifiedName(), token.getLine(), token.getColumn(), msg);
    }
    
    public void printErrorMessage(int num)
    {
        if (num < 0)
        {
            errors.stream()
                .forEachOrdered(System.err::println);
        }
        else
        {
            errors.stream()
                .limit(num)
                .forEachOrdered(System.err::println);
        }
    }
    
    public boolean isSuccess()
    {
        return errors.isEmpty();
    }
    
    public Optional<Procedure> getProgram()
    {
        return Optional.ofNullable(program);
    }
    
    
    private void check_(ProcedureDeclaration prg)
    {
        program = Procedure.create(this, prg);
        if (!isSuccess())
        {
            program = null;
            printErrorMessage(NUMBER_TO_PRINT);
        }
    }
    
    public static String getOrderString(int num)
    {
        if (num == 11 || num == 12)
        {
            return num + "th";
        }
        switch (num % 10)
        {
        case 1:
            return num + "st";
        case 2:
            return num + "nd";
        case 3:
            return num + "rd";
        default:
            return num + "th";
        }
    }
}
