package org.example;

public class BSTNode {
    public String articul;
    public int price;
    public BSTNode left;
    public BSTNode right;
    public int height;

    public BSTNode(String articul, int price) {
        this.articul = articul;
        this.price = price;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
}