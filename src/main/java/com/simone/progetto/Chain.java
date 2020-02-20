package com.simone.progetto;

import com.simone.progetto.utils.MyLogger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Chain {
    private boolean toUpdate = true; //booleano che permette di syncronizzare la chain una sola volta
    private List<Node> root = new ArrayList<Node>();
    private Node startComputationNode = null;

    public Chain() {}

    //TODO METEDO CHE MI DA L'ALTEZZA DELLO START COMPUTATION NODE


    public Block createBlock(Transaction transaction){
        //Dobbiamo prendere l'hash del blocco che sta nella catena più lunga o in caso di parità prendo la prima
        String prevHash = null;
        if(startComputationNode != null){
            prevHash = startComputationNode.getData().getHash();
        }
        return new Block(transaction,prevHash);
    }

    public boolean insertToChain(Block data,boolean dataFromOtherConsumer){
        boolean toRet = false;
        Node toInsert = new Node(data);
        if(data.getPreviousHash() == null) { //Nodo radice
            root.add(toInsert);
            if(this.compareHeight(toInsert)){//Aggiorno il punto da cui partire per il prossimo calcolo
                this.startComputationNode = toInsert;
            }
            toRet = true;
        }
        else{
            if(root.size() > 0){
                for (Node r: root) {
                    if(insert(r,toInsert,dataFromOtherConsumer)){
                        toRet = true;
                        break;
                    }
                }
            }
        }
        return toRet;
    }

    public boolean insertToChain(Node data,boolean dataFromOtherConsumer){
        boolean toRet = false;
        if(data.getData().getPreviousHash() == null) { //Nodo radice
            root.add(data);
            if(this.compareHeight(data)){//Aggiorno il punto da cui partire per il prossimo calcolo
                this.startComputationNode = data;
            }
            toRet = true;
        }
        else{
            if(root.size() > 0){
                for (Node r: root) {
                    if(insert(r,data,dataFromOtherConsumer)){
                        toRet = true;
                        break;
                    }
                }
            }
        }
        return toRet;
    }

    public void setStartComputationNode(){
        for (Node r: root) {
            this.getMaxDepthNode(r);
        }
    }
    public Node getMaxDepthNode(Node node){
        if(startComputationNode == null || node.getHeight() > startComputationNode.getHeight()){
            startComputationNode = node;
        }
        if(node.isLeaf()){
            return null;
        }
        for (Node n: node.getChildren()) {
            return getMaxDepthNode(n);
        }
        return null;
    }

    public boolean hasBrotherResearch(Node node,String toSearch) {
        if (node.isLeaf()) {
            return false;
        }
        else if(node.getData().getHash().equals(toSearch) && node.getChildren().size() > 1) { //è il padre ed ha più di un figlio
            return true;
        }
        else{
            for(Node child: node.getChildren()) {
                return hasBrotherResearch(child,toSearch);
            }
        }
        return false;
    }

    public boolean hasBrother(String parentHashToSearch){
        boolean toRet = false;
        if(root.size() > 0){
            for (Node r: root) {
                if(hasBrotherResearch(r,parentHashToSearch)){
                    toRet = true;
                    break;
                }
            }
        }
        return toRet;
    }

    private boolean insert(Node node,Node toInsert,boolean fromOtherConsumer){
        Node newChild = null;
        if(node.getData().getHash().equals(toInsert.getData().getPreviousHash())){
            if(checkHashTwoBlock(toInsert.getData(),node.getData())){
                newChild = node.addChild(toInsert);
                if(newChild != null){
                    if(this.compareHeight(newChild)){//Aggiorno il punto da cui partire per il prossimo calcolo
                        this.startComputationNode = newChild;
                        return true;
                    }
                }
                return false;
            }
        }
        else{
            for(Node child: node.getChildren()) {
                return insert(child,toInsert,fromOtherConsumer);
            }
        }
        return false;
    }

    public void printChain(){
        MyLogger.getInstance().info(Chain.class.getName(),"------------- CHAIN ----------------");
        for (Node r: this.root) {
            orderPreWalk(r);
        }
        MyLogger.getInstance().info(Chain.class.getName(),"----------------------------------");
    }

    private void orderPreWalk(Node r){
        MyLogger.getInstance().info(Chain.class.getName(),r.toString());
        if(r.isLeaf()){
            return;
        }
        for (Node ch: r.getChildren()) {
            orderPreWalk(ch);
        }
    }

    private boolean compareHeight(Node lastInserted){
        boolean toRet = false;
        if(this.startComputationNode == null){
            toRet = true;
        }
        else{
            if(lastInserted.getHeight() > this.startComputationNode.getHeight()){
                toRet = true;
            }
        }
        return toRet;
    }

    public Node search(String hash){
        Node toRet = null;
        if(root.size() > 0){
            for (Node r: root) {
                if(r.getData().getHash().equals(hash)){
                    toRet = r;
                    break;
                }
                Node s = searchRecursive(r,hash);
                if(s != null){
                    toRet = s;
                    break;
                }
            }
        }
        return toRet;
    }

    public Node searchRecursive(Node parent,String hash){
        if(parent.getData().getHash().equals(hash)){
            return parent;
        }
        else{
            if(parent.isLeaf()){
                return null;
            }
            else{
                for (Node child: parent.getChildren()) {
                    return searchRecursive(child,hash);
                }
            }
        }
        return null;
    }

    private boolean checkHashTwoBlock(Block currentBlock, Block previousBlock){
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

    private boolean checkValidityHash(Node node){
        for (Node ch: node.getChildren()) {
            if(!checkHashTwoBlock(ch.getData(),node.getData())){
                return false;
            }
        }
        if(node.isLeaf()){
            return true;
        }
        for (Node ch: node.getChildren()) {
            checkValidityHash(ch);
        }
        return true;
    }
    //TODO
    private boolean isChainValid(List<Node> chain){
        boolean toRet = true;
        for (Node r: this.root) {
            if(!checkValidityHash(r)){
                toRet = false;
                break;
            }
        }
        return toRet;
    }

    public boolean setChain(List<Node> chain) {
        if (this.isChainValid(chain)){
            this.root = chain;
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

    public List<Node> getRoot() {
        return root;
    }

    public void setRoot(List<Node> root) {
        this.root = root;
    }

    public Node getStartComputationNode() {
        return startComputationNode;
    }

    public void setStartComputationNode(Node startComputationNode) {
        this.startComputationNode = startComputationNode;
    }

}
