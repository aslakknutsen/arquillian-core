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

import org.jboss.arquillian.protocol.websocket.test.MockTestRunner;
import org.jboss.arquillian.protocol.websocket.test.TestCommandCallback;
import org.jboss.arquillian.test.spi.TestResult;
import org.junit.Assert;
import org.junit.Test;


/**
 * ProtocolTestCase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ProtocolTestCase extends AbstractServerBase 
{
   
   @Test
   public void shouldReturnTestResult() throws Exception 
   {
      MockTestRunner.add(TestResult.passed());
      
      WebsocketMethodExecutor executor = createExecutor();
      TestResult result = executor.invoke(new MockTestExecutor());
      
      Assert.assertEquals(
            "Should have returned a passed test",
            MockTestRunner.wantedResults.getStatus(),
            result.getStatus());
      
      Assert.assertNull(
            "No Exception should have been thrown",
            result.getThrowable());
   }
   
   @Test
   public void shouldReturnThrownException() throws Exception 
   {
      MockTestRunner.add(TestResult.failed(new Exception().fillInStackTrace()));
      
      WebsocketMethodExecutor executor = createExecutor();
      TestResult result = executor.invoke(new MockTestExecutor());
      
      Assert.assertEquals(
            "Should have returned a passed test",
            MockTestRunner.wantedResults.getStatus(),
            result.getStatus());
      
      Assert.assertNotNull(
            "Exception should have been thrown",
            result.getThrowable());
      
   }
/*
   @Test @Ignore
   public void shouldReturnExceptionWhenMissingTestClassParameter() throws Exception
   {
      URL url = createURL(WebsocketTestRunner.OUTPUT_MODE_SERIALIZED, null, null);
      TestResult result = (TestResult)TestUtil.execute(url);
      
      Assert.assertEquals(
            "Should have returned a passed test",
            Status.FAILED,
            result.getStatus());
      
      Assert.assertTrue(
            "No Exception should have been thrown",
            result.getThrowable() instanceof IllegalArgumentException);
   }
   
   @Test @Ignore
   public void shouldReturnExceptionWhenMissingMethodParameter() throws Exception
   {
      URL url = createURL(WebsocketTestRunner.OUTPUT_MODE_SERIALIZED, "org.my.test", null);
      TestResult result = (TestResult)TestUtil.execute(url);
      
      Assert.assertEquals(
            "Should have returned a passed test",
            Status.FAILED,
            result.getStatus());
      
      Assert.assertTrue(
            "No Exception should have been thrown",
            result.getThrowable() instanceof IllegalArgumentException);
   }
   
   @Test @Ignore
   public void shouldReturnExceptionWhenErrorLoadingClass() throws Exception
   {
      URL url = createURL(WebsocketTestRunner.OUTPUT_MODE_SERIALIZED, "org.my.test", "test");
      TestResult result = (TestResult)TestUtil.execute(url);
      
      Assert.assertEquals(
            "Should have returned a passed test",
            Status.FAILED,
            result.getStatus());
      
      Assert.assertTrue(
            "No Exception should have been thrown",
            result.getThrowable() instanceof ClassNotFoundException);
   }
*/
   protected WebsocketMethodExecutor createExecutor()
   {
       return new WebsocketMethodExecutor(
               new WebsocketProtocolConfiguration(),
               createContexts(), 
               new TestCommandCallback());
   }
}
