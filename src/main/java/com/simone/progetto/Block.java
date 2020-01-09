package com.simone.progetto;

public class Block {
    private Transaction data;
    private String hash;
    private String previousHash;
    private long timestamp;

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
        return Utils.applySha256(stringToHash);
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
