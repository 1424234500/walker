package com.walker.socket.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Data
@Accessors(chain = true)
public class MessageUserPK implements Serializable {
    private String id;
    private String msgId;
}