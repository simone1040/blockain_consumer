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
        Block block = new Block(transaction,this.getPreviousHash(),this.getLastIdBlock());
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

    private Integer getLastIdBlock(){
        if(chain.size() == 0){
            return 0;
        }
        else{
            return chain.get(chain.size() - 1).getId_block();
        }
    }

    private boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        for(int i = 0; i < chain.size() - 1;i++){
            if(i == 0){
                previousBlock = null;
                currentBlock = chain.get(i);
            }
            else{
                previousBlock = chain.get(i-1);
                currentBlock = chain.get(i);
            }
            //Effettuiamo la comparazione tra l'hash resgistrato e quello computato sul momento
            if(!currentBlock.getHash().equals(currentBlock.computeHash())){
                log.info("Hashcode del blocco corrente non corretto");
                return false;
            }
            if(!previousBlock.getHash().equals(previousBlock.computeHash())){
                log.info("Hashcode del blocco precedente non corretto ");
                return false;
            }
        }
        return true;
    }

}
