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
package org.jboss.arquillian.junit.container;


import org.jboss.arquillian.junit.State;
import org.jboss.arquillian.test.spi.TestResult;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.ExpectedException;

public class JUnitTestRunnerTestCase
{
   @Test
   public void shouldReturnExceptionToClientIfAnnotatedExpectedAndPassing() throws Exception
   {
      State.caughtTestException(new IllegalArgumentException());
      JUnitTestRunner runner = new JUnitTestRunner();
      TestResult result = runner.execute(TestScenarios.class, "shouldPassAnnotatedExpected");
      
      Assert.assertEquals(TestResult.Status.PASSED, result.getStatus());
      Assert.assertNotNull(result.getThrowable());
      Assert.assertEquals(IllegalArgumentException.class, result.getThrowable().getClass());
   }

   @Test
   public void shouldReturnExceptionToClientIfAnnotatedExpectedAndFailing() throws Exception
   {
      JUnitTestRunner runner = new JUnitTestRunner();
      TestResult result = runner.execute(TestScenarios.class, "shouldFailAnnotatedExpected");

      Assert.assertEquals(TestResult.Status.FAILED, result.getStatus());
      Assert.assertNotNull(result.getThrowable());
      Assert.assertEquals(AssertionError.class, result.getThrowable().getClass());
   }

   @Test
   public void shouldNotReturnExceptionToClientIfAsumptionPassing() throws Exception
   {
      JUnitTestRunner runner = new JUnitTestRunner();
      TestResult result = runner.execute(TestScenarios.class, "shouldPassOnAssumption");

      Assert.assertEquals(TestResult.Status.PASSED, result.getStatus());
      Assert.assertNull(result.getThrowable());
   }

   @Test
   public void shouldReturnExceptionToClientIfAsumptionFailing() throws Exception
   {
      JUnitTestRunner runner = new JUnitTestRunner();
      TestResult result = runner.execute(TestScenarios.class, "shouldSkipOnAssumption");

      Assert.assertEquals(TestResult.Status.SKIPPED, result.getStatus());
      Assert.assertNotNull(result.getThrowable());
      Assert.assertEquals(AssumptionViolatedException.class, result.getThrowable().getClass());
   }

   @Test
   public void shouldNotReturnExceptionToClientIfExpectedRulePassing() throws Exception
   {
      State.caughtTestException(new IllegalArgumentException());
      JUnitTestRunner runner = new JUnitTestRunner();
      TestResult result = runner.execute(TestScenarios.class, "shouldPassOnException");

      Assert.assertEquals(TestResult.Status.PASSED, result.getStatus());
      Assert.assertNull(result.getThrowable());
   }

   @Test
   public void shouldReturnExceptionToClientIfExpectedRuleFailing() throws Exception
   {
      JUnitTestRunner runner = new JUnitTestRunner();
      TestResult result = runner.execute(TestScenarios.class, "shouldFailOnException");

      Assert.assertEquals(TestResult.Status.FAILED, result.getStatus());
      Assert.assertNotNull(result.getThrowable());
      Assert.assertEquals(AssertionError.class, result.getThrowable().getClass());
   }

   public static class TestScenarios {

       @Rule
       public ExpectedException e = ExpectedException.none();

       @Test
       public void shouldSkipOnAssumption() throws Exception {
           Assume.assumeTrue(false);
       }

       @Test
       public void shouldPassOnAssumption() throws Exception {
           Assume.assumeTrue(true);
       }

       @Test
       public void shouldFailOnException() throws Exception {
           e.expect(IllegalArgumentException.class);
       }

       @Test
       public void shouldPassOnException() throws Exception {
           e.expect(IllegalArgumentException.class);
           throw new IllegalArgumentException();
       }

       @Test(expected = IllegalArgumentException.class)
       public void shouldPassAnnotatedExpected() throws Exception {
           throw new IllegalArgumentException();
       }

       @Test(expected = IllegalArgumentException.class)
       public void shouldFailAnnotatedExpected() throws Exception {
       }
   }
}
