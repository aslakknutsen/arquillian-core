package org.jboss.arquillian.protocol.websocket.runner;

import java.io.Serializable;

public class TestExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    private String className;
    private String methodName;

    public TestExecution(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "TestExecution [className=" + className + ", methodName="
                + methodName + "]";
    }
}
