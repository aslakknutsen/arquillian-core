package org.jboss.arquillian.junit.event;

import java.lang.reflect.Method;

import org.jboss.arquillian.test.spi.LifecycleMethodExecutor;
import org.jboss.arquillian.test.spi.event.suite.AfterTestLifecycleEvent;

/**
 * 
 * Event fired Before running the JUnit rules. LifecycleMethodExecutor controls if
 * the Rules should be invoked or not. 
 *
 */
public class AfterAroundRules extends AfterTestLifecycleEvent {

    /**
     * @param testInstance The test case instance being tested
     * @param testMethod The test method that is about to be executed
     * @param executor A call back when the LifecycleMethod represented by this event should be invoked
     */
    public AfterAroundRules(Object testInstance, Method testMethod, LifecycleMethodExecutor executor)
    {
       super(testInstance, testMethod, executor);
    }

}
