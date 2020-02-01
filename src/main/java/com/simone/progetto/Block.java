package com.simone.progetto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Block {
    private Transaction data;
    private String hash;
    private Random random = new Random();
    private String previousHash;
    private long timestamp;
    private static final Integer LOW_SECOND = 1000;
    private static final Integer HIGH_SECOND = 4500;
    private static final Logger log = LoggerFactory.getLogger(Block.class);

    public Block(Transaction data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.hash = this.computeHash();
    }

    private String computeHash(){
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
            log.info("Tempo usato calcolare il proof of work in ms --> " + mseconds);
        }
        catch (InterruptedException ex){
            log.info("Eccezione nello sleep --> " + ex);
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
}
