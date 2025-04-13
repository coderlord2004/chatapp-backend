package com.group4.chatapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class ChatRoomNotFoundException extends ErrorResponseException {
    public ChatRoomNotFoundException() {
        super(HttpStatus.NOT_FOUND);
        this.setTitle("Provided ChatRoom not found!");
    }
}
