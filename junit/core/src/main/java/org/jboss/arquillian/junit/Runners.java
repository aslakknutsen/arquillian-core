package org.jboss.arquillian.junit;

import org.jboss.arquillian.junit.suite.Suite;
import org.jboss.arquillian.test.spi.event.suite.SubSuiteEvent.SubSuiteClass;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;


public final class Runners {

    public static Runner runners(Class<?> testClass) throws InitializationError {
        if(testClass.isAnnotationPresent(Suite.class)) {
            return new ArquillianSuiteRunner(testClass);
        }
        return new Arquillian(testClass);
    }

    public static Runner runners(SubSuiteClass subSuiteClass, Class<?> testClass) throws InitializationError {
        if(testClass.isAnnotationPresent(Suite.class)) {
            return new ArquillianSuiteRunner(subSuiteClass, testClass);
        }
        return new Arquillian(subSuiteClass, testClass);
    }
}
