package main.java.com.github.return5.jlox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Tool {

    public static void main(final String[] args) throws IOException {
        if(args.length != 1) {
            System.err.println("Usage: Tool <output directory>");
            System.exit(64);
        }
        final String outputDirectory = args[0];
        defineAst(outputDirectory, "Expr",List.of("Binary<T> : final Expr left, final Token<T> operator, final Expr right",
                "Grouping : final Expr expression","Literal <T> : final T value","Unary<T> : final Token<T> operator, final Expr right"));

    }

    private static void defineType(final PrintWriter writer,final String baseName, final String className, final String fieldList) {
        writer.println(" static class " + className + " extends " + baseName + " {");
        final String stripClassName = className.split("<")[0];
        writer.println();
        final String[] fields = fieldList.split(", ");
        Arrays.stream(fields).forEach(f -> writer.println("\t" + f + ";"));
        writer.println();
        writer.println("\t" + stripClassName + "(" + fieldList + ") {" );
        Arrays.stream(fields).map(s -> s.split("final [\\w<>]+ ")[1]).forEach(f -> writer.println("\t\tthis." + f + " = " + f + ";"));
        writer.println("\t}");
        writer.println(" }");
    }

    private static void defineAst(final String outputDirectory,final String baseName,final List<String> types) throws IOException {
        final String path = outputDirectory + "/" + baseName + ".java";
        try (PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package main.java.com.github.return5.jlox.parser;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.println("abstract class " + baseName + "{");
            types.forEach(e -> defineType(writer,baseName,e.split(":")[0].trim(),e.split(":")[1].trim()));
            writer.println("}");
        }

    }
}
