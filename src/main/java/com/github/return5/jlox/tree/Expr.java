package main.java.com.github.return5.jlox.tree;

import main.java.com.github.return5.jlox.scanner.Token;

import java.util.List;

abstract class Expr{
	private Expr() {
		super();
	}

 static class Binary<T> extends Expr {

	final Expr left;
	final Token<T> operator;
	final Expr right;

	Binary(final Expr left, final Token<T> operator, final Expr right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
 }
 static class Grouping extends Expr {

	final Expr expression;

	Grouping(final Expr expression) {
		this.expression = expression;
	}
 }
 static class Literal <T> extends Expr {

	final T value;

	Literal (final T value) {
		this.value = value;
	}
 }
 static class Unary<T> extends Expr {

	final Token<T> operator;
	final Expr right;

	Unary(final Token<T> operator, final Expr right) {
		this.operator = operator;
		this.right = right;
	}
 }
}
