package com.simone.progetto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Node implements Serializable {
    private List<Node> children = new ArrayList<Node>();
    private Node parent = null;
    private Integer height = 1;
    private Block data = null;

    public Node(Node parent, Block data,Integer height) {
        this.parent = parent;
        this.data = data;
        this.height = height;
    }

    public Node(Block data) {
        this.data = data;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Block getData() {
        return data;
    }

    public void setData(Block data) {
        this.data = data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }


    /*ritorno il figlio */
    public Node addChild(Node child) {
        child.setParent(this);
        child.setHeight(this.getHeight() + 1);
        this.children.add(child);
        return child;
    }

    @Override
    public String toString() {
        return "Node{" +
                "height=" + height +
                ", data=" + data.toString() +
                '}';
    }
}
