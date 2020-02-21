package com.simone.progetto;

import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.MyLogger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class Chain {
    private boolean toUpdate = true; //booleano che permette di syncronizzare la chain una sola volta
    private Node root;
    private Node startComputationNode = null;
    private ArrayList<Node> topList = new ArrayList<Node>();

    public Chain() {
        //Creazione del block genesis
        root = startComputationNode = new Node(new Block());
        topList.add(root);
    }


    public int getMaxHeightChain(){
        return startComputationNode.getHeight();
    }


    public Block createBlock(Transaction transaction){
        //Dobbiamo prendere l'hash del blocco che sta nella catena più lunga o in caso di parità prendo la prima
        String prevHash = null;
        if(startComputationNode != null){
            prevHash = startComputationNode.getData().getHash();
        }
        return new Block(transaction,prevHash);
    }

    public boolean insertToChain(Block data){
        boolean toRet = false;
        Node toInsert = new Node(data);
        Node newNode = insert(toInsert);
        if(newNode != null){
            toRet = true;
            if(this.compareHeight(newNode)){//Aggiorno il punto da cui partire per il prossimo calcolo
                this.startComputationNode = newNode;
            }
            cleanForkChain();
        }
        return toRet;
    }

    private Node insert(Node toInsert){
        Node newInsert = null;
        for (Iterator<Node> it = topList.iterator(); it.hasNext();) {
            Node node = it.next();
            if(node.getData().getHash().equals(toInsert.getData().getPreviousHash())){
                newInsert = new Node(node,toInsert.getData(),node.getHeight() +1);
                it.remove();
            }
        }
        if(newInsert != null){
            topList.add(newInsert);
        }
        return newInsert;
    }

    private void cleanForkChain(){
        int maxHeight = this.getMaxHeightChain();
        topList.removeIf(node -> deleteRule(node, maxHeight));
    }


    private boolean deleteRule(Node node,int maxHeight){
        return node.getHeight() + 2 <= maxHeight;
    }

    public boolean insertToChain(Node toInsert){
        boolean toRet = false;
        Node newNode = insert(toInsert);
        if(newNode != null) {
            toRet = true;
            if (this.compareHeight(newNode)) {//Aggiorno il punto da cui partire per il prossimo calcolo
                this.startComputationNode = newNode;
            }
            cleanForkChain();
        }
        return toRet;
    }

    public void printChain(){
        MyLogger.getInstance().info(Chain.class.getName(),"------------- CHAIN ----------------");
        for (Node r: topList) {
            MyLogger.getInstance().info(Chain.class.getName(),"------------- RAMO CHAIN ----------------");
            printChainAncestor(r);
            MyLogger.getInstance().info(Chain.class.getName(),"----------------------------------");
        }
        MyLogger.getInstance().info(Chain.class.getName(),"----------------------------------");
    }

    private void printChainAncestor(Node r){
        MyLogger.getInstance().info(Chain.class.getName(),r.toString());
        if(r.getData().getHash().equals(Configuration.GENESIS_HASH)){
            return;
        }
        printChainAncestor(r.getParent());
    }

    private boolean compareHeight(Node lastInserted){
        boolean toRet = false;
        if(lastInserted.getHeight() > this.startComputationNode.getHeight()){
            toRet = true;
        }
        return toRet;
    }

    public Node searchBlock(String hash){
        Node toRet = null;
        for (Node r: topList) {
            Node res = Search(r,hash);
            if(res != null){
                toRet = res;
                break;
            }
        }
        return toRet;
    }

    public Node Search(Node node, String hash){
        if(node.getParent() == null){
            return null;
        }
        if(node.getData().getHash().equals(hash)){
            return node;
        }
        return Search(node.getParent(),hash);
    }

    private boolean checkHashTwoBlock(Block currentBlock, Block previousBlock){
        if(!currentBlock.getHash().equals(currentBlock.computeHash(false))){
            MyLogger.getInstance().info(Chain.class.getName() + " - " + Configuration.UUID,"Hashcode del blocco corrente non corretto");
            return false;
        }
        if(!previousBlock.getHash().equals(previousBlock.computeHash(false))){
            MyLogger.getInstance().info(Chain.class.getName() + " - " + Configuration.UUID,"Hashcode del blocco precedente non corretto ");
            return false;
        }
        return true;
    }

    private boolean checkValidityHash(Node node){
        if(node.getData().getPreviousHash() == null){
            return true;
        }
        if(!checkHashTwoBlock(node.getData(),node.getParent().getData())){
            return false;
        }
        return true;
    }
    //TODO
    private boolean isChainValid(List<Node> chain){
        boolean toRet = true;
        for (Node r: topList) {
            if(!checkValidityHash(r)){
                toRet = false;
                break;
            }
        }
        return toRet;
    }

    //TODO FARE REFACTORING
    public boolean setChain(List<Node> chain) {
        if (this.isChainValid(chain)){
            toUpdate = false;
            return true;
        }
        return false;
    }

    public boolean isToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(boolean toUpdate) {
        this.toUpdate = toUpdate;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getStartComputationNode() {
        return startComputationNode;
    }

    public void setStartComputationNode(Node startComputationNode) {
        this.startComputationNode = startComputationNode;
    }

}
