package com.mrzisad.smartmall.model;

public class Order {
    private Customer customer;
    private String id;
    private String payment_type;
    private Product product;
    private String quantity;
    private String size;
    private String trxnid;

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product2) {
        this.product = product2;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer2) {
        this.customer = customer2;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id2) {
        this.id = id2;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity2) {
        this.quantity = quantity2;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size2) {
        this.size = size2;
    }

    public String getPayment_type() {
        return this.payment_type;
    }

    public void setPayment_type(String payment_type2) {
        this.payment_type = payment_type2;
    }

    public String getTrxnid() {
        return this.trxnid;
    }

    public void setTrxnid(String trxnid2) {
        this.trxnid = trxnid2;
    }

    public class Product {
        private String id;
        private String name;
        private String picture;
        private String price;

        public Product() {
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

        public String getPrice() {
            return this.price;
        }

        public void setPrice(String price2) {
            this.price = price2;
        }

        public String getPicture() {
            return this.picture;
        }

        public void setPicture(String picture2) {
            this.picture = picture2;
        }
    }

    public class Customer {
        private String address;
        private String name;
        private String phone;

        public Customer() {
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

        public String getPhone() {
            return this.phone;
        }

        public void setPhone(String phone2) {
            this.phone = phone2;
        }
    }
}
