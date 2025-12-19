package com.example.shop_clothes.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZEO_EXCEPTION(9999, "uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email existed", HttpStatus.CONFLICT),
    EMAIL_INVALID(1003, "Email is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(1005, "Email not existed",HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(1006, "unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "you to not add permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

    USER_EXISTED(2001, "User already exists.", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(2002, "User does not exist in the system.", HttpStatus.NOT_FOUND),

    PHONENUMBER_EXISTED(2003, "Phone number already exists.", HttpStatus.CONFLICT),
    ;
    ErrorCode(int code, String message , HttpStatusCode httpStatusCode) {
        this.message = message;
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

}
