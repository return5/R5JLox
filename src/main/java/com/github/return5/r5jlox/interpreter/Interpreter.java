package main.java.com.github.return5.r5jlox.interpreter;

import main.java.com.github.return5.r5jlox.callable.FFEnum;
import main.java.com.github.return5.r5jlox.callable.R5JLoxCallable;
import main.java.com.github.return5.r5jlox.callable.R5JLoxFunction;
import main.java.com.github.return5.r5jlox.errorhandler.ErrorHandler;
import main.java.com.github.return5.r5jlox.errors.R5JloxRuntimeError;
import main.java.com.github.return5.r5jlox.errors.Return;
import main.java.com.github.return5.r5jlox.interpreter.classes.R5JLoxClass;
import main.java.com.github.return5.r5jlox.interpreter.classes.R5JLoxInstance;
import main.java.com.github.return5.r5jlox.tree.Stmt;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.token.TokenType;
import main.java.com.github.return5.r5jlox.tree.Expr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;


public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private static final Interpreter interpreter = new Interpreter();
    private final Environment global = new Environment(FFEnum.values());
    private Environment environment = global;
    private final Map<Expr,Integer> locals = new HashMap<>();


    private Interpreter() {
        super();
    }

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public Environment getGlobal() {
        return global;
    }

    @Override
    public Void visitWhileStmt(final Stmt.While stmt) {
        while(isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getBody());
        }
        return null;
    }

    public Void visitFunctionStmt(final Stmt.Function<?> stmt) {
        final String fnName = stmt.getName().getLexeme();
        final R5JLoxFunction func = new R5JLoxFunction(fnName,stmt.getFunction(),environment);
        environment.define(fnName,func);
        return null;
    }

    @Override
    public Void visitReturnStmt(final Stmt.Return<?> stmt) {
        final Object value = (stmt.getValue() != null) ? evaluate(stmt.getValue()) : null;
        throw new Return(value);
    }

    @Override
    public Void visitDesignationStmt(final Stmt.Designation<?> stmt) {
        environment.define(stmt.getName().getLexeme(),null);
        environment.assign(stmt.getName(),new R5JLoxClass(stmt.getName().getLexeme()));
        final Map<String,R5JLoxFunction> methods = new HashMap<>();
        stmt.getMethods().forEach(e -> methods.put(e.getName().getLexeme(),new R5JLoxFunction(e,environment)));
        final R5JLoxClass clazz = new R5JLoxClass(stmt.getName().getLexeme(),methods);
        environment.assign(stmt.getName(),clazz);
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
    public Object visitCallExpr(final Expr.Call<?> expr) {
        final Object callee = evaluate(expr.getCallee());
        final List<Object> arguments = expr.getArguments().stream()
                .map(this::evaluate)
                .toList();
        if(callee instanceof final R5JLoxCallable func) {
            if(expr.getArguments().size() != func.arity()) {
                throw new R5JloxRuntimeError(expr.getParen(),"Expected " + func.arity() + "arguments, but got " + arguments.size() +".");
            }
            return func.call(interpreter,arguments);

        }
        throw new R5JloxRuntimeError(expr.getParen(),"can only call classes and functions.");
    }

    @Override
    public Object visitFunctionExpr(final Expr.Function expr) {
        return new R5JLoxFunction(null,expr,environment);
    }

    @Override
    public Object visitGetExpr(final Expr.Get<?> expr) {
        final Object object = evaluate(expr.getObject());
        if(object instanceof final R5JLoxInstance instance) {
            return instance.get(expr.getName());
        }
        throw new R5JloxRuntimeError(expr.getName(),"Only instances have properties.");
    }

    @Override
    public Object visitSetExpr(final Expr.Set<?> expr) {
        final Object object = evaluate(expr.getObject());
        if(object instanceof final R5JLoxInstance instance) {
            final Object value = evaluate(expr.getValue());
            instance.setField(expr.getName(),value);
            return value;
        }
        else {
            throw new R5JloxRuntimeError(expr.getName(),"Only instances have fields.");
        }
    }

    @Override
    public Object visitSelfExpr(final Expr.Self<?> expr) {
        return lookUpVariable(expr.getKeyword(),expr);
    }

    @Override
    public Void visitStashStmt(final Stmt.Stash<?> stmt) {
        final Object value = (stmt.getInitializer() != null)? evaluate(stmt.getInitializer()) : null;
        environment.define(stmt.getName().getLexeme(),value);
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
        executeBlock(stmt.getStatements(),new Environment(environment));
        return null;
    }
    @Override
    public Object visitVariableExpr(final Expr.Variable<?> expr) {
        return lookUpVariable(expr.getName(),expr);
    }

    @Override
    public Object visitAssignExpr(final Expr.Assign<?> expr) {
        final Object value = evaluate(expr.getValue());
        final Integer dist = locals.get(expr);
        if(dist != null) {
            environment.assignAt(dist,expr.getName(),value);
        }
        else {
            global.assign(expr.getName(),value);
        }
        return value;
    }

    @Override
    public Void visitExpressionStmt(final Stmt.Expression stmt) {
        evaluate(stmt.getExpr());
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
            case CONCAT -> binaryExprStringOperation(left, right, (l, r) -> stringify(l) + stringify(r));
            default -> null;
        };
    }

    private <T> Object mathSwitch(final Token<T> expr, final int left, final int right) {
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

    private <T> Object mathSwitch(final Token<T> expr, final double left, final double right) {
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

    private Object binaryExprStringOperation(final Object left, final Object right, final BinaryOperator<Object> func) {
        //checkStringOperands(token,left,right);
        return func.apply(left, right);
    }

    private <T> Object binaryExprNumberOperation(final Token<T> token, final Object left, final Object right) {

        if(left instanceof final Integer l && right instanceof final Integer r) {
            return mathSwitch(token,l,r);
        }
        if(left instanceof final Integer l && right instanceof final Double r) {
            return mathSwitch(token,l.doubleValue(),r);
        }
        if(left instanceof final Double l && right instanceof final Integer r) {
            return mathSwitch(token,l,r.doubleValue());
        }
        if(left instanceof final Double l && right instanceof final Double r) {
            return mathSwitch(token,l,r);
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

    public void executeBlock(final List<Stmt> statements, final Environment environment) {
        final Environment previous = this.environment;
        try {
            this.environment = environment;
            statements.forEach(this::execute);
        }finally {
            this.environment = previous;
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
        return expr.accept(interpreter);
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
        stmt.accept(interpreter);
    }

    private String stringify(final Object obj) {
        if (obj == null) {
            return "nil";
        }
        return obj.toString();
    }

    public void resolve(final Expr expr,final int depth) {
        locals.put(expr,depth);
    }

    private <T> Object lookUpVariable(final Token<T> name,final Expr expr) {
        final Integer dist = locals.get(expr);
        if(dist != null) {
            return environment.getAt(dist,name.getLexeme());
        }
        else {
            return global.get(name);
        }
    }


}

