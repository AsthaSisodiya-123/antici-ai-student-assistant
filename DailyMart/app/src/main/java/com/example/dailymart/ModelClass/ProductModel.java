package com.example.dailymart.ModelClass;

public class ProductModel {
    int productImage;
    String productOffer,productName,productPrize,productDelivaryRate;

    public ProductModel(int productImage, String productOffer, String productName, String productPrize, String productDelivaryRate) {
        this.productImage = productImage;
        this.productOffer = productOffer;
        this.productName = productName;
        this.productPrize = productPrize;
        this.productDelivaryRate = productDelivaryRate;
    }

    public int getProductImage() {
        return productImage;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public String getProductOffer() {
        return productOffer;
    }

    public void setProductOffer(String productOffer) {
        this.productOffer = productOffer;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrize() {
        return productPrize;
    }

    public void setProductPrize(String productPrize) {
        this.productPrize = productPrize;
    }

    public String getProductDelivaryRate() {
        return productDelivaryRate;
    }

    public void setProductDelivaryRate(String productDelivaryRate) {
        this.productDelivaryRate = productDelivaryRate;
    }
}
