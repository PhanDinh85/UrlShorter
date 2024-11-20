package com.example.UrlShorter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BaseResponse<T> {

    private String status;
    private int code;
    private String message;
    private T data;
}
