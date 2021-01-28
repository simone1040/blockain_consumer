package com.simone.progetto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Node implements Serializable {
    private Node parent = null;
    private Integer height = 1;
    private Block data;

    public Node(Node parent, Block data) {
        this.parent = parent;
        this.data = data;
        this.height = parent.getHeight() + 1;
    }

    public Node(Block data) {
        this.data = data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    @Override
    public String toString() {
        return "Node{" +
                "height=" + height +
                ", data=" + data.toString() +
                '}';
    }
}
