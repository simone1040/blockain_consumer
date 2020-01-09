package com.simone.progetto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Chain {
    private ArrayList<Transaction> chain = new ArrayList<Transaction>();
    private static final Logger log = LoggerFactory.getLogger(Chain.class);

    public void insertElement(Transaction transaction){
        transaction.setHash(this.computeHash(transaction.getStringToHash()));
        chain.add(transaction);
    }

    private String computeHash(String blockHash){
        String stringToHash = blockHash;
        if(!chain.isEmpty()){
            stringToHash = chain.get(chain.size()-1).getHash() + blockHash;
        }
        String hash = Utils.applySha256(stringToHash);
        log.info("Hash dell'item --> " + hash);
        return hash;
    }
}
