package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Chat_message")
@Getter
@Setter
public class ChatMessage extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "session_id")
    private ChatSession session;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "message")
    private String message;

    @Column(name = "sent_time")
    private Date sentTime;
}

