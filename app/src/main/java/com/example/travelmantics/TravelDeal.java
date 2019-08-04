package com.example.travelmantics;

class TravelDeal {

    private String id;
    private String title;
    private String price;
    private String imageUrl;

    public TravelDeal(String title, String price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
