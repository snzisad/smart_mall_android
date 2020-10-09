package com.mrzisad.smartmall.model;

public class User {
    private String address;
    private String bkash;
    private Category category;
    private String email;
    private String id;
    private String name;
    private String phone;
    private String rocket;
    private Shopingmall shopingmall;
    private String shopname;
    private String shopno;
    private String type;

    public class Shopingmall {
        private String id;
        private String name;

        public Shopingmall() {
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id2) {
            this.id = id2;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            this.name = name2;
        }
    }

    public class Category {
        private String id;
        private String name;

        public Category() {
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id2) {
            this.id = id2;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            this.name = name2;
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id2) {
        this.id = id2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address2) {
        this.address = address2;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type2) {
        this.type = type2;
    }

    public String getShopname() {
        return this.shopname;
    }

    public void setShopname(String shopname2) {
        this.shopname = shopname2;
    }

    public String getShopno() {
        return this.shopno;
    }

    public void setShopno(String shopno2) {
        this.shopno = shopno2;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone2) {
        this.phone = phone2;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email2) {
        this.email = email2;
    }

    public Shopingmall getShopingmall() {
        return this.shopingmall;
    }

    public void setShopingmall(Shopingmall shopingmall2) {
        this.shopingmall = shopingmall2;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category customer) {
        this.category = customer;
    }

    public String getBkash() {
        return this.bkash;
    }

    public void setBkash(String bkash2) {
        this.bkash = bkash2;
    }

    public String getRocket() {
        return this.rocket;
    }

    public void setRocket(String rocket2) {
        this.rocket = rocket2;
    }
}