package com.redhat.widget.rest;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.redhat.widget.config.OffsetDateTimeDeserializer;
import com.redhat.widget.config.OffsetDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private static final String OFFSET_DATE_TIME_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OFFSET_DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime timestamp;

    private long status;

    private String error;

    private String message;

    private String path;

}
