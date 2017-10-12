package enshud.s2.parser.node;

public interface INode
{
    final static boolean COLOR_ENABLE = true;

    boolean isSuccess();

    default boolean isFailure()
    {
        return !isSuccess();
    }

    int getLine();
    int getColumn();



    default void println()
    {
        println("");
    }

    default void println(String indent)
    {
        println(indent, null);
    }

    default void println(String indent, String msg)
    {
        printIndent(
            indent.length() > 0? indent.substring(0, indent.length() - 2) + " |" // force
                                                                                 // indent
                                                                                 // line
                    : ""
        );
        printHead(indent, msg);
        printBodyln(indent);
        // printIndentln(indent);
    }

    default void printHead(String indent, String msg)
    {
        System.out.print("-");
        printBlue(getClass().getSimpleName());
        System.out.print(": ");
        printGreenln(msg != null? msg: toString());
    }

    default void printBodyln(String indent)
    {
        // Empty
    }

    static void printIndent(String indent)
    {
        System.out.print(indent);
        /*
         * for(int i = 0; i < indent; ++i) { System.out.print(" |"); }
         */
    }

    static void printIndentln(String indent)
    {
        printIndent(indent);
        System.out.println();
    }

    static void colorprint(int color_code, String msg)
    {
        if( COLOR_ENABLE )
        {
            System.out.print("\033[" + color_code + "m" + msg + "\033[0m");
        }
        else
        {
            System.out.print(msg);
        }
    }

    static void printCyan(String msg)
    {
        colorprint(36, msg);
    }

    static void printCyanln(String msg)
    {
        printBlue(msg);
        System.out.println();
    }

    static void printGreen(String msg)
    {
        colorprint(32, msg);
    }

    static void printGreenln(String msg)
    {
        printGreen(msg);
        System.out.println();
    }

    static void printPurple(String msg)
    {
        colorprint(35, msg);
    }

    static void printPurpleln(String msg)
    {
        printPurple(msg);
        System.out.println();
    }

    static void printYellow(String msg)
    {
        colorprint(33, msg);
    }

    static void printYellowln(String msg)
    {
        printPurple(msg);
        System.out.println();
    }

    static void printRed(String msg)
    {
        colorprint(31, msg);
    }

    static void printRedln(String msg)
    {
        printPurple(msg);
        System.out.println();
    }

    static void printBlue(String msg)
    {
        colorprint(34, msg);
    }

    static void printBlueln(String msg)
    {
        printPurple(msg);
        System.out.println();
    }
}


