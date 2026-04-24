package com.bookshelf.idp.libraryservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
public class ErrorObject {
    @JsonProperty("error_code")
    private Integer statusCode;

    @JsonProperty("error_message")
    private String message;

    @JsonProperty("error_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}