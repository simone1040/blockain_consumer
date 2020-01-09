package com.simone.progetto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Chain {
    private ArrayList<Block> chain = new ArrayList<Block>();
    private static final Logger log = LoggerFactory.getLogger(Chain.class);

    public void insertElement(Transaction transaction){
        //TODO ASPETTO TEMPO RANDOM PER SIMULAZIONE PROOF OF WORK
        Block block = new Block(transaction,this.getPreviousHash());
        log.info("Hashcode del blocco --> " + block.getHash());
        chain.add(block);
    }

    private String getPreviousHash(){
        String previousHash = null;
        if(!chain.isEmpty()){
            previousHash = chain.get(chain.size()-1).getHash();
        }
        return previousHash;
    }

}
