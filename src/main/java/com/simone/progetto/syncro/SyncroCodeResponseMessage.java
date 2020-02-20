package com.simone.progetto.syncro;

import com.simone.progetto.Block;
import com.simone.progetto.Constants;
import com.simone.progetto.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SyncroCodeResponseMessage implements Serializable {
    private String id_publisher;
    private String id_consumer;
    private Constants.Status_request_block type_request;
    private List<Node> request_node = new ArrayList<Node>();

    public SyncroCodeResponseMessage(String id_publisher,String id_consumer, Constants.Status_request_block type_request) {
        this.id_publisher = id_publisher;
        this.id_consumer = id_consumer;
        this.type_request = type_request;
    }

    public String getId_publisher() {
        return id_publisher;
    }

    public String getId_consumer() {
        return id_consumer;
    }

    public Constants.Status_request_block getType_request() {
        return type_request;
    }

    public void setType_request(Constants.Status_request_block type_request) {
        this.type_request = type_request;
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
