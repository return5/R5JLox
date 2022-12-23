package main.java.com.github.return5.r5jlox.errors;

public class Return extends RuntimeException{
    final Object value;
    public Return(final Object value) {
        super(null,null,false,false);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
