package enshud.s4.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import enshud.casl.CaslSimulator;
import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.Lexer;
import enshud.s2.parser.Parser;
import enshud.s2.parser.node.INode;
import enshud.s3.checker.Checker;
import enshud.s3.checker.Procedure;

public class Compiler {
	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// Compilerを実行してcasを生成する
		new Compiler().run("data/ts/normal04.ts", "tmp/out.cas");
		
		// CaslSimulatorクラスを使ってコンパイルしたcasを，CASLアセンブラ & COMETシミュレータで実行する
		CaslSimulator.run("tmp/out.cas", "tmp/out.ans", "36", "48");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるCompiler実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，CASL IIプログラムにコンパイルする．
	 * コンパイル結果のCASL IIプログラムは第二引数で指定されたcasファイルに書き出すこと．
	 * 構文的もしくは意味的なエラーを発見した場合は標準エラーにエラーメッセージを出力すること．
	 * （エラーメッセージの内容はChecker.run()の出力に準じるものとする．）
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 * 
	 * @param inputFileName 入力tsファイル名
	 * @param outputFileName 出力casファイル名
	 */
	public void run(final String inputFileName, final String outputFileName) {
		List<LexedToken> tokens = Lexer.importLexedFile(inputFileName);
        if( tokens == null )
        {
            return;
        }

        final INode root = new Parser().parse(tokens);
        if( root == null )
        {
            return;
        }
        
        Checker c = new Checker();
        if( !c.isSuccess() )
        {
        	c.printErrorMessage(1);
            return;
        }
        Procedure proc = c.getProgram();
        compiling(proc, outputFileName);
	}
	
	void compiling(Procedure proc, String output_name)
    {
        StringBuilder sb = new StringBuilder();
        //proc.compile(sb);
        try
        {
            Files.write(Paths.get(output_name), Arrays.asList(sb.toString()));
            System.out.println("OK");
        }
        catch(IOException e)
        {
            System.err.println("File not found");
        }
    }
}
