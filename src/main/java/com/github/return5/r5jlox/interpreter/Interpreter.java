package main.java.com.github.return5.r5jlox.interpreter;

import main.java.com.github.return5.r5jlox.errorhandler.ErrorHandler;
import main.java.com.github.return5.r5jlox.errors.R5JloxRuntimeError;
import main.java.com.github.return5.r5jlox.stmt.Stmt;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.token.TokenType;
import main.java.com.github.return5.r5jlox.tree.Expr;

import java.util.List;
import java.util.function.BinaryOperator;


public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private static final Interpreter interpreter = new Interpreter();
    private Enviroment enviroment = new Enviroment();

    private Interpreter() {
        super();
    }

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    @Override
    public Void visitWhileStmt(final Stmt.While stmt) {
        while(isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getBody());
        }
        return null;
    }

    @Override
    public Object visitLogicalExpr(final Expr.Logical<?> expr) {
        final Object left = evaluate(expr.getLeft());
        if((expr.getOperator().getType() == TokenType.OR && isTruthy(left)) ||
                (expr.getOperator().getType() == TokenType.AND && !isTruthy(left))) {
            return left;
        }
        return evaluate(expr.getRight());
    }

    @Override
    public Void visitStashStmt(final Stmt.Stash<?> stmt) {
        final Object value = (stmt.getInitializer() != null)? evaluate(stmt.getInitializer()) : null;
        enviroment.define(stmt.getName().getLexeme(),value);
        return null;
    }

    @Override
    public Void visitIfStmt(final Stmt.If stmt) {
        if(isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getThenBranch());
        }
        else if(stmt.getElseBranch() != null) {
            execute(stmt.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(final Stmt.Block stmt) {
        executeBlock(stmt.getStatements(),new Enviroment(enviroment));
        return null;
    }
    @Override
    public Object visitVariableExpr(final Expr.Variable<?> expr) {
        return enviroment.get(expr.getName());
    }

    @Override
    public Object visitAssignExpr(final Expr.Assign<?> expr) {
        final Object value = evaluate(expr.getValue());
        enviroment.assign(expr.getName(),value);
        return value;
    }

    @Override
    public Void visitExpressionStmt(final Stmt.Expression stmt) {
        evaluate(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitSayStmt(final Stmt.Say stmt) {
        final Object value = evaluate(stmt.getExpression());
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Object visitBinaryExpr(final Expr.Binary<?> expr) {
        final Object left = evaluate(expr.getLeft());
        final Object right = evaluate(expr.getRight());
        return switch (expr.getOperator().getType()) {
            case MINUS, STAR, SLASH, PLUS, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL -> binaryExprNumberOperation(expr.getOperator(), left, right);
            case BANG_EQUAL -> !isEqual(left, right);
            case BANG -> isEqual(left, right);
            case CONCAT -> binaryExprStringOperation(expr.getOperator(), left, right, (l, r) -> stringify(l) + stringify(r));
            default -> null;
        };
    }

    private <T> Object integerMathSwitch(final Token<T> expr, final int left, final int right) {
        return switch (expr.getType()) {
            case MINUS -> left - right;
            case SLASH -> left / right;
            case STAR -> left * right;
            case PLUS -> left + right;
            case GREATER -> left > right;
            case GREATER_EQUAL -> left >= right;
            case LESS -> left < right;
            case LESS_EQUAL -> left <= right;
            default -> null;
        };
    }

    private <T> Object doubleMathSwitch(final Token<T> expr, final double left, final double right) {
        return switch (expr.getType()) {
            case MINUS -> left - right;
            case SLASH -> left / right;
            case STAR -> left * right;
            case PLUS -> left + right;
            case GREATER -> left > right;
            case GREATER_EQUAL -> left >= right;
            case LESS -> left < right;
            case LESS_EQUAL -> left <= right;
            default -> null;
        };
    }

    private <T> Object binaryExprStringOperation(final Token<T> token, final Object left, final Object right, final BinaryOperator<Object> func) {
        //checkStringOperands(token,left,right);
        return func.apply(left, right);
    }


    private <T> Object binaryExprNumberOperation(final Token<T> token, final Object left, final Object right) {
        if(left instanceof final Integer l && right instanceof final Integer r) {
            return integerMathSwitch(token,l,r);
        }
        if(left instanceof final Integer l && right instanceof final Double r) {
            return doubleMathSwitch(token,l.doubleValue(),r);
        }
        if(left instanceof final Double l && right instanceof final Integer r) {
            return doubleMathSwitch(token,l,r.doubleValue());
        }
        if(left instanceof final Double l && right instanceof final Double r) {
            return doubleMathSwitch(token,l,r);
        }
        throw new R5JloxRuntimeError(token, "Operands must be numbers.");
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
       return switch (expr.getOperator().getType()) {
            case MINUS -> negativeNumber(expr.getOperator(),right);
            case BANG -> !isTruthy(right);
            default -> null;
        };
    }

    private Object negativeNumber(final Token<?> operator,final Object right) {
        if(right instanceof final Double d) {
            return -d;
        }
        else if(right instanceof final Integer i){
            return -i;
        }
        else {
            throw new R5JloxRuntimeError(operator, "Operand must be a number.");
        }
    }

    private void executeBlock(final List<Stmt> statements,final Enviroment enviroment) {
        final Enviroment previous = this.enviroment;
        try {
            this.enviroment = enviroment;
            statements.forEach(this::execute);
        }finally {
            this.enviroment = previous;
        }
    }

    private boolean isEqual(final Object left, final Object right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null) {
            return false;
        }
        return left.equals(right);
    }

    private boolean isTruthy(final Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof final Boolean bool) {
            return bool;
        }
        return true;
    }

    private <T> void checkNumberOperand(final Token<T> operator, final Object operand) {
        if (operand instanceof Double) return;
        throw new R5JloxRuntimeError(operator, "Operand must be a number.");
    }

//    private <T> void checkStringOperands(final Token<T> operator, final Object left, final Object right) {
//        if(left instanceof String || right instanceof String) {
//            return;
//        }
//        throw new R5JloxRuntimeError(operator,"Operands must be Strings.");
//    }

    private Object evaluate(final Expr expr) {
        return expr.accept(this);
    }

    public void interpret(final List<Stmt> statements) {
        final ErrorHandler errorHandler = ErrorHandler.getParseErrorHandler();
        try {
            statements.forEach(this::execute);
        } catch (final R5JloxRuntimeError e) {
            errorHandler.runtimeError(e);
        }
    }

    private void execute(final Stmt stmt) {
        stmt.accept(this);
    }

    private String stringify(final Object obj) {
        if (obj == null) {
            return "nil";
        }
        return obj.toString();
    }
}

