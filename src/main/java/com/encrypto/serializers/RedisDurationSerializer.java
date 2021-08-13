package com.encrypto.serializers;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;

public class RedisDurationSerializer extends DurationSerializer {

    private static final long serialVersionUID = 1L;

    public static final DurationSerializer INSTANCE = new RedisDurationSerializer();

    private RedisDurationSerializer() {
        super();
    }

    protected RedisDurationSerializer(DurationSerializer base, Boolean useTimestamp, DateTimeFormatter dtf) {
        super(base, useTimestamp, dtf);
    }

    @Override
    public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException {

        if (useTimestamp(provider)) {
            if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(duration.getSeconds(), duration.getNano()));
            } else {
                generator.writeNumber(duration.toMillis());
            }
        } else {
            generator.writeString(duration.toString());
        }
    }
}