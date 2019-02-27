/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.util;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.WebApplicationException;

/**
 * Interceptor class for catching exceptions thrown by services.
 */
public class APIExceptionInterceptor {

     // The method should not throw a exception because it is intended do convert these to Responses but TomEE is not going to deploy without throws declaration
    @AroundInvoke
    public Object handleException(InvocationContext context) throws Exception {
        Object proceedResponse;
        try {
            proceedResponse = context.proceed();
        } catch(WebApplicationException webEx) {
            return webEx.getResponse();
        } catch (Exception ex) {
            return Responses.errorInternal(ex);
        }
        return proceedResponse;
    }
}
