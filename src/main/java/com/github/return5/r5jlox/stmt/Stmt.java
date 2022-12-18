package main.java.com.github.return5.r5jlox.stmt;

import main.java.com.github.return5.r5jlox.token.Token;
import main.java.com.github.return5.r5jlox.tree.Expr;

import java.util.List;

public abstract class Stmt{

	public interface Visitor<R> {
		R visitExpressionStmt(Expression stmt);
		R visitSayStmt(Say stmt);
		R visitStashStmt(Stash<?> stmt);
		R visitIfStmt(If stmt);
		R visitBlockStmt(Block stmt);
		R visitWhileStmt(While stmt);

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

		public Expr getExpression() {
			return expression;
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

		public Stmt getBody() {
			return body;
		}
	}


}
