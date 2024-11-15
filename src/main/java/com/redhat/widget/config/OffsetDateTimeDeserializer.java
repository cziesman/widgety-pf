package com.redhat.widget.config;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    private static final Logger LOG = LoggerFactory.getLogger(OffsetDateTimeDeserializer.class);

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            SHORT_DATE_FORMATTER
    };

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

        String str = p.getText();
        LOG.debug(str);
        for (DateTimeFormatter dateFormat : DATE_FORMATS) {
            try {
                return OffsetDateTime.parse(str, dateFormat);
            } catch (DateTimeParseException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        return null;
    }

}
