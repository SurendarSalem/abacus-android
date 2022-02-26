package com.balaabirami.abacusandroid.model;

public class StockTransaction {
    private String name;
    private int type;
    private long quantity;
    private long prevQuantity;
    private long newQuantity;
    private String date;
    private String franchiseID;

    public StockTransaction() {
    }

    public StockTransaction(String name, int type, long quantity, long prevQuantity, long newQuantity, String date, String franchiseID) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.prevQuantity = prevQuantity;
        this.newQuantity = newQuantity;
        this.date = date;
        this.franchiseID = franchiseID;
    }

    public static String createID() {
        return "TR" + System.currentTimeMillis();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getPrevQuantity() {
        return prevQuantity;
    }

    public void setPrevQuantity(long prevQuantity) {
        this.prevQuantity = prevQuantity;
    }

    public long getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(long newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFranchiseID() {
        return franchiseID;
    }

    public void setFranchiseID(String franchiseID) {
        this.franchiseID = franchiseID;
    }

    public enum TYPE {
        ADD, REMOVE
    }
}
