package com.simone.progetto;

import com.simone.progetto.utils.MyLogger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Chain {
    private ArrayList<Block> chain = new ArrayList<Block>();

    public Block createBlock(Transaction transaction){
        return new Block(transaction,this.getPreviousHash(),this.getIdNewBlock());
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

    public void insertBlock(Block b){
        chain.add(b);
    }

    private String getPreviousHash(){
        String previousHash = null;
        if(!chain.isEmpty()){
            previousHash = chain.get(chain.size()-1).getHash();
        }
        return previousHash;
    }

    private Integer getIdNewBlock(){
        if(chain.size() == 0){
            return 0;
        }
        else{
            return chain.get(chain.size() - 1).getId_block() + 1;
        }
    }

    public Integer getIdLastBlock(){
        if(chain.size() == 0){
            return -1;
        }
        else{
            return chain.get(chain.size() - 1).getId_block();
        }
    }

    private boolean isChainValid(){
        MyLogger.getInstance().info(Chain.class.getName(),"---------------- Stato chain -----------------");
        for (Block b: chain) {
            MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Chain element --> " + b.toString());
        }
        MyLogger.getInstance().info(Chain.class.getName(),"----------------------------------------------");
        Block currentBlock;
        Block previousBlock;
        for(int i = 1; i < chain.size(); i++){
            previousBlock = chain.get(i-1);
            currentBlock = chain.get(i);
            //Effettuiamo la comparazione tra l'hash resgistrato e quello computato sul momento
            if(!currentBlock.getHash().equals(currentBlock.computeHash(false))){
                MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"Hashcode del blocco corrente non corretto");
                return false;
            }
            if(!previousBlock.getHash().equals(previousBlock.computeHash(false))){
                MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"Hashcode del blocco precedente non corretto ");
                return false;
            }
        }
        return true;
    }

}
