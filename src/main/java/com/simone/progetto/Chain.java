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
        transaction.setHash(this.computeHash());
        chain.add(transaction);
    }

    private long computeHash(){
        log.info("Hash dell'item --> " + chain.hashCode());
        return chain.hashCode();
    }
}
