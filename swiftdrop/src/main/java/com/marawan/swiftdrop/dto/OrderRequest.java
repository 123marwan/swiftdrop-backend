package com.marawan.swiftdrop.dto;

public class OrderRequest {
    private String orderDetails;
    private double totalPrice;

    public String getOrderDetails() { return orderDetails; }
    public void setOrderDetails(String orderDetails) { this.orderDetails = orderDetails; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}