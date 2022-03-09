package com.balaabirami.abacusandroid.model;

public class StockTransaction {
    private String name;
    private int type;
    private long stockQuantity;
    private long salesQuantity;
    private long purchaseQuantity;
    private String date;
    private String franchiseID;
    private String franchiseName;
    private String studentState;

    public StockTransaction() {
    }

    public StockTransaction(String name, int type, long stockQuantity, long salesQuantity, long purchaseQuantity, String date, String franchiseID, String franchiseName, String studentState) {
        this.name = name;
        this.type = type;
        this.stockQuantity = stockQuantity;
        this.salesQuantity = salesQuantity;
        this.purchaseQuantity = purchaseQuantity;
        this.date = date;
        this.franchiseID = franchiseID;
        this.franchiseName = franchiseName;
        this.studentState = studentState;
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

    public long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public long getSalesQuantity() {
        return salesQuantity;
    }

    public void setSalesQuantity(long salesQuantity) {
        this.salesQuantity = salesQuantity;
    }

    public long getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(long purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
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

    public String getFranchiseName() {
        return franchiseName;
    }

    public void setFranchiseName(String franchiseName) {
        this.franchiseName = franchiseName;
    }

    public String getStudentState() {
        return studentState;
    }

    public void setStudentState(String studentState) {
        this.studentState = studentState;
    }

    public enum TYPE {
        ADD, REMOVE
    }
}
