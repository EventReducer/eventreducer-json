package org.eventreducer.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.net.ntp.TimeStamp;
import org.eventreducer.Command;
import org.eventreducer.Event;
import org.eventreducer.Serializable;
import org.eventreducer.Serializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class EventReducerModule extends SimpleModule {

    public EventReducerModule() {
        super("EventReducerModule", new Version(1,0,0,null, "org.eventreducer.json", "EventReducerModule"));

        addSerializer(new TimestampSerializer());
        addSerializer(new SerializerSerializer());
        addSerializer(new BigDecimalSerializer());
        addDeserializer(TimeStamp.class, new TimestampDeserializer());
        addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        setMixInAnnotation(Serializable.class, SerializableMixin.class);
        setMixInAnnotation(Command.class, CommandMixin.class);
        setMixInAnnotation(Event.class, EventMixin.class);
    }

    public static abstract class SerializableMixin {

        @JsonProperty("@hash")
        public abstract <T extends Serializable> Serializer<T> entitySerializer() throws ClassNotFoundException, IllegalAccessException, InstantiationException;

    }

    public static abstract class CommandMixin {

        @JsonProperty("@trace") @JsonInclude(JsonInclude.Include.NON_NULL)
        public abstract Object trace();

        @JsonProperty("@trace")
        public abstract void trace(Object trace);

        @JsonProperty("timestamp")
        public abstract TimeStamp timestamp();
        @JsonProperty("timestamp")
        public abstract TimeStamp timestamp(TimeStamp timeStamp);

        @JsonProperty("uuid")
        public abstract UUID uuid();
        @JsonProperty("uuid")
        public abstract UUID uuid(UUID uuid);

    }

    public static abstract class EventMixin {

        @JsonProperty("timestamp")
        public abstract TimeStamp timestamp();
        @JsonProperty("timestamp")
        public abstract TimeStamp timestamp(TimeStamp timeStamp);

        @JsonProperty("uuid")
        public abstract UUID uuid();
        @JsonProperty("uuid")
        public abstract UUID uuid(UUID uuid);

    }

    static class BigDecimalSerializer extends StdSerializer<BigDecimal> {
        protected BigDecimalSerializer() {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toPlainString());
        }

        @Override
        public void serializeWithType(BigDecimal value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }
    }

    static class BigDecimalDeserializer extends StdDeserializer<BigDecimal> {
        public BigDecimalDeserializer() {
            super(BigDecimal.class);
        }

        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return new BigDecimal(p.readValueAs(String.class));
        }

        @Override
        public BigDecimal deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            return new BigDecimal(p.readValueAs(String.class));
        }
    }


    static class SerializerSerializer extends StdSerializer<Serializer> {
        protected SerializerSerializer() {
            super(Serializer.class);
        }

        @Override
        public void serialize(Serializer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeBinary(value.hash());
        }

        @Override
        public void serializeWithType(Serializer value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }
    }

    static class TimestampSerializer extends StdSerializer<TimeStamp> {

        protected TimestampSerializer() {
            super(TimeStamp.class);
        }

        @Override
        public void serialize(TimeStamp value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toString());
        }

        @Override
        public void serializeWithType(TimeStamp value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            gen.writeString(value.toString());
        }
    }

    static class TimestampDeserializer extends StdDeserializer<TimeStamp> {
        public TimestampDeserializer() {
            super(TimeStamp.class);
        }

        @Override
        public TimeStamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return new TimeStamp(p.readValueAs(String.class));
        }

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            return new TimeStamp(p.readValueAs(String.class));
        }
    }

}
