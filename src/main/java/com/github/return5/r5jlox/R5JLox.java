package main.java.com.github.return5.r5jlox;

import main.java.com.github.return5.r5jlox.errorhandler.ErrorHandler;
import main.java.com.github.return5.r5jlox.interpreter.Interpreter;
import main.java.com.github.return5.r5jlox.resolver.Resolver;
import main.java.com.github.return5.r5jlox.parser.Parser;
import main.java.com.github.return5.r5jlox.scanner.Scanner;
import main.java.com.github.return5.r5jlox.tree.Stmt;
import main.java.com.github.return5.r5jlox.token.Token;

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
        final ErrorHandler errorHandler = ErrorHandler.getParseErrorHandler();
        run(new String(bytes, Charset.defaultCharset()));
        if (errorHandler.isHadError()) {
            System.exit(65);
        }
        if(errorHandler.isHadRuntimeError()) {
            System.exit(70);
        }
    }

    private static void repl() throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(input);
        final ErrorHandler errorHandler = ErrorHandler.getParseErrorHandler();
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
        final ErrorHandler errorHandler = ErrorHandler.getParseErrorHandler();
        final Interpreter interpreter = Interpreter.getInterpreter();
        final Scanner scanner = new Scanner(resource);
        final List<Token<?>> tokens = scanner.scanTokens();
        //tokens.forEach(System.out::println);
        final Parser parser = new Parser(tokens);
        final List<Stmt> statements = parser.parse();
        if(errorHandler.isHadError()) {
            return;
        }
        Resolver.getResolver().resolve(statements);
        //System.out.println(new AstPrinter().print(expr));
        if(errorHandler.isHadError()) {
            return;
        }
        interpreter.interpret(statements);
    }


}
