package com.redhat;

import com.redhat.ResultCollector.AfterStepPayload;
import com.redhat.ResultCollector.AfterSuitePayload;
import com.redhat.ResultCollector.BeforeStepPayload;
import com.redhat.ResultCollector.InitPayload;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import java.util.HashMap;

public class MessageCodecs {
    public static class BeforeStepPayloadCodec implements MessageCodec<BeforeStepPayload,BeforeStepPayload> {

        @Override
        public void encodeToWire(Buffer buffer, BeforeStepPayload s) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'encodeToWire'");
        }

        @Override
        public BeforeStepPayload decodeFromWire(int pos, Buffer buffer) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'decodeFromWire'");
        }

        @Override
        public BeforeStepPayload transform(BeforeStepPayload s) {
             
            return new BeforeStepPayload(s.step(), new HashMap<>(s.ctx()));
        }

        @Override
        public String name() {
            return BeforeStepPayloadCodec.class.getName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }

    }

    public static class AfterStepPayloadCodec implements MessageCodec<AfterStepPayload,AfterStepPayload> {

        @Override
        public void encodeToWire(Buffer buffer, AfterStepPayload s) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'encodeToWire'");
        }

        @Override
        public AfterStepPayload decodeFromWire(int pos, Buffer buffer) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'decodeFromWire'");
        }

        @Override
        public AfterStepPayload transform(AfterStepPayload s) {
            return new AfterStepPayload(s.step(), new HashMap<>(s.ctx()));
        }

        @Override
        public String name() {
            return AfterStepPayloadCodec.class.getName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }

    }

    public static class AfterSuitePayloadCodec implements MessageCodec<AfterSuitePayload,AfterSuitePayload> {

        @Override
        public void encodeToWire(Buffer buffer, AfterSuitePayload s) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'encodeToWire'");
        }

        @Override
        public AfterSuitePayload decodeFromWire(int pos, Buffer buffer) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'decodeFromWire'");
        }

        @Override
        public AfterSuitePayload transform(AfterSuitePayload s) {
            return new AfterSuitePayload(s.suite(), new HashMap<>(s.ctx()));
        }

        @Override
        public String name() {
            return AfterSuitePayloadCodec.class.getName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }

    }

    public static class InitPayloadCodec implements MessageCodec<InitPayload,InitPayload> {

        @Override
        public void encodeToWire(Buffer buffer, InitPayload s) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'encodeToWire'");
        }

        @Override
        public InitPayload decodeFromWire(int pos, Buffer buffer) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'decodeFromWire'");
        }

        @Override
        public InitPayload transform(InitPayload s) {
            return  new InitPayload(s.file(), s.model());
        }

        @Override
        public String name() {
            return InitPayloadCodec.class.getName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }

    }
    
}
