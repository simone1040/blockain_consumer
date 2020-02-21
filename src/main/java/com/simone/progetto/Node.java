package com.simone.progetto;
import java.io.Serializable;

public class Node implements Serializable {
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

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Node{" +
                "height=" + height +
                ", data=" + data.toString() +
                '}';
    }
}
