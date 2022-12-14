package main.java.com.github.return5.r5jlox.interpreter;

import main.java.com.github.return5.r5jlox.tree.Expr;


public class Interpreter implements Expr.Visitor<Object>{

    @Override
    public Object visitBinaryExpr(final Expr.Binary<?> expr) {
        final Object left = evaluate(expr.getLeft());
        final Object right = evaluate(expr.getRight());
        return switch(expr.getOperator().getType()) {
            case MINUS -> (double)left - (double)right;
            case SLASH -> (double)left / (double)right;
            case STAR -> (double)left * (double)right;
            case PLUS -> (double)left + (double)right;
            case CONCAT -> left + (String)right;
            case GREATER -> (double)left > (double)right;
            case GREATER_EQUAL -> (double)left >= (double)right;
            case LESS -> (double)left < (double)right;
            case LESS_EQUAL -> (double)left <= (double)right;
            case BANG_EQUAL -> !isEqual(left,right);
            case BANG -> isEqual(left,right);
            default -> null;
        };
    }

    @Override
    public Object visitGroupingExpr(final Expr.Grouping expr) {
        return evaluate(expr.getExpression());
    }

    @Override
    public Object visitLiteralExpr(final Expr.Literal<?> expr) {
        return expr.getValue();
    }

    @Override
    public Object visitUnaryExpr(final Expr.Unary<?> expr) {
        final Object right = evaluate(expr.getRight());
        return switch(expr.getOperator().getType()) {
            case MINUS -> -(double) right;
            case BANG -> !isTruthy(right);
            default -> null;
        };

    }

    private boolean isEqual(final Object left, final Object right) {
        if(left == null && right == null) {
            return true;
        }
        if(left == null) {
            return false;
        }
        return left.equals(right);
    }

    private boolean isTruthy(final Object object) {
        if(object == null) {
            return false;
        }
        if(object instanceof final Boolean bool) {
            return bool;
        }
        return true;
    }

    private Object evaluate(final Expr expr) {
        return expr.accept(this);
    }
}
