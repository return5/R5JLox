package main.java.com.github.return5.jlox;

import main.java.com.github.return5.jlox.errorhandler.ParserErrorHandler;
import main.java.com.github.return5.jlox.scanner.Scanner;
import main.java.com.github.return5.jlox.token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JLox {

    public static void main(final String[] args) throws IOException {
        final ParserErrorHandler errorHandler = ParserErrorHandler.getParseErrorHandler();
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        else if (args.length == 1){
            runFile(args[0],errorHandler);
        }
        else {
            repl(errorHandler);
        }
    }

    private static void runFile(final String path,final ParserErrorHandler errorHandler) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()),errorHandler);
        if (errorHandler.isHadError()) {
            System.exit(65);
        }
    }

    private static void repl(final ParserErrorHandler errorHandler) throws IOException {
        final InputStreamReader input = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(input);
        while(true) {
            System.out.print("> ");
            final String line = reader.readLine();
            if(line == null) {
                break;
            }
            run(line,errorHandler);
            errorHandler.setHadError(false);
        }
    }

    private static void run(final String resource,final ParserErrorHandler errorHandler) {
        final Scanner scanner = new Scanner(resource,errorHandler);
        final List<Token<?>> tokens = scanner.scanTokens();
        tokens.forEach(System.out::println);
    }


}
