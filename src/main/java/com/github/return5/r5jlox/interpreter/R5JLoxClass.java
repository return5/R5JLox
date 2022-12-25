package main.java.com.github.return5.r5jlox.interpreter;


public class R5JLoxClass {

    private final String name;

    public R5JLoxClass(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
