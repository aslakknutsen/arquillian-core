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

import java.lang.reflect.Field;

import org.jboss.arquillian.protocol.websocket.WebsocketMethodExecutor;
import org.jboss.arquillian.protocol.websocket.WebsocketProtocolConfiguration;
import org.jboss.arquillian.protocol.websocket.runner.WebsocketCommandService;
import org.jboss.arquillian.protocol.websocket.test.MockTestRunner;
import org.jboss.arquillian.protocol.websocket.test.TestCommandCallback;
import org.jboss.arquillian.protocol.websocket.test.TestIntegerCommand;
import org.jboss.arquillian.protocol.websocket.test.TestStringCommand;
import org.jboss.arquillian.test.spi.TestResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * ServletCommandServiceTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class WebsocketCommandServiceTestCase extends AbstractServerBase
{
   @Test
   public void shouldBeAbleToTransfereCommand() throws Exception
   {
      Object[] results = new Object[] {"Wee", 100};

      MockTestRunner.add(TestResult.passed());
      MockTestRunner.add(new TestStringCommand());
      MockTestRunner.add(new TestIntegerCommand());

      WebsocketMethodExecutor executor = new WebsocketMethodExecutor(
              new WebsocketProtocolConfiguration(),
              createContexts(),
              new TestCommandCallback(results));

      TestResult result = executor.invoke(new MockTestExecutor());

      Assert.assertEquals(
            "Should have returned a passed test",
            MockTestRunner.wantedResults.getStatus(),
            result.getStatus());

      Assert.assertNull(
            "Exception should have been thrown",
            result.getThrowable());

      Assert.assertEquals(
            "Should have returned command",
            results[0],
            MockTestRunner.commandResults.get(0));

      Assert.assertEquals(
            "Should have returned command",
            results[1],
            MockTestRunner.commandResults.get(1));
   }

   @Test
   public void shouldDisableCommandService() throws Exception
   {
      Field f = WebsocketCommandService.class.getDeclaredField("TIMEOUT");
      f.setAccessible(true);
      f.set(null, 500);

      WebsocketProtocolConfiguration config = new WebsocketProtocolConfiguration();
      config.setPullInMilliSeconds(0);

      Object[] results = new Object[] {"Wee", 100};

      MockTestRunner.add(TestResult.failed(null));
      MockTestRunner.add(new TestStringCommand());

      WebsocketMethodExecutor executor = new WebsocketMethodExecutor(
              config,
              createContexts(),
              new TestCommandCallback(results));

      TestResult result = executor.invoke(new MockTestExecutor());

      Assert.assertEquals(
            "Should have returned a passed test",
            MockTestRunner.wantedResults.getStatus(),
            result.getStatus());

      Assert.assertNotNull(
            "Exception should have been thrown",
            result.getThrowable());

      Assert.assertTrue(
            "Timeout exception should have been thrown",
            result.getThrowable().getMessage().contains("timeout"));
   }
}
