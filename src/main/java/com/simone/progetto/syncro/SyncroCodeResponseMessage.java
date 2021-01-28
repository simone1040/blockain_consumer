package com.simone.progetto.syncro;

import com.simone.progetto.Node;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Stack;

@Getter
@Setter
public class SyncroCodeResponseMessage implements Serializable {
    private String id_publisher;
    private String id_consumer;
    private Stack<Node> request_node = new Stack<>();

    public SyncroCodeResponseMessage(String id_publisher,String id_consumer) {
        this.id_publisher = id_publisher;
        this.id_consumer = id_consumer;
    }

    public void add_node(Node toSend){
        request_node.add(toSend);
    }
}
