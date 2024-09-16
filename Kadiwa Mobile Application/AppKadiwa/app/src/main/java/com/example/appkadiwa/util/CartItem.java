package com.example.appkadiwa.util;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String itemId;
    private String productName;
    private double productPrice;
    private int productQuantity;
    private String imageUrl;

    public CartItem() {
    }

    public CartItem( String productName, double productPrice, int productQuantity, String imageUrl) {

        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.imageUrl = imageUrl;
    }

    protected CartItem(Parcel in) {
        itemId = in.readString();
        productName = in.readString();
        productPrice = in.readDouble();
        productQuantity = in.readInt();
        imageUrl = in.readString();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(productName);
        dest.writeDouble(productPrice);
        dest.writeInt(productQuantity);
        dest.writeString(imageUrl);
    }
}