package main.java.com.github.return5.jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JLox {

    static boolean hadError = false;

    public static void main(final String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        else if (args.length == 1){
            runFile(args[0]);
        }
        else {
            repl();
        }
    }

    private static void runFile(final String path) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) {
            System.exit(65);
        }
    }

    private static void repl() throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(input);
        while(true) {
            System.out.print("> ");
            final String line = reader.readLine();
            if(line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    private static void run(final String resource) {
        final Scanner scanner = new Scanner(resource);
        final List<Token<?>> tokens = scanner.scanTokens();
        tokens.forEach(System.out::println);
    }

    static void error(final int line, final String message) {
        report(line,"",message);
    }

    private static void report(final int line, final String where, final String message) {
        System.err.println("[line " + line + " ] Error: " + where + ": " + message);
        hadError = true;
    }
}
