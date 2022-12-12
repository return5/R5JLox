package main.java.com.github.return5.jlox.tree;

import main.java.com.github.return5.jlox.scanner.Token;


abstract class Expr{

	interface Visitor<R> {
		R visitBinaryExpr(Binary<?> expr);
		R visitGroupingExpr(Grouping expr);
		R visitLiteralExpr(Literal<?> expr);
		R visitUnaryExpr(Unary<?> expr);
	}

	abstract<R> R accept(final Visitor<R> visitor);

	static class Binary<T> extends Expr {

		final Expr left;
		final Token<T> operator;
		final Expr right;

		Binary(final Expr left, final Token<T> operator, final Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		<R> R accept(final Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}
	}
	static class Grouping extends Expr {

		final Expr expression;

		Grouping(final Expr expression) {
			this.expression = expression;
		}

		<R> R accept(final Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}
	}
	static class Literal<T> extends Expr {

		final T value;

		Literal(final T value) {
			this.value = value;
		}

		<R> R accept(final Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}
	}
	static class Unary<T> extends Expr {

		final Token<T> operator;
		final Expr right;

		Unary(final Token<T> operator, final Expr right) {
			this.operator = operator;
			this.right = right;
		}

		<R> R accept(final Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}

}
