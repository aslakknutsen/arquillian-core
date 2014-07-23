package org.jboss.arquillian.protocol.websocket.runner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ExecuteProtocol {

    public static class SerializeEncoder implements Encoder.Binary<CorrelatedPayload> {
        
        @Override
        public ByteBuffer encode(CorrelatedPayload object) throws EncodeException {
            String className = this.getClass().getSimpleName().replaceAll("(Decoder|Encoder)", "");
            byte[] marker = className.getBytes();
            byte[] encodedObject = encodeObject(object).array();
            byte[] complete = new byte[marker.length + encodedObject.length];
            
            System.arraycopy(marker, 0, complete, 0, marker.length);
            System.arraycopy(encodedObject, 0, complete, marker.length, encodedObject.length);

            return ByteBuffer.wrap(encodedObject);
        }
        
        @Override
        public void destroy() { }
        
        @Override
        public void init(EndpointConfig config) { }
    }

    public static class SerializeDecoder implements Decoder.Binary<CorrelatedPayload> {

        @Override
        public CorrelatedPayload decode(ByteBuffer bytes) throws DecodeException {
            return (CorrelatedPayload)decodeObject(bytes);
        }

        @Override
        public void destroy() { }

        @Override
        public void init(EndpointConfig config) { }

        @Override
        public boolean willDecode(ByteBuffer bytes) {
            return true;
        }
    }

    private static ByteBuffer encodeObject(Object object) throws EncodeException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(object);
            return ByteBuffer.wrap(out.toByteArray());

        } catch (IOException e) {
            throw new EncodeException(object, "Could not encode result", e);
        }
    }
    private static Object decodeObject(ByteBuffer bb) throws DecodeException {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bb.array());
            
            ObjectInputStream objOut = new ObjectInputStream(in);
            return objOut.readObject();

        } catch (IOException e) {
            throw new DecodeException(bb, "Could not decode result", e);
        } catch (ClassNotFoundException e) {
            throw new DecodeException(bb, "Could not decode result", e);
        }
    }
}
