package org.jboss.arquillian.protocol.websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.jboss.arquillian.container.test.spi.command.Command;
import org.jboss.arquillian.container.test.spi.command.CommandCallback;
import org.jboss.arquillian.protocol.websocket.runner.CorrelatedPayload;
import org.jboss.arquillian.protocol.websocket.runner.ExecuteProtocol.SerializeDecoder;
import org.jboss.arquillian.protocol.websocket.runner.ExecuteProtocol.SerializeEncoder;
import org.jboss.arquillian.protocol.websocket.runner.ResponseBarrier;
import org.jboss.arquillian.protocol.websocket.runner.TestExecution;
import org.jboss.arquillian.test.spi.TestResult;

@ClientEndpoint(
        encoders = {SerializeEncoder.class},
        decoders = {SerializeDecoder.class})
public class ClientTestRunner {

    private Session session;
    private CommandCallback callback;
    
    public ClientTestRunner() {
    }
    
    public ClientTestRunner(CommandCallback callback) {
        this.callback = callback;
    }
    
    @OnOpen
    public void open(Session session) {
        this.session = session;
    }

    @OnClose
    public void close() {
        this.session = null;
    }

    public TestResult execute(String className, String methodName) {
        CorrelatedPayload request = new CorrelatedPayload(new TestExecution(className, methodName));
        try {
            session.getBasicRemote().sendObject(request);
        } catch(Exception e) {
            throw new RuntimeException("Could not send ExecutionRequest", e);
        }

        return ResponseBarrier.await(request, TestResult.class, 3);
    }
    
    @OnMessage
    public CorrelatedPayload onMessage(CorrelatedPayload message) {
        Object payload = message.getPayload();
        if(payload instanceof TestResult) {
            ResponseBarrier.arrived(message);
        } else if (payload instanceof Command) {
            callback.fired((Command<?>)payload);
            return new CorrelatedPayload(
                    message.getId(),
                    payload);
        }
        return null;
    }
}
