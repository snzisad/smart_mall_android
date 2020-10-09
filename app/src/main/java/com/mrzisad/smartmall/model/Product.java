package com.mrzisad.smartmall.model;

public class Product {
    private String description;
    private String id;
    private String name;
    private String picture;
    private String price;
    private int quantity;
    public Shop shop;
    private String size;

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

    public String getSize() {
        return this.size;
    }

    public void setSize(String size2) {
        this.size = size2;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price2) {
        this.price = price2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description2) {
        this.description = description2;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(String picture2) {
        this.picture = picture2;
    }

    public Integer getQuantity() {
        return Integer.valueOf(this.quantity);
    }

    public void setQuantity(Integer quantity2) {
        this.quantity = quantity2.intValue();
    }

    public class Shop {
        private String address;
        private String bkash;
        private String email;
        private String id;
        private String name;
        private String phone;
        private String rocket;
        private String shopname;
        private String shopno;
        private String type;

        public Shop() {
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

        public String getEmail() {
            return this.email;
        }

        public void setEmail(String email2) {
            this.email = email2;
        }
    }
}