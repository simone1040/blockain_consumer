package com.simone.progetto;

import com.simone.progetto.utils.Configuration;
import com.simone.progetto.utils.MyLogger;
import org.springframework.stereotype.Component;

import java.util.*;

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
        Node toInsert = new Node(data);
        boolean insert = false;
        for (Node n: topList) {
            if(n.getData().getHash().equals(data.getPreviousHash())){
                toInsert = new Node(n,data);
                insert = true;
                break;
            }
            else{
                toInsert = insertFromOtherConsumer(n.getParent(),toInsert);
                if(toInsert != null){
                    insert = true;
                    break;
                }
            }
        }
        if(insert){
            this.updateNodeToplist(toInsert);
            this.updateStartComputationNode(toInsert);
            cleanForkChain();
        }
        return insert;
    }

    public boolean insertToChain(Node toInsert){
        boolean insert = false;
        for (Node n: topList) {
            if(n.getData().getHash().equals(toInsert.getData().getPreviousHash())){
                toInsert = new Node(n,toInsert.getData());
                insert = true;
                break;
            }
            else{
                toInsert = insertFromOtherConsumer(n.getParent(),toInsert);
                if(toInsert != null){
                    insert = true;
                    break;
                }
            }
        }
        if(insert){
            this.updateNodeToplist(toInsert);
            this.updateStartComputationNode(toInsert);
            cleanForkChain();
        }
        return insert;
    }

    private Node insertFromOtherConsumer(Node top,Node node){
        Node tmp = top;
        Node toRet = null;
        while(tmp != null && toRet == null){
            if(tmp.getData().getHash().equals(node.getData().getPreviousHash())){
                toRet = new Node(top,node.getData());
            }
            tmp = tmp.getParent();
        }
        return toRet;
    }

    private void updateNodeToplist(Node newNode){
        topList.removeIf(node -> node.getData().getHash().equals(newNode.getData().getPreviousHash()));
        topList.add(newNode);
        this.sortToplist(); //Ordino i blocchi per altezza
    }

    private void cleanForkChain(){
        int maxHeight = this.getMaxHeightChain();
        if(topList.removeIf(node -> deleteRule(node, maxHeight))){
            MyLogger.getInstance().info(Chain.class.getName()+ " - " + Configuration.UUID,"Blockchain pulita dai fork !");
        }
    }

    private boolean deleteRule(Node node,int maxHeight){
        return node.getHeight() + 2 <= maxHeight;
    }

    public void updateStartComputationNode(Node node){
        if (this.compareHeight(node)) {//Aggiorno il punto da cui partire per il prossimo calcolo
            this.startComputationNode = node;
        }
    }

    public void printChain(){
        MyLogger.getInstance().info(Chain.class.getName()+ " - " + Configuration.UUID,"------------- CHAIN ----------------");
        for (Node r: topList) {
            MyLogger.getInstance().info(Chain.class.getName()+ " - " + Configuration.UUID,"------------- RAMO CHAIN ----------------");
            printChainAncestor(r);
            MyLogger.getInstance().info(Chain.class.getName()+ " - " + Configuration.UUID,"----------------------------------");
        }
        MyLogger.getInstance().info(Chain.class.getName()+ " - " + Configuration.UUID,"----------------------------------");
    }

    private void printChainAncestor(Node r){
        Node tmp = r;
        while(tmp != null){
            MyLogger.getInstance().info(Chain.class.getName()+ " - " + Configuration.UUID,tmp.toString());
            tmp = tmp.getParent();
        }
    }

    private boolean compareHeight(Node lastInserted){
        boolean toRet = false;
        if(lastInserted.getHeight() > this.startComputationNode.getHeight()){
            toRet = true;
        }
        return toRet;
    }

    public boolean checkHashBlock(Block currentBlock){
        if(!currentBlock.getHash().equals(currentBlock.computeHash(false))){
            MyLogger.getInstance().info(Chain.class.getName() + " - " + Configuration.UUID,"Hashcode del blocco corrente non corretto");
            return false;
        }
        return true;
    }

    public Stack<Node> searchListOfBlock(Node topNode,String searchHash){
        Stack<Node> toRet = new Stack<>();
        boolean trovato = false;
        Node tmp = topNode;
        while (tmp != null && !trovato){
            if(tmp.getData().getHash().equals(searchHash)){
                trovato = true;
            }
            else{
                toRet.push(tmp);
            }
            tmp = tmp.getParent();
        }
        if(trovato){
            return  toRet;
        }
        return null;
    }

    public boolean isToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(boolean toUpdate) {
        this.toUpdate = toUpdate;
    }

    public void restartTopList(Node node){
        topList = new ArrayList<>();
        topList.add(node);
    }

    public void restartTopList(){
        topList = new ArrayList<>();
        startComputationNode = root;
        topList.add(root);
    }

    public Node getStartComputationNode() {
        return startComputationNode;
    }

    public ArrayList<Node> getTopList() {
        return topList;
    }

    public void sortToplist(){
        topList.sort(Comparator.comparing(Node::getHeight).reversed());
    }

}
