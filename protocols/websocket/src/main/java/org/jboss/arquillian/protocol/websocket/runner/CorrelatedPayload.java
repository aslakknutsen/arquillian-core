package org.jboss.arquillian.protocol.websocket.runner;

import java.io.Serializable;
import java.util.UUID;

public class CorrelatedPayload implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Object payload;
    
    public CorrelatedPayload(Object payload) {
        this(UUID.randomUUID().toString(), payload);
    }

    public CorrelatedPayload(String id, Object payload) {
        this.id = id;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "CorrelatedPayload [id=" + id + ", payload=" + payload + "]";
    }
}
