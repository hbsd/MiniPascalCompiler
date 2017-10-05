package enshud.s1.lexer;


import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.nio.file.Files;

public class Lexer {
	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		new Lexer().run("data/pas/normal01.pas", "tmp/out1.ts");
		new Lexer().run("data/pas/normal02.pas", "tmp/out2.ts");
	}

	/**
	 * 開発対象となるLexer実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたpasファイルを読み込み，トークン列に分割する．
	 * トークン列は第二引数で指定されたtsファイルに書き出すこと．
	 * 正常に処理が終了した場合は標準出力に"OK"を，
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 * 
	 * @param inputFileName 入力pasファイル名
	 * @param outputFileName 出力tsファイル名
	 */
	public void run(final String inputFileName, final String outputFileName) {
        Lexer lexer = new Lexer();

        if(!lexer.lexFromFile(inputFileName))
        {
        	return;
        }
        // lexer.println();
        lexer.outputToFile(outputFileName);
        System.out.println("OK");
	}
	


    private List<LexedToken> tokens;


    /**
     * 第一引数で指定されたpasファイルを読み込み，トークン列に分割する．
     * @param input_file 入力pasファイル名
     * @return ファイル読み込みエラー時はfalse
     */
    public boolean lexFromFile(String input_file)
    {
        tokens = new ArrayList<>();

        try{
            List<String> lines = Files.readAllLines(Paths.get(input_file));
            lex(lines);
            return true;
        }
        catch (IOException e)
        {
            System.err.println("File not found");
            return false;
        }
    }

    private void lex(List<String> lines)
    {
        int line_cnt = 1;
        boolean is_in_comment = false;

        for (final String line: lines)
        {
            is_in_comment = lexLine(line, line_cnt, is_in_comment);
            ++line_cnt;
        }
    }

    private boolean lexLine(final String line, final int line_cnt, boolean is_in_comment)
    {
        int col_idx = 0;

        while(col_idx < line.length())
        {
            // when inside comment
            if(is_in_comment)
            {
                Token token = TokenType.SCOMMENTEND.lex(line.substring(col_idx));
                if(token != null)
                {
                    is_in_comment = false; // finish ignoring
                }

                // next
                ++col_idx;
            }

            // when NOT inside comment
            if(!is_in_comment)
            {
                Token token = TokenType.lexOneToken(line.substring(col_idx));
                switch(token.type)
                {
                case SSPACE:      // simply ignore
                    break;

                case SCOMMENTBEG: // start ignoring
                    is_in_comment = true;
                    break;

                case SCOMMENTEND:
                case SUNKNOWN:
                    // error output
                    System.err.println(
                        "Illegal Character(" + line_cnt + "," + col_idx + "): " + token.str
                    );
                    // display error column
                    // (when include multibyte char,
                    //  this output will be wrong position)
                    System.err.println(line);
                    for(int i = 1; i < col_idx; ++i)
                    {
                        System.err.print(" ");
                    }
                    System.err.println("^");
                    System.exit(1);

                default:
                    tokens.add(new LexedToken(token, line_cnt, col_idx + 1));
                    break;
                }

                // next
                col_idx += token.str.length();
            }
        }

        return is_in_comment;
    }

    /**
     * 分割したトークン列を第一引数で指定されたファイルに書き出す．
     * @param output_name 出力tsファイル名
     */
    public void outputToFile(String output_name)
    {
        try(
            PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(Paths.get(output_name))
            )
        )
        {
            tokens.forEach(writer::println);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void println()
    {
        tokens.forEach(System.out::println);
    }





}
