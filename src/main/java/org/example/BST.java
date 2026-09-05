package org.example;

import java.util.List;

public class BST {
    private BSTNode root;
    private int comparisons;
    private int nodeCount;

    public BST() {
        this.root = null;
        comparisons = 0;
        nodeCount = 0;
    }

    public void insert(String articul, int price) {
        root = insertRec(root, articul, price);
    }

    public BSTNode insertRec(BSTNode node, String articul, int price) {
        if (node == null) {
            nodeCount++;
            return new BSTNode(articul, price);
        }

        int cmp = articul.compareTo(node.articul);


        if (cmp < 0) {
            node.left = insertRec(node.left, articul, price);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, articul, price);
        } else {
            node.price = price;
        }
        return node;
    }

    public int search(String articul) {
        comparisons = 0;
        BSTNode result = searchRec(root, articul);
        return result != null ? result.price : -1;
    }

    private BSTNode searchRec(BSTNode node, String articul) {
        if (node == null) {
            return null;
        }

        comparisons++;
        int cmp = articul.compareTo(node.articul);

        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchRec(node.left, articul);
        } else {
            return searchRec(node.right, articul);
        }
    }

    public int getHeight() {
        return getHeightRec(root);
    }

    private int getHeightRec(BSTNode node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeightRec(node.left), getHeightRec(node.right));
    }

    public int getSize() {
        return nodeCount;
    }

    public int getComparisons() {
        return comparisons;
    }

}