package org.aspectj.util;

import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class Utils {

    public static final String CONFIGURATION_PREFIX = "org.aspectj.weaver.loadtime.configuration.";
    public static final String DEBUG_OPTION = CONFIGURATION_PREFIX + "debug";
    public static final String CONFIGURATION_FILTER = CONFIGURATION_PREFIX + "filter";
    public static final String CONFIGURATION_MAVEN_REPOSITORY = "repo.remote.url";

    public static final String MVEL_PREFIX = "mvel:";
    public static final String MVEL_PACKAGE_PREFIX = "org.mvel2";

    public static Object checkMvelExpression(String source){
        if(source.startsWith(MVEL_PREFIX)){
            String expression = source.substring(MVEL_PREFIX.length());
            return MVEL.eval(expression);
        } else {
            return source;
        }
    }

    public static Throwable unwrapMvelException(RuntimeException exception) {
        Throwable resultException = exception;
        while (resultException!=null && resultException.getStackTrace()!=null
                && resultException.getStackTrace().length>0 &&
                (resultException.getStackTrace()[0].getClassName().startsWith(MVEL_PACKAGE_PREFIX)
                    || resultException.getClass().isAssignableFrom(InvocationTargetException.class))){
            if(resultException.getCause()==null) break;
            resultException = resultException.getCause();
        }
        return resultException;
    }

    public static Object executeMvelExpression(Serializable compiledScript, VariableResolverFactory variableResolverFactory) throws Throwable {
        try {
            return MVEL.executeExpression(compiledScript, variableResolverFactory);
        } catch (RuntimeException exception) {
            throw unwrapMvelException(exception);
        }
    }

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static Serializable compileMvelExpression(String expression) {
        return MVEL.compileExpression(expression);
    }

    public static boolean isMavenClassLoader() {
        boolean isMavenClassLoader = false;
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        for(StackTraceElement traceElement: stackTrace){
            if(MavenLoader.class.getName().equals(traceElement.getClassName())){
                isMavenClassLoader = true;
                break;
            }
        }
        return isMavenClassLoader;
    }
}
