package main.java.com.github.return5.r5jlox.stmt;

import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.tree.Expr;

public abstract class Stmt{

	public interface Visitor<R> {
		R visitExpressionStmt(Expression stmt);
		R visitSayStmt(Say stmt);
		R visitstashStmt(stash<?> stmt);
	}

	public abstract<R> R accept(final Visitor<R> visitor);

	public static class Expression extends Stmt {

		final Expr expression;

		public Expression(final Expr expression) {
			this.expression = expression;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}
	public static class Say extends Stmt {

		final Expr expression;

		public Say(final Expr expression) {
			this.expression = expression;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitSayStmt(this);
		}
	}
	public static class stash<T> extends Stmt {

		final Token<T> name;
		final Expr initializer;

		public stash(final Token<T> name, final Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitstashStmt(this);
		}
	}

}
