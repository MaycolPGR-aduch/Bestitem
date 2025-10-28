package com.example.bestitem.ui.businessdetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bestitem.R;

public class BusinessDetailActivity extends AppCompatActivity{

    private String name, category, address, phone, description, imageUrl;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_detail);

        // UI
        ImageView img = findViewById(R.id.imgCover);
        TextView txtName = findViewById(R.id.txtName);
        TextView txtCategory = findViewById(R.id.txtCategory);
        TextView txtAddress = findViewById(R.id.txtAddress);
        TextView txtPhone = findViewById(R.id.txtPhone);
        TextView txtDesc = findViewById(R.id.txtDescription);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnNavigate = findViewById(R.id.btnNavigate);

        // Extras
        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            category = getIntent().getStringExtra("category");
            address = getIntent().getStringExtra("address");
            phone = getIntent().getStringExtra("phone");
            description = getIntent().getStringExtra("description");
            imageUrl = getIntent().getStringExtra("imageUrl");
            lat = getIntent().getDoubleExtra("lat", 0);
            lng = getIntent().getDoubleExtra("lng", 0);
        }

        // Render
        txtName.setText(nonNull(name));
        txtCategory.setText(nonNull(category));
        txtAddress.setText(nonNull(address));
        txtPhone.setText(nonNull(phone));
        txtDesc.setText(nonNull(description));
        if (imageUrl != null && !imageUrl.isEmpty()) Glide.with(this).load(imageUrl).into(img);

        // Acciones
        btnCall.setOnClickListener(v -> {
            if (phone != null && !phone.trim().isEmpty()) {
                // ACTION_DIAL no requiere permiso
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
            }
        });

        btnNavigate.setOnClickListener(v -> {
            if (lat != 0 && lng != 0) {
                String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + Uri.encode(name) + ")";
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps"); // opcional
                startActivity(mapIntent);
            } else if (address != null && !address.isEmpty()) {
                String uri = "geo:0,0?q=" + Uri.encode(address);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });
    }

    private String nonNull(String s) { return s == null ? "" : s; }
}
