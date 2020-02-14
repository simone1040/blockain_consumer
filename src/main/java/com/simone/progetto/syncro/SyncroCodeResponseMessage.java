package com.simone.progetto.syncro;

import com.simone.progetto.Block;
import com.simone.progetto.Constants;

import java.io.Serializable;
import java.util.ArrayList;

public class SyncroCodeResponseMessage implements Serializable {
    private String id_consumer;
    private Constants.Status_request_block type_request;
    private ArrayList<Block> block_of_transaction;

    public SyncroCodeResponseMessage(String id_consumer, Constants.Status_request_block type_request) {
        this.id_consumer = id_consumer;
        this.type_request = type_request;
    }

    public String getId_consumer() {
        return id_consumer;
    }

    public void setId_consumer(String id_consumer) {
        this.id_consumer = id_consumer;
    }

    public Constants.Status_request_block getType_request() {
        return type_request;
    }

    public void setType_request(Constants.Status_request_block type_request) {
        this.type_request = type_request;
    }

    public ArrayList<Block> getBlock_of_transaction() {
        return block_of_transaction;
    }

    public void setBlock_of_transaction(ArrayList<Block> block_of_transaction) {
        this.block_of_transaction = block_of_transaction;
    }
}
