package org.jboss.arquillian.protocol.websocket.runner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseBarrier {

    private static ResponseBarrier instance = new ResponseBarrier();

    private ConcurrentHashMap<String, CountDownLatch> requestLatchs = new ConcurrentHashMap<String, CountDownLatch>();
    private ConcurrentHashMap<String, CorrelatedPayload> responses = new ConcurrentHashMap<String, CorrelatedPayload>();

    
    private ResponseBarrier() {
    }
    
    public static <T> T await(CorrelatedPayload request, Class<T> responseType, int seconds) {
        CountDownLatch latch = new CountDownLatch(1);
        instance.requestLatchs.put(request.getId(), latch);
        
        try {
            if(!latch.await(seconds, TimeUnit.SECONDS) ) {
                throw new RuntimeException("No response within timeout for " + request);
            }
            instance.requestLatchs.remove(request.getId());
            
            CorrelatedPayload response = instance.responses.remove(request.getId());
            return responseType.cast(response.getPayload());
        } catch(InterruptedException e) {
            throw new RuntimeException("Interupted", e);
        }

    }
    
    public static void arrived(CorrelatedPayload response) {
        if(instance.requestLatchs.containsKey(response.getId())) {
            instance.responses.put(response.getId(), response);
            instance.requestLatchs.get(response.getId()).countDown();
        } else {
            System.out.println("Unknown response");
        }
    }
    
}
