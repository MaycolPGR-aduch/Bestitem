package com.example.bestitem.data.model;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Business {
    private String id;          // docId
    private String name;
    private String category;
    private String address;
    private String phone;
    private Double lat;
    private Double lng;
    private String description;
    private Double rating;      // opcional
    private String imageUrl; // nueva propiedad
    @ServerTimestamp
    private Date createdAt;


    public Business() {} // Firestore necesita ctor vacío

    public Business(String name, String category, String address, String phone,
                    Double lat, Double lng, String description) {
        this.name = name; this.category = category; this.address = address;
        this.phone = phone; this.lat = lat; this.lng = lng; this.description = description;
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public Double getLat() { return lat; }
    public Double getLng() { return lng; }
    public String getDescription() { return description; }
    public Double getRating() { return rating; }
    public Date getCreatedAt() { return createdAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

}
