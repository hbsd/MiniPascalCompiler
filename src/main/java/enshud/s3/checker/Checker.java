package enshud.s3.checker;

import java.util.ArrayList;
import java.util.List;

import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.Lexer;
import enshud.s2.parser.Parser;
import enshud.s3.checker.ast.IASTNode;
import enshud.s3.checker.ast.Program;

public class Checker {
	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		//new Checker().run("data/ts/normal01.ts");
		//new Checker().run("data/ts/normal02.ts");

		// synerrの確認
		//new Checker().run("data/ts/synerr01.ts");
		//new Checker().run("data/ts/synerr02.ts");

		// semerrの確認
		//new Checker().run("data/ts/semerr01.ts");
		new Checker().run("data/ts/semerr08.ts");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるChecker実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，意味解析を行う．
	 * 意味的に正しい場合は標準出力に"OK"を，正しくない場合は"Semantic error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Semantic error: line 6"）．
	 * また，構文的なエラーが含まれる場合もエラーメッセージを表示すること（例： "Syntax error: line 1"）．
	 * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを出力すること．
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 * 
	 * @param inputFileName 入力tsファイル名
	 */
	public void run(final String inputFileName) {
        final List<LexedToken> tokens = Lexer.importLexedFile(inputFileName);
        if( tokens == null )
        {
            return;
        }

        final Program root = new Parser().parse(tokens);
        if( root == null )
        {
            return;
        }
        
        check(root);
        if( isSuccess() )
        {
            System.out.println("OK");
        }
	}

	static final boolean SIMPLE_ERROR_MSG = true;

    Procedure program = null;
    List<String> errors = new ArrayList<>();

    
    public void addErrorMessage(Procedure proc, IASTNode node, String msg)
    {
    	if(SIMPLE_ERROR_MSG){
        	errors.add("Semantic error: line " + node.getLine());
    	} else {
            errors.add(String.format("(near line %4d, col %4d in %8s): %s", node.getLine(), node.getColumn(), proc.getName(), msg));
    	}
    }

    public void printErrorMessage()
    {
        for(final String msg: errors)
        {
            System.err.println(msg);
        }
    }
    
    public void printErrorMessage(int num)
    {
    	int i = 0;
        for(final String msg: errors)
        {
            System.err.println(msg);
            ++i;
        	if(i >= num)
        	{
        		break;
        	}
        }
    }

    public boolean isSuccess()
    {
        return errors.isEmpty();
    }
    
    public Procedure getProgram() {
		return program;
	}


    public Procedure check(Program prg)
    {
        program = new Procedure(this, prg);
        if( !isSuccess() )
        {
            program = null;
            printErrorMessage(1);
        }
        return program;
    }
    
    public static String getOrderString(int num)
    {
        if(num == 11 || num == 12)
        {
            return num + "th";
        }
        switch( num % 10 )
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
