package com.simone.progetto;

import com.simone.progetto.utils.MyLogger;
import com.simone.progetto.utils.Utils;
import java.io.Serializable;
import java.util.Random;

public class Block implements Serializable {
    private Integer id_block; //Contatore che tiene traccia di quanti blocchi tiene la blockchain
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
        this.previousHash = previousHash;
        this.hash = this.computeHash();
    }

    public String computeHash(){
        String stringToHash = data.getStringToHash();
        if(this.previousHash != null){
            stringToHash = this.previousHash + timestamp + data.getStringToHash();
        }
        this.computeProofOfWoork();
        return Utils.applySha256(stringToHash);
    }

    private void computeProofOfWoork(){
        int mseconds = (random.nextInt(HIGH_SECOND-LOW_SECOND) + LOW_SECOND);
        try{
            Thread.sleep(mseconds );
            MyLogger.getInstance().info(Block.class.getName() + " - " + Constants.UUID,"Tempo usato calcolare il proof of work in ms --> " + mseconds);
        }
        catch (InterruptedException ex){
            MyLogger.getInstance().info(Block.class.getName(),"Eccezione nello sleep --> " + ex);
        }
    }

    public Transaction getData() {
        return data;
    }

    public void setData(Transaction data) {
        this.data = data;
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

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getId_block() {
        return id_block;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id_block=" + id_block +
                ", data=" + data +
                ", hash='" + hash + '\'' +
                ", random=" + random +
                ", previousHash='" + previousHash + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
