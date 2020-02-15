package com.simone.progetto;

import com.simone.progetto.utils.MyLogger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Chain {
    private boolean toUpdate = true; //booleano che permette di syncronizzare la chain una sola volta
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

    public boolean isToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(boolean toUpdate) {
        this.toUpdate = toUpdate;
    }

    private boolean isChainValid(ArrayList<Block> chain){
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

    public boolean setChain(ArrayList<Block> chain) {
        if (this.isChainValid(chain)){
            this.chain = new ArrayList<Block>(chain);
            toUpdate = false;
            return true;
        }
        return false;
    }

    public boolean setBlockFromOtherConsumer(Block currentBlock){
        Block previousBlock = null;
        if(currentBlock.getId_block() > this.getIdLastBlock() + 1) { //Mancano alcuni blocchi nel mezzo e quindi ci sarà qualche errore
            return false;
        }
        else{
            try {
                 previousBlock = chain.get(currentBlock.getId_block() - 1);
            } catch ( IndexOutOfBoundsException e ) {
                MyLogger.getInstance().error(Chain.class.getName(),"Exception blocco non presente -->",e);
                return false;
            }
            if(previousBlock != null){
                if(!currentBlock.getHash().equals(currentBlock.computeHash(false))){
                    MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"Hashcode del blocco corrente non corretto");
                    return false;
                }
                if(!previousBlock.getHash().equals(previousBlock.computeHash(false))){
                    MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"Hashcode del blocco precedente non corretto ");
                    return false;
                }
                chain.set(currentBlock.getId_block(),currentBlock);
                return true;
            }
            return false;
        }
    }

}
