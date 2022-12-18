package main.java.com.github.return5.r5jlox.parser;

@FunctionalInterface
public interface ConstructorFunc<R,T,U,V> {
    R construct(final T t,final U u,final V v);
}
