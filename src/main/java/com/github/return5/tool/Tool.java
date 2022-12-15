package main.java.com.github.return5.tool;

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
                "Grouping : final Expr expression","Literal<T> : final T value","Unary<T> : final Token<T> operator, final Expr right",
                "Variable<T> : final Token<T> name","Assign<T> : final Token<T> name, final Expr value"));

        defineAst(outputDirectory,"Stmt",List.of("Expression : final Expr expression",
                "Say : final Expr expression","Stash<T> : final Token<T> name, final Expr initializer", "Block :final List<Stmt> statements"));

    }

    private static void defineType(final PrintWriter writer,final String baseName, final String className, final String fieldList) {
        writer.println("\tpublic static class " + className + " extends " + baseName + " {");
        final String stripClassName = className.split("<")[0].trim();
        writer.println();
        final String[] fields = fieldList.split(", ");
        Arrays.stream(fields).forEach(f -> writer.println("\t\t" + f + ";"));
        writer.println();
        //generate Constructor
        writer.println("\t\tpublic " + stripClassName + "(" + fieldList + ") {" );
        //inside fo constructor, initialize parameters.
        Arrays.stream(fields).map(s -> s.split("final [\\w<>]+ ")[1]).forEach(f -> writer.println("\t\t\tthis." + f + " = " + f + ";"));
        writer.println("\t\t}");
        writer.println();
        writer.println("\t\tpublic <R> R accept(final Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" + stripClassName + baseName + "(this);");
        writer.println("\t\t}");
        writer.println("\t}");
    }

    private static void defineAst(final String outputDirectory,final String baseName,final List<String> types) throws IOException {
        final String path = outputDirectory + "/" + baseName + ".java";
        try (PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package main.java.com.github.return5.r5jlox.;  //TODO fill in the correct package here");
            writer.println();
            writer.println("public abstract class " + baseName + "{");
            writer.println();
            defineVisitor(writer,baseName,types);
            writer.println();
            writer.println("\tpublic abstract<R> R accept(final Visitor<R> visitor);");
            writer.println();
            types.forEach(e -> defineType(writer,baseName,e.split(":")[0].trim(),e.split(":")[1].trim()));
            writer.println();
            writer.println("}");
        }
    }

    private static void defineVisitor(final PrintWriter writer,final String baseName,final List<String> types) {
        writer.println("\tpublic interface Visitor<R> {");
        types.stream()
                .map(t -> t.split(":")[0].trim())
                .map(t -> t.replace("<T>","<?>"))
                .forEach(e -> writer.println("\t\tR visit" + e.split("<")[0].trim() + baseName + "(" + e + " " + baseName.toLowerCase() + ");"));
        writer.println("\t}");
    }
}
