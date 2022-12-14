package main.java.com.github.return5.r5jlox.interpreter;

import main.java.com.github.return5.r5jlox.errors.R5JloxRuntimeError;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.tree.Expr;

import java.util.function.BinaryOperator;


public class Interpreter implements Expr.Visitor<Object>{

    @Override
    public Object visitBinaryExpr(final Expr.Binary<?> expr) {
        final Object left = evaluate(expr.getLeft());
        final Object right = evaluate(expr.getRight());
        return switch(expr.getOperator().getType()) {
            case MINUS -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l - (double)r);
            case SLASH -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l / (double)r);
            case STAR -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l * (double)r);
            case PLUS -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l + (double)r);
            case GREATER -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l > (double)r);
            case GREATER_EQUAL -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l >= (double)r);
            case LESS -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l < (double)r);
            case LESS_EQUAL -> binaryExprNumberOperation(expr.getOperator(),left,right,(l, r) -> (double)l <= (double)r);
            case BANG_EQUAL -> !isEqual(left,right);
            case BANG -> isEqual(left,right);
            case CONCAT -> binaryExprStringOperation(expr.getOperator(),left,right,(l,r)-> l + (String)r);
            default -> null;
        };
    }

    private <T> Object binaryExprStringOperation(final Token<T> token, final Object left, final Object right, final BinaryOperator<Object> func) {
        checkStringOperands(token,left,right);
        return func.apply(left,right);
    }

    private <T> Object binaryExprNumberOperation(final Token<T> token, final Object left, final Object right, final BinaryOperator<Object> func) {
       checkNumberOperands(token,left,right);
       return func.apply(left,right);
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
        switch(expr.getOperator().getType()) {
            case MINUS:
                checkNumberOperand(expr.getOperator(),right);
                return -(double) right;
            case BANG: return !isTruthy(right);
            default: return null;
        }
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

    private <T> void checkNumberOperand(final Token<T> operator, final Object operand) {
        if(operand instanceof Double) return;
        throw new R5JloxRuntimeError(operator,"Operand must be a number.");
    }

    private <T> void checkNumberOperands(final Token<T> operator, final Object left, final Object right) {
        if(left instanceof Double && right instanceof Double) {
            return;
        }
        throw new R5JloxRuntimeError(operator,"Operands must be numbers.");
    }

    private <T> void checkStringOperands(final Token<T> operator, final Object left, final Object right) {
        if(left instanceof String || right instanceof String) {
            return;
        }
        throw new R5JloxRuntimeError(operator,"Operands must be Strings.");
    }


    private Object evaluate(final Expr expr) {
        return expr.accept(this);
    }
}
