package com.simone.progetto.syncro;
import com.simone.progetto.Block;
import com.simone.progetto.utils.Configuration;
import java.io.Serializable;
import java.util.Date;

public class SyncroMessage implements Serializable {
    private String id_consumer;
    private long timestamp;
    private Block block;

    public SyncroMessage(Block b){
        this.id_consumer = Configuration.UUID;
        this.timestamp = this.createTimestamp();
        this.block = b;
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

    public Block getBlock() {
        return block;
    }
}
