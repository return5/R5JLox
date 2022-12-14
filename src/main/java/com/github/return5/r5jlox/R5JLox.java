package main.java.com.github.return5.r5jlox;

import main.java.com.github.return5.r5jlox.errorhandler.ParserErrorHandler;
import main.java.com.github.return5.r5jlox.parser.Parser;
import main.java.com.github.return5.r5jlox.printer.AstPrinter;
import main.java.com.github.return5.r5jlox.scanner.Scanner;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.tree.Expr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class R5JLox {

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
        final ParserErrorHandler errorHandler = ParserErrorHandler.getParseErrorHandler();
        run(new String(bytes, Charset.defaultCharset()));
        if (errorHandler.isHadError()) {
            System.exit(65);
        }
    }

    private static void repl() throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(input);
        final ParserErrorHandler errorHandler = ParserErrorHandler.getParseErrorHandler();
        while(true) {
            System.out.print("> ");
            final String line = reader.readLine();
            if(line == null) {
                break;
            }
            run(line);
            errorHandler.setHadError(false);
        }
    }

    private static void run(final String resource) {
        final ParserErrorHandler errorHandler = ParserErrorHandler.getParseErrorHandler();
        final Scanner scanner = new Scanner(resource);
        final List<Token<?>> tokens = scanner.scanTokens();
        System.out.println("got through scanner");
        //tokens.forEach(System.out::println);
        final Parser parser = new Parser(tokens);
        final Expr expr = parser.parse();
        if(errorHandler.isHadError()) {
            return;
        }
        System.out.println(new AstPrinter().print(expr));
    }


}
