package com.group4.chatapp.dtos;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignalMessage {
    private String type; // "offer", "answer", "candidate"
    private String caller;
    private String target;
    private Object sdp;
    private Object candidate;
}
