package com.youyu.bean;

import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class DelegatingSecurityContextSupplier<T> implements Supplier<T> {

    private final Callable<T> delegate;

    public DelegatingSecurityContextSupplier(Supplier<T> original) {
        SecurityContext contextCopy = SecurityContextHolder.getContext(); // 提前捕获
        this.delegate = new DelegatingSecurityContextCallable<>(original::get, contextCopy);
    }

    @Override
    public T get() {
        try {
            return delegate.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
