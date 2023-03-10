package main.java.com.github.return5.r5jlox.printer;

import main.java.com.github.return5.r5jlox.tree.Expr;

import java.util.Arrays;

// incomplete. //
public class AstPrinter implements Expr.Visitor<String>{

    @Override
    public String visitBinaryExpr(Expr.Binary<?> expr) {
        return parenthesize(expr.getOperator().getLexeme(),expr.getLeft(),expr.getRight());
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group",expr.getExpression());
    }

    @Override
    public String visitLiteralExpr(Expr.Literal<?> expr) {
        if(expr.getValue() == null) {
            return "nil";
        }
        return expr.getValue().toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary<?> expr) {
        return parenthesize(expr.getOperator().getLexeme(),expr.getRight());
    }

    @Override
    public String visitVariableExpr(final Expr.Variable<?> expr) {
        //TODO fill this in.
        return null;
    }

    @Override
    public String visitAssignExpr(Expr.Assign<?> expr) {
        //TODO fill this in.
        return null;
    }

    @Override
    public String visitLogicalExpr(Expr.Logical<?> expr) {
        //TODO fill this in.
        return null;
    }

    @Override
    public String visitCallExpr(Expr.Call<?> expr) {
        //TODO fill this in.
        return null;
    }

    @Override
    public String visitFunctionExpr(Expr.Function expr) {
        //TODO fill this in.
        return null;
    }

    @Override
    public String visitGetExpr(Expr.Get<?> expr) {
        //TODO fill this in.
        return null;
    }

    @Override
    public String visitSetExpr(Expr.Set<?> expr) {
        return null;
    }

    @Override
    public String visitSelfExpr(Expr.Self<?> expr) {
        return null;
    }

    public String parenthesize(final String name,final Expr...exprs) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        Arrays.stream(exprs)
                .forEach(s -> builder.append(" ").append(s.accept(this)));
        builder.append(")");
        return builder.toString();
    }

    public String print(final Expr expr) {
        return expr.accept(this);
    }
    //TODO fill this in.

//    public static void main(final String[] args) {
//        final Expr expression = new Expr.Binary<>(
//                new Expr.Unary<>(
//                        new Token<>(TokenType.MINUS,"-",null,1),
//                        new Expr.Literal<>(123)),
//                new Token<>(TokenType.STAR,"*",null,1),
//                new Expr.Grouping(new Expr.Literal<>(45.67)));
//        System.out.println(new AstPrinter().print(expression));
//    }
//
}
