package com.github.panchitoboy.shiro.interceptor;

import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AnnotationsAuthorizingMethodInterceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.NotAuthorizedException;
import java.lang.reflect.Method;

/**
 * An interceptor for declarative security checks using the annotations from the
 * {@link org.apache.shiro.authz.annotation} package.
 *
 * This class is similair to {@link org.ops4j.pax.shiro.cdi.interceptor.ShiroInterceptor} except it
 * maps the Shiro generated {@link org.apache.shiro.authz.UnauthorizedException} to JAX-RS exception
 * when "JAX-RS" is passed as the type.
 *
 * <pre>
 * <code>
 *
 *   {@literal @}SecurityChecked(type = "JAX-RS")
 *   class My RestSevice
 *   {
 *
 *   }
 * </code>
 * </pre>
 *
 * doing this will return the right HTTP error code to the client when used with JAX-RS services.
 * You can use the same annotation without the type when not used with JAX-RS services.
 *
 */
@Interceptor
@SecurityChecked
public class ShiroAuthorizationInterceptor extends AnnotationsAuthorizingMethodInterceptor {

    @AroundInvoke
    public Object around(final InvocationContext ic) throws Exception {
        try {
            assertAuthorized(new InvocationContextToMethodInvocationConverter(ic));
        } catch (AuthorizationException exception) {

            // If it is a authorization exception get the type string from annotation

            // Need to check to see if the annotation is on a method or class level
            SecurityChecked classAnn = ic.getMethod().getDeclaringClass().getAnnotation(SecurityChecked.class);
            SecurityChecked methodAnn = ic.getMethod().getAnnotation(SecurityChecked.class);

            String type = null;

            if(classAnn != null)
            {
                type = classAnn.type();
            }
            // Method level annotation takes precedence over class level
            if(methodAnn != null)
            {
                type = methodAnn.type();
            }

            if(type != null && type.equalsIgnoreCase("JAX-RS")) {

                throw new NotAuthorizedException("Unauthorized", exception);
            }
            else
            {
                throw exception;
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
