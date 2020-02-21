package com.simone.progetto.syncro;
import com.simone.progetto.utils.Configuration;
import java.io.Serializable;

public class SyncroCodeRequestMessage implements Serializable {
    private String id_applicant;
    /* SE LO STATUS È ALL, ALLORA ARRAYLIST PUO RIMANERE VUOTA */
    private String request_block = null;

    public SyncroCodeRequestMessage(String id_applicant) {
        this.id_applicant = id_applicant;
    }

    public String getId_applicant() {
        return id_applicant;
    }

    public String getRequest_block() {
        return request_block;
    }

    public void setRequest_block(String request_block) {
        this.request_block = request_block;
    }

}
