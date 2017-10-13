package compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import checker.Checker;
import lexer.LexedToken;
import lexer.Lexer;
import parser.Parser;
import pascal.Procedure;
import pascal.ast.Program;


public class Compiler
{

    public static void main(String[] args)
    {
        if( args.length != 1 && args.length != 2 )
        {
            System.err.println("Usange: compiler input [output]");
            return;
        }

        // args = new String[]{"testdata/pas/normal_03.pas", "normal_03.cas"};

        // List<LexedToken> tokens = Lexer.importLexedFile(args[1]);
        final List<String> lines = Lexer.importFromFile(args[0]);

        final List<LexedToken> tokens = Lexer.lexing(lines);
        if( tokens == null )
        {
            return;
        }

        final Program root = Parser.parsing(tokens, lines);
        if( root == null )
        {
            return;
        }
        Procedure proc = Checker.checking(root);
        if( proc == null )
        {
            return;
        }
        compiling(proc, args.length == 2? args[1]: args[0].replaceFirst("\\.[^.]+$", "\\.cas"));
    }

    static void compiling(Procedure proc, String output_name)
    {
        StringBuilder sb = new StringBuilder();
        proc.compile(sb);
        try
        {
            Files.write(Paths.get(output_name), Arrays.asList(sb.toString()));
            System.out.println("Compiler Success!");
        }
        catch(IOException e)
        {
            System.err.println("IOException");
            System.exit(1);
        }
    }
}
