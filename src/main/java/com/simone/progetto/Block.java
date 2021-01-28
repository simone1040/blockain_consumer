package com.simone.progetto;

import com.simone.progetto.bean.BeanUtil;
import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.InsertChainSemaphore;
import com.simone.progetto.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

@Getter
@Setter
@Slf4j
public class Block implements Serializable {
    private transient InsertChainSemaphore insertChainSemaphore;
    private String  id_consumer;//Id del miner che ha creato questo blocco
    private Transaction data;
    private String hash = "";
    private Integer nonce;
    private Random random = new Random();
    private String previousHash = null;
    private long timestamp = 0;

    public Block(Transaction data, String previousHash) {
        this.data = data;
        this.id_consumer =  Configuration.UUID;
        this.previousHash = previousHash;
        this.nonce = random.nextInt(Configuration.MAX_NONCE-Configuration.MIN_NONCE) + Configuration.MIN_NONCE;
        insertChainSemaphore = BeanUtil.getBean(InsertChainSemaphore.class);
        this.hash = this.computeHash(true);
    }

    public Block(){
        this.id_consumer = "";
        this.data = new Transaction(0,new Product("GENESIS",0),0,0);
        this.hash = Configuration.GENESIS_HASH;
    }

    public String computeHash(boolean compute_proof){
        if(compute_proof){
            this.computeProofOfWoork();
        }
        String stringToHash = nonce + timestamp + data.getStringToHash();
        if(this.previousHash != null){
            stringToHash = this.previousHash  + nonce + timestamp + data.getStringToHash();
        }
        return Utils.applySha256(stringToHash);
    }
    
    private void computeProofOfWoork(){
        int count_num_steps;
        int number_of_step = (random.nextInt(Configuration.MAX_NUMBER_OF_STEPS-Configuration.MIN_NUMBER_OF_STEPS) + Configuration.MIN_NUMBER_OF_STEPS);
        for(count_num_steps = 0; count_num_steps < number_of_step; count_num_steps++){
            if(insertChainSemaphore.isToCompute()){
                compute();
            }
        }
        if(insertChainSemaphore.isToCompute()){
            log.info("{"+Configuration.UUID + "} proof of work time computation --> " + Configuration.MS_TIME_COMPUTE * count_num_steps);
        }
        this.timestamp = new Date().getTime();
    }

    private void compute(){
        try{
            Thread.sleep(Configuration.MS_TIME_COMPUTE);
        }
        catch (InterruptedException ex){
            log.info("{"+Configuration.UUID + "} sleep Exception --> " + ex.getMessage() );
        }
    }

    @Override
    public String toString() {
        return "Block{" +
                "id_consumer=" + id_consumer +
                ", Client Product=" + data.getId_client() +
                ", Product name=" + data.getProduct().getName() +
                ", Product price=" + data.getProduct().getPrice() +
                ", Product quantity=" + data.getQuantity() +
                ", timestamp=" + timestamp +
                ", Hash=" + hash +
                ", PreviousHash=" + previousHash +
                '}';
    }
}
