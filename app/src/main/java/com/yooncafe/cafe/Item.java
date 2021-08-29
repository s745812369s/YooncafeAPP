package com.yooncafe.cafe;

public class Item {
    String title;
    String cafeCode;
    String cafeLoc;
    String cafeTel;
    String cafeTime;

    public Item(String title, String cafeCode,String cafeLoc,String cafeTel,String cafeTime ) {
        this.title = title;
        this.cafeCode = cafeCode;
        this.cafeLoc = cafeLoc;
        this.cafeTel = cafeTel;
        this.cafeTime = cafeTime;
    }
    public String gettitle() {
        return title;
    }
    public void settitle(String title) {
        this.title = title;
    }
    public String getcafeCode() {
        return cafeCode;
    }
    public void setcafeCode(String cafeCode) {
        this.cafeCode = cafeCode;
    }
    public String getcafeLoc() {
        return cafeLoc;
    }
    public void setcafeLoc(String cafeLoc) {
        this.cafeLoc = cafeLoc;
    }
    public String getcafeTel() {
        return cafeTel;
    }
    public void setcafeTel(String cafeTel) {
        this.cafeTel = cafeTel;
    }
    public String getcafeTime() {
        return cafeTime;
    }
    public void setcafeTime(String cafeTime) {
        this.cafeTime = cafeTime;
    }
}