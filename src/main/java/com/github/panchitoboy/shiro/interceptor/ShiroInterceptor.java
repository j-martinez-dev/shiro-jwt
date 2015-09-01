package com.github.panchitoboy.shiro.interceptor;

import java.lang.reflect.Method;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.apache.shiro.ShiroException;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AnnotationsAuthorizingMethodInterceptor;

@Interceptor
@SecurityChecked
public class ShiroInterceptor extends AnnotationsAuthorizingMethodInterceptor {

    @AroundInvoke
    public Object around(final InvocationContext ic) throws Exception {
        try {
            assertAuthorized(new InvocationContextToMethodInvocationConverter(ic));
        } catch (AuthorizationException exception) {
            Method m = ic.getMethod();
            String message = m.getAnnotation(SecurityChecked.class).message();

            if ("".equals(message)) {
                throw exception;
            } else {
                throw new ShiroException(message, exception);
            }

        }
        return ic.proceed();
    }

    private static class InvocationContextToMethodInvocationConverter implements MethodInvocation {

        private final InvocationContext context;

        public InvocationContextToMethodInvocationConverter(InvocationContext ctx) {
            context = ctx;
        }

        @Override
        public Object proceed() throws Exception {
            return context.proceed();
        }

        @Override
        public Method getMethod() {
            return context.getMethod();
        }

        @Override
        public Object[] getArguments() {
            return context.getParameters();
        }

        @Override
        public Object getThis() {
            return context.getTarget();
        }
    }

}
