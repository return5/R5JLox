package main.java.com.github.return5.r5jlox.tree;

import main.java.com.github.return5.r5jlox.token.Token;

import java.util.List;
import java.util.StringJoiner;

public abstract class Expr{

	public interface Visitor<R> {
		R visitBinaryExpr(Binary<?> expr);
		R visitGroupingExpr(Grouping expr);
		R visitLiteralExpr(Literal<?> expr);
		R visitUnaryExpr(Unary<?> expr);
		R visitVariableExpr(Variable<?> expr);
		R visitAssignExpr(Assign<?> expr);
		R visitLogicalExpr(Logical<?> expr);
		R visitCallExpr(Call<?> expr);
		R visitFunctionExpr(Function expr);
		R visitGetExpr(Get<?> expr);
		R visitSetExpr(Set<?> expr);
		R visitSelfExpr(Self<?> expr);
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

		@Override
		public String toString() {
			return "Binary{" +
					"left=" + left +
					", operator=" + operator +
					", right=" + right +
					'}';
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

		@Override
		public String toString() {
			return "Grouping{" +
					"expression=" + expression +
					'}';
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

		@Override
		public String toString() {
			return "Literal{" +
					"value=" + value +
					'}';
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

		@Override
		public String toString() {
			return "Unary{" +
					"operator=" + operator +
					", right=" + right +
					'}';
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}

	public static class Set<T> extends Expr {

		final Expr object;
		final Token<T> name;
		final Expr value;

		public Set(final Expr object, final Token<T> name, final Expr value) {
			this.object = object;
			this.name = name;
			this.value = value;
		}

		public Expr getObject() {
			return object;
		}
		public Token<T> getName() {
			return name;
		}
		public Expr getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "Set{" +
					"object=" + object +
					", name=" + name +
					", value=" + value +
					'}';
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitSetExpr(this);
		}
	}

	public static class Logical<T> extends Expr {

		final Expr left;
		final Token<T> operator;
		final Expr right;

		public Logical(final Expr left, final Token<T> operator, final Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitLogicalExpr(this);
		}

		public Expr getLeft() {
			return left;
		}

		public Token<T> getOperator() {
			return operator;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Logical.class.getSimpleName() + "[", "]")
					.add("left=" + left)
					.add("operator=" + operator)
					.add("right=" + right)
					.toString();
		}

		public Expr getRight() {
			return right;
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

		@Override
		public String toString() {
			return new StringJoiner(", ", Variable.class.getSimpleName() + "[", "]")
					.add("name=" + name)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitVariableExpr(this);
		}
	}

	public static class Assign<T> extends Expr {

		final Token<T> name;
		final Expr value;

		public Assign(final Token<T> name, final Expr value) {
			this.name = name;
			this.value = value;
		}

		public Token<T> getName() {
			return name;
		}

		public Expr getValue() {
			return value;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Assign.class.getSimpleName() + "[", "]")
					.add("name=" + name)
					.add("value=" + value)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitAssignExpr(this);
		}
	}

	public static class Call<T> extends Expr {

		final Expr callee;
		final Token<T> paren;
		final List<Expr> arguments;

		public Call(final Expr callee, final Token<T> paren, final List<Expr> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		public Expr getCallee() {
			return callee;
		}

		public Token<T> getParen() {
			return paren;
		}

		public List<Expr> getArguments() {
			return arguments;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Call.class.getSimpleName() + "[", "]")
					.add("callee=" + callee)
					.add("paren=" + paren)
					.add("arguments=" + arguments)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitCallExpr(this);
		}
	}

	public static class Function extends Expr {

		final List<Token<?>> parameters;
		final List<Stmt> body;

		public Function(final List<Token<?>> parameters, final List<Stmt> body) {
			this.parameters = parameters;
			this.body = body;
		}

		public List<Token<?>> getParameters() {
			return parameters;
		}

		public List<Stmt> getBody() {
			return body;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Function.class.getSimpleName() + "[", "]")
					.add("parameters=" + parameters)
					.add("body=" + body)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitFunctionExpr(this);
		}
	}

	public static class Get<T> extends Expr {

		final Expr object;
		final Token<T> name;

		public Get(final Expr object, final Token<T> name) {
			this.object = object;
			this.name = name;
		}

		public Expr getObject() {
			return object;
		}

		public Token<T> getName() {
			return name;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Get.class.getSimpleName() + "[", "]")
					.add("object=" + object)
					.add("name=" + name)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitGetExpr(this);
		}
	}

	public static class Self<T> extends Expr {

		final Token<T> keyword;

		public Self(final Token<T> keyword) {
			this.keyword = keyword;
		}

		public Token<T> getKeyword() {
			return keyword;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Self.class.getSimpleName() + "[", "]")
					.add("keyword=" + keyword)
					.toString();
		}

		public <R> R accept(final Visitor<R> visitor) {
			return visitor.visitSelfExpr(this);
		}
	}


}
