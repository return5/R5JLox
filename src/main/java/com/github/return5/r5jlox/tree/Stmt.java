package main.java.com.github.return5.r5jlox.tree;

import main.java.com.github.return5.r5jlox.token.Token;

import java.util.List;
import java.util.StringJoiner;

public abstract class Stmt{

	public interface Visitor<R> {
		R visitExpressionStmt(Expression stmt);
		R visitSayStmt(Say stmt);
		R visitStashStmt(Stash<?> stmt);
		R visitIfStmt(If stmt);
		R visitBlockStmt(Block stmt);
		R visitWhileStmt(While stmt);
		R visitFunctionStmt(Stmt.Function<?> stmt);
		R visitReturnStmt(Return<?> stmt);
		R visitDesignationStmt(Designation<?> stmt);
	}

	public abstract<R> R accept(final Visitor<R> visitor);

	public static class Expression extends Stmt {

		final Expr expr;

		public Expression(final Expr expr) {
			this.expr = expr;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Expression.class.getSimpleName() + "[", "]")
					.add("expr=" + expr)
					.toString();
		}

		public Expr getExpr() {
			return expr;
		}
	}

	public static class If extends Stmt {

		final Expr condition;
		final Stmt thenBranch;
		final Stmt elseBranch;

		public If(final Expr condition, final Stmt thenBranch, final Stmt elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", If.class.getSimpleName() + "[", "]")
					.add("condition=" + condition)
					.add("thenBranch=" + thenBranch)
					.add("elseBranch=" + elseBranch)
					.toString();
		}

		public Expr getCondition() {
			return condition;
		}

		public Stmt getThenBranch() {
			return thenBranch;
		}

		public Stmt getElseBranch() {
			return elseBranch;
		}
	}

	public static class Say extends Stmt {

		final Expr expression;

		public Say(final Expr expression) {
			this.expression = expression;
		}

		public Expr getExpression() {
			return expression;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Say.class.getSimpleName() + "[", "]")
					.add("expression=" + expression)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitSayStmt(this);
		}
	}
	public static class Stash<T> extends Stmt {

		final Token<T> name;
		final Expr initializer;

		public Token<T> getName() {
			return name;
		}

		public Expr getInitializer() {
			return initializer;
		}

		public Stash(final Token<T> name, final Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Stash.class.getSimpleName() + "[", "]")
					.add("name=" + name)
					.add("initializer=" + initializer)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitStashStmt(this);
		}
	}
	public static class Block extends Stmt {
		final List<Stmt> statements;

		public Block(final List<Stmt> statements) {
			this.statements = statements;
		}

		public List<Stmt> getStatements() {
			return this.statements;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Block.class.getSimpleName() + "[", "]")
					.add("statements=" + statements)
					.toString();
		}

		@Override
		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}
	}

	public static class While extends Stmt {

		final Expr condition;
		final Stmt body;

		public While(final Expr condition, final Stmt body) {
			this.condition = condition;
			this.body = body;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}

		public Expr getCondition() {
			return condition;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", While.class.getSimpleName() + "[", "]")
					.add("condition=" + condition)
					.add("body=" + body)
					.toString();
		}

		public Stmt getBody() {
			return body;
		}
	}

	public static class Function<T> extends Stmt {

		final Token<T> name;
		final Expr.Function function;

		public Function(final Token<T> name, final Expr.Function function) {
			this.name = name;
			this.function = function;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitFunctionStmt(this);
		}

		public Token<T> getName() {
			return name;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Function.class.getSimpleName() + "[", "]")
					.add("name=" + name)
					.add("function=" + function)
					.toString();
		}

		public Expr.Function getFunction() {
			return function;
		}
	}

	public static class Return<T> extends Stmt {

		final Token<T> keyword;
		final Expr value;

		public Return(final Token<T> keyword, final Expr value) {
			this.keyword = keyword;
			this.value = value;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitReturnStmt(this);
		}

		public Token<T> getKeyword() {
			return keyword;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Return.class.getSimpleName() + "[", "]")
					.add("keyword=" + keyword)
					.add("value=" + value)
					.toString();
		}

		public Expr getValue() {
			return value;
		}
	}

	public static class Designation<T> extends Stmt {

		final Token<T> name;
		final List<Stmt.Function<?>> methods;

		public Designation(final Token<T> name, final List<Stmt.Function<?>> methods) {
			this.name = name;
			this.methods = methods;
		}

		public List<Function<?>> getMethods() {
			return methods;
		}

		public Token<T> getName() {
			return name;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Designation.class.getSimpleName() + "[", "]")
					.add("name=" + name)
					.add("methods=" + methods)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitDesignationStmt(this);
		}
	}

}
