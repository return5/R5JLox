package main.java.com.github.return5.jlox;

public class JLox {

    public static void main(final String[] args) {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        else if (args.length == 1){
            runFile(args[0]);
        }
        else {
            runPrompt();
        }
    }
}
