/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.arquillian.protocol.websocket.runner.WebsocketEventRunner;
import org.jboss.arquillian.protocol.websocket.runner.WebsocketTestRunner;
import org.jboss.arquillian.protocol.websocket.test.MockTestRunner;
import org.jboss.arquillian.test.spi.TestMethodExecutor;
import org.junit.After;
import org.junit.Before;

/**
 * AbstractServerBase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class AbstractServerBase
{
    public static final int PORT = 8181;
    
    protected Server server;

   @Before
   public void setup() throws Exception
   {
      server = new Server(PORT);

      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath("/arquillian-protocol");
      server.setHandler(context);
      
      ServerContainer container = WebSocketServerContainerInitializer.configureContext(context);
      container.addEndpoint(WebsocketTestRunner.class);
      container.addEndpoint(WebsocketEventRunner.class);
      

      server.start();
   }

   @After
   public void cleanup() throws Exception
   {
      MockTestRunner.clear();
      server.stop();
   }

   protected Collection<HTTPContext> createContexts()
   {
       List<HTTPContext> context = new ArrayList<HTTPContext>();
       context.add(createContext());
       return context;
   }
   protected HTTPContext createContext()
   {
      URI baseURI = createBaseURL();
      HTTPContext context = new HTTPContext(baseURI.getHost(), baseURI.getPort());
      context.add(new Servlet(WebsocketTestRunner.ARQUILLIAN_ENDPOINT_NAME, baseURI.getPath()));
      return context;
   }

   protected URI createBaseURL()
   {
      return URI.create("ws://localhost:" + PORT + "/arquillian-protocol");
   }

   public static class MockTestExecutor implements TestMethodExecutor, Serializable
   {

      private static final long serialVersionUID = 1L;

      public void invoke(Object... parameters) throws Throwable
      {
      }

      public Method getMethod()
      {
         try
         {
            return this.getClass().getMethod("getMethod");
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not find my own method ?? ");
         }
      }

      public Object getInstance()
      {
         return this;
      }
   }
}
