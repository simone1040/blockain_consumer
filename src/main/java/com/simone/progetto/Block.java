package com.simone.progetto;

import com.simone.progetto.utils.MyLogger;
import com.simone.progetto.utils.Utils;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Block implements Serializable {
    private Integer id_block; //Contatore che tiene traccia di quanti blocchi tiene la blockchain
    private String  id_consumer;//Id del miner che ha creato questo blocco
    private Transaction data;
    private String hash;
    private Random random = new Random();
    private String previousHash;
    private long timestamp;
    private static final Integer LOW_SECOND = 1000;
    private static final Integer HIGH_SECOND = 8500;

    public Block(Transaction data, String previousHash,Integer id_block) {
        this.id_block = id_block;
        this.data = data;
        this.id_consumer =  Constants.UUID;
        this.previousHash = previousHash;
        this.hash = this.computeHash(true);
    }

    public String computeHash(boolean compute_proof){
        String stringToHash = data.getStringToHash();
        if(compute_proof){
            this.computeProofOfWoork();
        }
        if(this.previousHash != null){
            stringToHash = this.previousHash  + timestamp + data.getStringToHash();
        }
        return Utils.applySha256(stringToHash);
    }

    public String getId_consumer() {
        return id_consumer;
    }

    private void computeProofOfWoork(){
        int mseconds = (random.nextInt(HIGH_SECOND-LOW_SECOND) + LOW_SECOND);
        try{
            Thread.sleep(mseconds);
            this.timestamp = new Date().getTime();
            MyLogger.getInstance().info(Block.class.getName() + " - " + Constants.UUID,"Tempo usato calcolare il proof of work in ms --> " + mseconds);
        }
        catch (InterruptedException ex){
            MyLogger.getInstance().info(Block.class.getName() + " - " + Constants.UUID,"Eccezione nello sleep --> " + ex);
        }
    }

    public Transaction getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }
    
    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Integer getId_block() {
        return id_block;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id_block=" + id_block +
                ", id_consumer=" + id_consumer +
                ", Client Product=" + data.getId_client() +
                ", Product name=" + data.getProduct().getName() +
                ", Product price=" + data.getProduct().getPrice() +
                ", Product quantity=" + data.getQuantity() +
                ", timestamp=" + timestamp +
                '}';
    }
}
