package com.example.bestitem.ui.map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bestitem.R;
import com.example.bestitem.data.model.Business;
import com.example.bestitem.ui.businessdetail.BusinessDetailActivity;
import com.example.bestitem.vm.BusinessViewModel;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// imports nuevos:
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import android.util.TypedValue;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class BusinessMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BusinessViewModel vm;
    // guarda referencia del Business por MarkerId
    private final Map<String, Business> markerBusiness = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_map);

        vm = new ViewModelProvider(this).get(BusinessViewModel.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // 🔹 Centrar el mapa en Lima Metropolitana
        LatLng lima = new LatLng(-12.0464, -77.0428);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f));

        mMap.setOnMarkerClickListener(marker -> {
            Business b = markerBusiness.get(marker.getId());
            if (b != null) openDetail(b);
            return true; // consume el evento
        });

        // Al tocar el marker, abrimos la pantalla de detalle
        mMap.setOnInfoWindowClickListener(marker -> {
            Business b = markerBusiness.get(marker.getId());
            if (b != null) openDetail(b);
        });

        // Observa lista de negocios y dibuja
        vm.getBusinesses().observe(this, this::renderMarkers);
    }

    private void renderMarkers(List<Business> list) {
        if (mMap == null) return;
        mMap.clear();
        markerBusiness.clear();

        LatLng first = null;
        for (Business b : list) {
            if (b.getLat() == null || b.getLng() == null) continue;
            LatLng pos = new LatLng(b.getLat(), b.getLng());
            if (first == null) first = pos;

            if (b.getImageUrl() != null && !b.getImageUrl().isEmpty()) {
                addMarkerWithImage(b, pos);
            } else {
                // marcador simple si no hay imagen
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(b.getName())
                        .snippet(b.getAddress()));
                if (m != null) markerBusiness.put(m.getId(), b);
            }
        }

        // centra
        if (first != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(first, 14f));
        } else {
            LatLng lima = new LatLng(-12.0464, -77.0428);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f));
        }
        /*
        LatLng first = null;
        for (Business b : list) {
            if (b.getLat() == null || b.getLng() == null) continue;
            LatLng pos = new LatLng(b.getLat(), b.getLng());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(b.getName())
                    .snippet(b.getAddress() != null ? b.getAddress() : "Ver detalles"));
            if (marker != null) {
                markerBusiness.put(marker.getId(), b);
                if (first == null) first = pos;
            }
        }
        if (first != null) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 14f));*/
    }

    /** Descarga la imagen, la convierte a círculo y la usa como icono del marker */
    private void addMarkerWithImage(Business b, LatLng pos) {
        int sizePx = dp(48);     // diámetro del marcador
        int borderPx = dp(2);    // borde blanco

        Glide.with(this)
                .asBitmap()
                .load(b.getImageUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .centerCrop())
                .into(new CustomTarget<Bitmap>(sizePx, sizePx) {
                    @Override
                    public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap circle = makeCircularBitmap(resource, sizePx, borderPx);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(b.getName())
                                .snippet(b.getAddress())
                                .icon(BitmapDescriptorFactory.fromBitmap(circle)));
                        if (marker != null) markerBusiness.put(marker.getId(), b);
                    }
                    @Override public void onLoadCleared(@Nullable Drawable placeholder) { /* no-op */ }
                    @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        // Fallback: marcador por defecto si falló la imagen
                        Marker m = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(b.getName())
                                .snippet(b.getAddress()));
                        if (m != null) markerBusiness.put(m.getId(), b);
                    }
                });
    }

    /** Convierte un bitmap en círculo con borde (y tamaño fijo) */
    private Bitmap makeCircularBitmap(Bitmap src, int size, int borderPx) {
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // recorte circular
        Path path = new Path();
        RectF rect = new RectF(0, 0, size, size);
        path.addOval(rect, Path.Direction.CW);
        canvas.clipPath(path);

        // dibuja la imagen escalada al tamaño
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(Bitmap.createScaledBitmap(src, size, size, true), 0, 0, paint);

        // borde
        if (borderPx > 0) {
            Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
            border.setStyle(Paint.Style.STROKE);
            border.setStrokeWidth(borderPx);
            border.setColor(0xFFFFFFFF); // blanco
            float r = (size / 2f) - (borderPx / 2f);
            canvas.drawCircle(size / 2f, size / 2f, r, border);
        }
        return output;
    }

    /** dp → px */
    private int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void openDetail(Business b) {
        Intent i = new Intent(this, BusinessDetailActivity.class);
        i.putExtra("id", b.getId());
        i.putExtra("name", b.getName());
        i.putExtra("category", b.getCategory());
        i.putExtra("address", b.getAddress());
        i.putExtra("phone", b.getPhone());
        i.putExtra("lat", b.getLat() != null ? b.getLat() : 0);
        i.putExtra("lng", b.getLng() != null ? b.getLng() : 0);
        i.putExtra("description", b.getDescription());
        i.putExtra("imageUrl", b.getImageUrl());
        startActivity(i);
    }
}
