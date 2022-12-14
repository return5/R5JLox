package main.java.com.github.return5.r5jlox.stmt;

import main.java.com.github.return5.r5jlox.tree.Expr;

public abstract class Stmt{

	interface Visitor<R> {
		R visitExpressionStmt(Expression stmt);
		R visitSayStmt(Say stmt);
	}

	abstract<R> R accept(final Visitor<R> visitor);

	static class Expression extends Stmt {

		final Expr expression;

		Expression(final Expr expression) {
			this.expression = expression;
		}

		<R> R accept(final Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}
	static class Say extends Stmt {

		final Expr expression;

		Say(final Expr expression) {
			this.expression = expression;
		}

		<R> R accept(final Visitor<R> visitor) {
			return visitor.visitSayStmt(this);
		}
	}

}
