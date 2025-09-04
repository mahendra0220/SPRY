package com.maahi.spry.model;


import lombok.Data;

@Data
    public class BaseResponse<T> {

        private String message;

        private T data;
        public BaseResponse(String message, T data) {
            this.message = message;
            this.data = data;
        }
}
