package enshud.s4.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import enshud.casl.CaslSimulator;
import enshud.s1.lexer.Lexer;
import enshud.s3.checker.Checker;


public class Compiler
{
    /**
     * サンプルmainメソッド． 単体テストの対象ではないので自由に改変しても良い．
     */
    public static void main(final String[] args)
    {
        // Compilerを実行してcasを生成する
        new Lexer().run("data/pas/test.pas", "tmp/out.ts");
        new Compiler().run("tmp/out.ts", "tmp/out.cas");
        // new Compiler().run("data/ts/normal02.ts", "tmp/out.cas");
        
        // CaslSimulatorクラスを使ってコンパイルしたcasを，CASLアセンブラ & COMETシミュレータで実行する
        CaslSimulator.run("tmp/out.cas", "tmp/out.ans", "36", "48");
    }
    
    /**
     * TODO
     * 
     * 開発対象となるCompiler実行メソッド． 以下の仕様を満たすこと．
     * 
     * 仕様: 第一引数で指定されたtsファイルを読み込み，CASL IIプログラムにコンパイルする． コンパイル結果のCASL
     * IIプログラムは第二引数で指定されたcasファイルに書き出すこと．
     * 構文的もしくは意味的なエラーを発見した場合は標準エラーにエラーメッセージを出力すること．
     * （エラーメッセージの内容はChecker.run()の出力に準じるものとする．） 入力ファイルが見つからない場合は標準エラーに"File not
     * found"と出力して終了すること．
     * 
     * @param inputFileName
     *            入力tsファイル名
     * @param outputFileName
     *            出力casファイル名
     */
    public void run(final String inputFileName, final String outputFileName)
    {
        fromLexedFile(inputFileName)
            .map(
                cis -> cis
                    .stream()
                    .map(ci -> ci.toString())
                    .collect(Collectors.toList())
            )
            .ifPresent(
                code -> {
                    if (outputToFile(outputFileName, code))
                    {
                        CaslSimulator.appendLibcas(outputFileName);
                        System.out.println("OK");
                    }
                }
            );
    }
    
    public static Optional<Casl2Code> fromLexedFile(String input_file)
    {
        return Checker.fromLexedFile(input_file)
            .flatMap(checker -> checker.getProgram())
            .map(proc -> proc.compile(new Casl2Code()));
    }
    
    public static boolean outputToFile(String output_name, List<? extends CharSequence> code)
    {
        try
        {
            Files.write(Paths.get(output_name), code);
            return true;
        }
        catch (final IOException e)
        {
            System.err.println("File not found");
            return false;
        }
    }
}
