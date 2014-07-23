/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.protocol.websocket.runner;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.arquillian.container.test.spi.TestRunner;
import org.jboss.arquillian.container.test.spi.command.Command;
import org.jboss.arquillian.container.test.spi.util.TestRunners;
import org.jboss.arquillian.protocol.websocket.runner.ExecuteProtocol.SerializeDecoder;
import org.jboss.arquillian.protocol.websocket.runner.ExecuteProtocol.SerializeEncoder;
import org.jboss.arquillian.test.spi.TestResult;

/**
 * ServletTestRunner
 * 
 * The server side executor for the Servlet protocol impl.
 * 
 * Supports multiple output modes ("outputmode"):
 *  - html
 *  - serializedObject 
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
@ServerEndpoint(
        value = WebsocketEventRunner.ARQUILLIAN_ENDPOINT_MAPPING,
        encoders = {SerializeEncoder.class},
        decoders = {SerializeDecoder.class})
public class WebsocketEventRunner
{
   public static final String ARQUILLIAN_ENDPOINT_NAME = "ArquillianEventRunner";

   public static final String ARQUILLIAN_ENDPOINT_MAPPING = "/" + ARQUILLIAN_ENDPOINT_NAME;

   private static Session session;
   
   @OnClose
   public void removeSession(Session session) {
       WebsocketEventRunner.session = null;
   }
   
   @OnOpen
   public void storeSession(Session session) {
       WebsocketEventRunner.session = session;
   }

   @OnMessage
   public CorrelatedPayload onMessage(CorrelatedPayload message) throws Exception {
       Object payload = message.getPayload();
       if(payload instanceof TestExecution) {
           return executeTest(message);
       } else if (payload instanceof Command) {
           ResponseBarrier.arrived(message);
       }
       return null;
   }

   public CorrelatedPayload executeTest(CorrelatedPayload message) throws Exception {
       TestExecution test = (TestExecution)message.getPayload();
       return new CorrelatedPayload(
               message.getId(),
               executeTest(
                       test.getClassName(),
                       test.getMethodName()));
   }
   
   public static Command<?> sendCommand(Command<?> command) {
       Session session = WebsocketEventRunner.session;
       if(session == null) {
           throw new RuntimeException("No session found for client");
       }
       
       CorrelatedPayload payload = new CorrelatedPayload(command);
       try {
           session.getBasicRemote().sendObject(payload);
       } catch(Exception e) {
           throw new RuntimeException("Could not send ExecutionRequest", e);
       }
       return ResponseBarrier.await(payload, Command.class, 3);
   }
  

   private TestResult executeTest(String className, String methodName) throws ClassNotFoundException
   {
      try { 
          Class<?> testClass = SecurityActions.getThreadContextClassLoader().loadClass(className);
          TestRunner runner = TestRunners.getTestRunner();
          return runner.execute(testClass, methodName);
      } catch(Exception e) {
          return TestResult.failed(e);
      }
   }
}
