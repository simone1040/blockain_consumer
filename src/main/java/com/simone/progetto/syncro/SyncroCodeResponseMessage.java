package com.simone.progetto.syncro;

import com.simone.progetto.utils.Configuration;
import com.simone.progetto.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SyncroCodeResponseMessage implements Serializable {
    private String id_publisher;
    private String id_consumer;
    private List<Node> request_node = new ArrayList<Node>();

    public SyncroCodeResponseMessage(String id_publisher,String id_consumer) {
        this.id_publisher = id_publisher;
        this.id_consumer = id_consumer;
    }

    public String getId_publisher() {
        return id_publisher;
    }

    public String getId_consumer() {
        return id_consumer;
    }

    public List<Node> getRequest_node() {
        return request_node;
    }

    public void setRequest_node(List<Node> request_node) {
        this.request_node = new ArrayList<Node>(request_node);
    }

    public void add_node(Node toSend){
        request_node.add(toSend);
    }
}
