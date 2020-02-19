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
        boolean toRet = true;
        int count = 0;
        Block currentBlock;
        Block previousBlock;
        MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"---------------- Stato chain -----------------");
        for (Block b: chain) {
            MyLogger.getInstance().info(Receiver.class.getName() + " - " + Constants.UUID,"Chain element " + count + " --> " + b.toString());
            count++;
        }
        MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"----------------------------------------------");
        for(int i = 1; i < chain.size(); i++){
            previousBlock = getElementChain(chain,i-1);
            currentBlock = getElementChain(chain,i);
            if(previousBlock != null && currentBlock != null){
                //Effettuiamo la comparazione tra l'hash registrato e quello computato sul momento
                if (!checkHashTwoBlock(currentBlock, previousBlock)) toRet = false;
            }
            else{
                toRet = false;
            }
        }
        return toRet;
    }

    public boolean setChain(ArrayList<Block> chain) {
        if (this.isChainValid(chain)){
            this.chain = new ArrayList<Block>(chain);
            toUpdate = false;
            return true;
        }
        return false;
    }

    public Block getElementChain(ArrayList<Block> blocksChain,Integer index){
        Block b = null;
        try {
            b = blocksChain.get(index);
        }
        catch (ArrayIndexOutOfBoundsException ex){
            MyLogger.getInstance().error(Chain.class.getName() + " - " + Constants.UUID,"Elemento in Chain non esistente --> "+ex.toString(),ex);
        }
        return b;
    }

    public boolean setBlockFromOtherConsumer(Block currentBlock){
        Block previousBlock;
        if(currentBlock.getId_block() > this.getIdLastBlock() + 1) { //Mancano alcuni blocchi nel mezzo e quindi ci sarà qualche errore
            return false;
        }
        else{
            previousBlock = getElementChain(chain,currentBlock.getId_block());
            if(previousBlock != null){
                if (checkHashTwoBlock(currentBlock, previousBlock)){
                    chain.set(currentBlock.getId_block(),currentBlock);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkHashTwoBlock(Block currentBlock, Block previousBlock) {
        if(!currentBlock.getHash().equals(currentBlock.computeHash(false))){
            MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"Hashcode del blocco corrente non corretto");
            return false;
        }
        if(!previousBlock.getHash().equals(previousBlock.computeHash(false))){
            MyLogger.getInstance().info(Chain.class.getName() + " - " + Constants.UUID,"Hashcode del blocco precedente non corretto ");
            return false;
        }
        return true;
    }




}
