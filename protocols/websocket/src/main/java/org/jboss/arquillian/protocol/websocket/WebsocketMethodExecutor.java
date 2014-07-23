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
package org.jboss.arquillian.protocol.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.test.spi.ContainerMethodExecutor;
import org.jboss.arquillian.container.test.spi.command.CommandCallback;
import org.jboss.arquillian.protocol.websocket.runner.WebsocketEventRunner;
import org.jboss.arquillian.protocol.websocket.runner.WebsocketTestRunner;
import org.jboss.arquillian.test.spi.TestMethodExecutor;
import org.jboss.arquillian.test.spi.TestResult;

/**
 * ServletMethodExecutor
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class WebsocketMethodExecutor implements ContainerMethodExecutor
{
   protected ServletURIHandler uriHandler;
   protected CommandCallback callback;
   protected WebsocketProtocolConfiguration config;

   protected WebsocketMethodExecutor() {}
   
   public WebsocketMethodExecutor(WebsocketProtocolConfiguration config, Collection<HTTPContext> contexts, final CommandCallback callback)
   {
      if(config == null)
      {
         throw new IllegalArgumentException("ServletProtocolConfiguration must be specified");
      }
      if (contexts == null || contexts.size() == 0)
      {
         throw new IllegalArgumentException("HTTPContext must be specified");
      }
      if (callback == null)
      {
         throw new IllegalArgumentException("Callback must be specified");
      }
      this.config = config;
      this.uriHandler = new ServletURIHandler(config, contexts);
      this.callback = callback;
   }

   public TestResult invoke(final TestMethodExecutor testMethodExecutor)
   {
      if (testMethodExecutor == null)
      {
         throw new IllegalArgumentException("TestMethodExecutor must be specified");
      }

      URI targetBaseURI = uriHandler.locateTestServlet(testMethodExecutor.getMethod());
      URI targetURI = URI.create(targetBaseURI.toASCIIString() + WebsocketTestRunner.ARQUILLIAN_ENDPOINT_MAPPING);
      URI targetEventURI = URI.create(targetBaseURI.toASCIIString() + WebsocketEventRunner.ARQUILLIAN_ENDPOINT_MAPPING);

      ClientTestRunner runner = new ClientTestRunner(callback);
      ClientEventRunner event = new ClientEventRunner(callback);
      
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      Session runnerSession = null;
      Session eventSession = null;
      try {
          runnerSession = container.connectToServer(runner, targetURI);
          eventSession = container.connectToServer(event, targetEventURI);
      } catch(Exception e) {
          throw new RuntimeException("Could not connect to server", e);
      }
      
      // TODO: Create timeout
      while(!runnerSession.isOpen() && !eventSession.isOpen()) {
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
      }
      TestResult result = runner.execute(
              testMethodExecutor.getInstance().getClass().getName(),
              testMethodExecutor.getMethod().getName());
      
      try {
          eventSession.close();
          runnerSession.close();
      } catch(IOException e) {
          System.out.println("Could not close execution sessions: " + e.getMessage());
          e.printStackTrace();
      }
      return result;
   }
}
