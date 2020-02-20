package com.simone.progetto.syncro;


import com.simone.progetto.Constants;

import java.io.Serializable;
import java.util.ArrayList;

public class SyncroCodeRequestMessage implements Serializable {
    private String id_applicant;
    private Constants.Status_request_block request;
    /* SE LO STATUS È ALL, ALLORA ARRAYLIST PUO RIMANERE VUOTA */
    private ArrayList<String> request_block = new ArrayList<String>();

    public SyncroCodeRequestMessage(String id_applicant, Constants.Status_request_block request) {
        this.id_applicant = id_applicant;
        this.request = request;
    }

    public String getId_applicant() {
        return id_applicant;
    }

    public Constants.Status_request_block getRequest() {
        return request;
    }

    public ArrayList<String> getRequest_block() {
        return request_block;
    }

    public void setRequest_block(ArrayList<String> request_block) {
        this.request_block = request_block;
    }

    public void addRequestBlock(String indexBlock){
        request_block.add(indexBlock);
    }
}
