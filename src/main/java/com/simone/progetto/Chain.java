package com.simone.progetto;

import com.simone.progetto.utils.Configuration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Getter
@Setter
@Slf4j
public class Chain {
    private boolean toUpdate = true; //booleano che permette di syncronizzare la chain una sola volta
    private Node root;
    private Node startComputationNode;
    private ArrayList<Node> topList = new ArrayList<>();
    private final Object lockObj = new Object();

    public Chain() {
        //Creazione del block genesis
        root = startComputationNode = new Node(new Block());
        getTopList().add(root);
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
        for (Node n: getTopList()) {
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
        for (Node n: getTopList()) {
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
                toRet = new Node(tmp,node.getData());
            }
            tmp = tmp.getParent();
        }
        return toRet;
    }

    private void updateNodeToplist(Node newNode){
        getTopList().removeIf(node -> node.getData().getHash().equals(newNode.getData().getPreviousHash()));
        getTopList().add(newNode);
        this.sortToplist(); //Ordino i blocchi per altezza
    }

    private void cleanForkChain(){
        int maxHeight = this.getMaxHeightChain();
        if(getTopList().removeIf(node -> deleteRule(node, maxHeight))){
            log.info("{"+Configuration.UUID + "} Blockchain cleaned from fork !");
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
        log.info("{"+Configuration.UUID + "} ------------- CHAIN ----------------");
        for (Node r: getTopList()) {
            log.info("{"+Configuration.UUID + "} ------------- RAMO CHAIN ----------------");
            printChainAncestor(r);
            log.info("{"+Configuration.UUID + "} ----------------------------------");
        }
        log.info("{"+Configuration.UUID + "} ----------------------------------");
    }

    private void printChainAncestor(Node r){
        Node tmp = r;
        while(tmp != null){
            log.info("{"+Configuration.UUID + "} " + tmp.toString());
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
            log.info("{"+Configuration.UUID + "} hashcode for this block is wrong");
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

    public void sortToplist(){
        getTopList().sort(Comparator.comparing(Node::getHeight).reversed());
    }

    public ArrayList<Node> getTopList() {
        synchronized (lockObj){
            return topList;
        }
    }
}
