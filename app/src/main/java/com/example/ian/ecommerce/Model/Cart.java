package com.example.ian.ecommerce.Model;

public class Cart {
    private String pid, price, quantity, discount, pname;

    public Cart() {

    }

    public Cart(String pid, String price, String quantity, String discount, String pname) {
        this.pid = pid;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.pname = pname;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
