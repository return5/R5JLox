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
                "Grouping : final Expr expression",
                "Literal<T> : final T value",
                "Unary<T> : final Token<T> operator, final Expr right",
                "Set<T> : final Expr object, final Token<T> name, final Expr value",
                "Super<T,U> : final Token<T> keyword, final Token<U> method",
                "Self<T> : final Token<T> keyowrd",
                "Variable<T> : final Token<T> name",
                "Assign<T> : final Token<T> name, final Expr value",
                "Logical<T> : final Expr left, final Token<T> operator, final Expr right",
                "Call<T> : final Expr callee, final Token<T> paren, final List<Expr> arguments",
                "Get<T> : final Expr object, final Token<T> name",
                "Function : final List<Token<?>> parameters, final List<Stmt> body"));

        defineAst(outputDirectory,"Stmt",List.of("Expression : final Expr expression",
                "Function<T> : final Token<T> name, final Expr.Function function",
                "Return<T> : final Token<T> keyword, final Expr value",
                "If : final Expr condition, final Stmt thenBranch, final Stmt elseBranch",
                "Say : final Expr expression",
                "Stash<T> : final Token<T> name, final Expr initializer",
                "Designation<T,U> : final Token<T> name, final Expr.Variable<U> superClass, final List<Stmt.Function<?>> methods",
                "Block :final List<Stmt> statements",
                "While : final Expr condition, final Stmt body"));
    }


    private static void writeGetters(final PrintWriter writer,final String[] fields, final String fieldOffset,final String methodOffset) {
        writer.println();
        //write getters.
        for(final String field : fields) {
            final String[] splitFields = field.split(" ");
            final String type = splitFields[1];
            final String name = splitFields[2];
            writer.println(methodOffset + "public " + type + " get" + Character.toUpperCase(name.charAt(0)) + name.substring(1) + "() {");
            writer.println(fieldOffset + "return " + name + ";");
            writer.println("\t\t}");
            writer.println();
        }
    }

    private static void defineType(final PrintWriter writer,final String baseName, final String className, final String fieldList) {
        final String methodOffset = "\t\t";
        final String fieldOffset = "\t\t\t";
        writer.println("\tpublic static class " + className + " extends " + baseName + " {");
        final String stripClassName = className.split("<")[0].trim();
        writer.println();
        final String[] fields = fieldList.split(", ");
        Arrays.stream(fields).forEach(f -> writer.println(methodOffset + f + ";"));
        writer.println();
        //generate Constructor
        writer.println("\t\tpublic " + stripClassName + "(" + fieldList + ") {" );
        //inside of constructor, initialize parameters.
        Arrays.stream(fields).map(s -> s.split("final [\\w<>?.]+ ")[1]).forEach(f -> writer.println(fieldOffset + "this." + f + " = " + f + ";"));
        writer.println(methodOffset + "}");
        writeGetters(writer,fields,fieldOffset,methodOffset);
        writer.println(methodOffset + "public <R> R accept(final Visitor<R> visitor) {");
        writer.println(fieldOffset + "return visitor.visit" + stripClassName + baseName + "(this);");
        writer.println(methodOffset + "}");
        writer.println("\t}");
        writer.println();
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
