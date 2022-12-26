package main.java.com.github.return5.r5jlox.resolver;
import main.java.com.github.return5.r5jlox.errorhandler.ErrorHandler;
import main.java.com.github.return5.r5jlox.interpreter.Interpreter;
import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.tree.Expr;
import main.java.com.github.return5.r5jlox.tree.Stmt;

import java.util.*;
import java.util.function.Supplier;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>{

    private static final Resolver resolver = new Resolver();

    private final Interpreter interpreter = Interpreter.getInterpreter();

    private final ErrorHandler errorHandler = ErrorHandler.getParseErrorHandler();

    private FunctionType currentFunction = FunctionType.NONE;

    //linkedList will be used as a stack.
    private final Deque<Map<String,Boolean>> scopes = new LinkedList<>();


    private Resolver() {
        super();
    }


    public static Resolver getResolver() {
        return resolver;
    }

    private void beginScope() {
        scopes.addFirst(new HashMap<>());
    }

    private void endScope() {
        scopes.removeFirst();
    }

    private void resolve(final Expr expr) {
        expr.accept(this);
    }

    private void resolve(final Stmt stmt) {
        stmt.accept(this);
    }

    public void resolve(final List<Stmt> statements) {
        statements.forEach(this::resolve);
    }

    private <T> void declare(final Token<T> name) {
        if(scopes.isEmpty()) {
            return;
        }
        final Map<String,Boolean> scope = scopes.peek();
        if(scope.containsKey(name.getLexeme())) {
            errorHandler.reportError(name,"Already declared a variable with that name in this scope.");
        }
        scope.put(name.getLexeme(),false);

    }

    private <T> void define(final Token<T> name) {
        if(scopes.isEmpty()) {
            return;
        }
        scopes.peek().put(name.getLexeme(),true);
    }

    private void resolveLocal(final Expr expr,final Token<?> name) {
        int i = scopes.size() - 1;
        final Iterator<Map<String,Boolean>> iter = scopes.descendingIterator();
        while(iter.hasNext()) {
            if(iter.next().containsKey(name.getLexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
            i--;
        }
    }

    private void resolveFunction(Supplier<List<Token<?>>> paramSup,Supplier<List<Stmt>> stmtSup,final FunctionType functionType)  {
        final FunctionType enclosingFunction = currentFunction;
        currentFunction = functionType;
        beginScope();
        paramSup.get().forEach(p -> {declare(p);define(p);});
        resolve(stmtSup.get());
        endScope();
        currentFunction = enclosingFunction;
    }

    private <T> void resolveFunction(final Stmt.Function<T> stmt,final FunctionType funcType){
        resolveFunction(() ->stmt.getFunction().getParameters(),()->stmt.getFunction().getBody(),funcType);
    }


    @Override
    public Void visitBinaryExpr(final Expr.Binary<?> expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }


    @Override
    public Void visitGroupingExpr(final Expr.Grouping expr) {
        resolve(expr.getExpression());
        return null;
    }

    @Override
    public Void visitLiteralExpr(final Expr.Literal<?> expr) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(final Expr.Unary<?> expr) {
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitVariableExpr(final Expr.Variable<?> expr) {
        if(!scopes.isEmpty() && Boolean.FALSE.equals(scopes.peek().get(expr.getName().getLexeme()))) {
            errorHandler.reportError(expr.getName(),"Can't read local variable in its own initializer.");
        }
        resolveLocal(expr,expr.getName());
        return null;
    }
    @Override
    public Void visitAssignExpr(final Expr.Assign<?> expr) {
        resolve(expr.getValue());
        resolveLocal(expr,expr.getName());
        return null;
    }

    @Override
    public Void visitLogicalExpr(final Expr.Logical<?> expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitCallExpr(final Expr.Call<?> expr) {
        resolve(expr.getCallee());
        expr.getArguments().forEach(this::resolve);
        return null;
    }

    @Override
    public Void visitFunctionExpr(final Expr.Function expr) {
        resolveFunction(expr::getParameters,expr::getBody,FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get<?> expr) {
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set<?> expr) {
        resolve(expr.getValue());
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitExpressionStmt(final Stmt.Expression stmt) {
        resolve(stmt.getExpr());
        return null;
    }

    @Override
    public Void visitSayStmt(final Stmt.Say stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitStashStmt(final Stmt.Stash<?> stmt) {
        declare(stmt.getName());
        if(stmt.getInitializer() != null) {
            resolve(stmt.getInitializer());
        }
        define(stmt.getName());
        return null;
    }

    @Override
    public Void visitIfStmt(final Stmt.If stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getThenBranch());
        if(stmt.getElseBranch() != null) {
            resolve(stmt.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(final Stmt.Block stmt) {
        beginScope();
        resolve(stmt.getStatements());
        endScope();
        return null;
    }

    @Override
    public Void visitWhileStmt(final Stmt.While stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getBody());
        return null;
    }

    @Override
    public Void visitFunctionStmt(final Stmt.Function<?> stmt) {
        declare(stmt.getName());
        define(stmt.getName());
        resolveFunction(stmt,FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitReturnStmt(final Stmt.Return<?> stmt) {
        if(currentFunction == FunctionType.NONE) {
            errorHandler.reportError(stmt.getKeyword(), "Can't return from outside a function.");
        }
        if(stmt.getValue() != null) {
            resolve(stmt.getValue());
        }
        return null;
    }

    @Override
    public Void visitDesignationStmt(Stmt.Designation<?> stmt) {
        declare(stmt.getName());
        define(stmt.getName());
        for(final Stmt.Function<?> method : stmt.getMethods()) {
            final FunctionType declaration = FunctionType.METHOD;
            resolveFunction(method,declaration);
        }
        return null;
    }
}
