package com.simone.progetto.syncro;

import com.simone.progetto.Constants;
import com.simone.progetto.Transaction;

import java.io.Serializable;
import java.util.Date;

public class SyncroMessage implements Serializable {
    private String id_consumer;
    private long timestamp;
    private Transaction transaction;

    public SyncroMessage(Transaction transaction){
        this.id_consumer = Constants.UUID;
        this.timestamp = this.createTimestamp();
        this.transaction = transaction;
    }

    private long createTimestamp(){
        return  new Date().getTime();
    }

    public String getId_consumer() {
        return id_consumer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
