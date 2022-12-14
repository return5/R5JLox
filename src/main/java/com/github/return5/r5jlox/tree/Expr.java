package main.java.com.github.return5.r5jlox.tree;

import main.java.com.github.return5.r5jlox.token.Token;

public abstract class Expr{

	public interface Visitor<R> {
		R visitBinaryExpr(Binary<?> expr);
		R visitGroupingExpr(Grouping expr);
		R visitLiteralExpr(Literal<?> expr);
		R visitUnaryExpr(Unary<?> expr);
		R visitVariableExpr(Variable<?> expr);
	}

	public abstract<R> R accept(final Visitor<R> visitor);

	public static class Binary<T> extends Expr {

		final Expr left;
		final Token<T> operator;
		final Expr right;

		public Binary(final Expr left, final Token<T> operator, final Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		public Expr getLeft() {
			return left;
		}

		public Token<T> getOperator() {
			return operator;
		}

		public Expr getRight() {
			return right;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}
	}
	public static class Grouping extends Expr {

		final Expr expression;

		public Grouping(final Expr expression) {
			this.expression = expression;
		}

		public Expr getExpression() {
			return expression;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}
	}
	public static class Literal<T> extends Expr {

		final T value;

		public Literal(final T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}
	}
	public static class Unary<T> extends Expr {

		final Token<T> operator;
		final Expr right;

		public Unary(final Token<T> operator, final Expr right) {
			this.operator = operator;
			this.right = right;
		}

		public Token<T> getOperator() {
			return operator;
		}

		public Expr getRight() {
			return right;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}
	public static class Variable<T> extends Expr {

		final Token<T> name;

		public Token<T> getName() {
			return name;
		}

		public Variable(final Token<T> name) {
			this.name = name;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitVariableExpr(this);
		}
	}

}
