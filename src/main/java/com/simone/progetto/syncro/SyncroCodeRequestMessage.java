package com.simone.progetto.syncro;
import com.simone.progetto.Constants;
import java.io.Serializable;

public class SyncroCodeRequestMessage implements Serializable {
    private String id_applicant;
    private Constants.Status_request_block request;
    /* SE LO STATUS È ALL, ALLORA ARRAYLIST PUO RIMANERE VUOTA */
    private String request_block = null;

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

    public String getRequest_block() {
        return request_block;
    }

    public void setRequest_block(String request_block) {
        this.request_block = request_block;
    }

}
