package com.example.bestitem.ui.businessform;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.bestitem.R;
import com.example.bestitem.data.model.Business;
import com.example.bestitem.data.repo.BusinessRepository;
import com.example.bestitem.vm.BusinessViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BusinessFormActivity extends AppCompatActivity {

    private BusinessViewModel vm;

    // UI
    private EditText etName, etCategory, etAddress, etPhone, etLat, etLng, etDesc;
    private ImageView imgPreview;
    private Button btnPickImage, btnSave;

    // Estado
    private String editingId;          // != null si estamos editando
    private String existingImageUrl;   // url ya guardada (si editas)
    private Uri selectedImageUri;      // imagen elegida para subir

    // Selector de imagen (galería) sin permisos peligrosos
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_form);

        vm = new ViewModelProvider(this).get(BusinessViewModel.class);

        // Referencias UI
        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etLat = findViewById(R.id.etLat);
        etLng = findViewById(R.id.etLng);
        etDesc = findViewById(R.id.etDescription);
        imgPreview = findViewById(R.id.imgPreview);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSave = findViewById(R.id.btnSave);

        // ¿Modo edición?
        if (getIntent() != null && getIntent().hasExtra("id")) {
            editingId = getIntent().getStringExtra("id");
            etName.setText(getIntent().getStringExtra("name"));
            etCategory.setText(getIntent().getStringExtra("category"));
            etAddress.setText(getIntent().getStringExtra("address"));
            etPhone.setText(getIntent().getStringExtra("phone"));
            if (getIntent().hasExtra("lat")) {
                double lat = getIntent().getDoubleExtra("lat", 0);
                if (lat != 0) etLat.setText(String.valueOf(lat));
            }
            if (getIntent().hasExtra("lng")) {
                double lng = getIntent().getDoubleExtra("lng", 0);
                if (lng != 0) etLng.setText(String.valueOf(lng));
            }
            etDesc.setText(getIntent().getStringExtra("description"));

            existingImageUrl = getIntent().getStringExtra("imageUrl");
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                Glide.with(this).load(existingImageUrl).into(imgPreview);
            }
        }

        // Elegir imagen
        btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Guardar (crear/actualizar)
        btnSave.setOnClickListener(v -> saveBusiness());
    }

    private void saveBusiness() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Requerido");
            return;
        }

        Business b = new Business(
                name,
                etCategory.getText().toString().trim(),
                etAddress.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                parseDouble(etLat.getText().toString()),
                parseDouble(etLng.getText().toString()),
                etDesc.getText().toString().trim()
        );

        if (editingId == null) {
            // Crear documento con ID conocido para poder subir imagen a /businesses/{id}/main.jpg
            vm.createWithKnownId(b, new BusinessRepository.OnComplete() {
                @Override public void ok(String id) {
                    if (selectedImageUri != null) {
                        uploadBusinessImage(id, selectedImageUri, url -> {
                            b.setId(id);
                            b.setImageUrl(url);
                            vm.update(b, callback("Creado"));
                        });
                    } else {
                        toast("Creado");
                        finish();
                    }
                }
                @Override public void err(Exception e) { toast("Error: " + e.getMessage()); }
            });
        } else {
            // Actualizar. Si hay nueva imagen, se sube y se actualiza imageUrl; si no, mantiene la anterior.
            b.setId(editingId);
            if (selectedImageUri != null) {
                uploadBusinessImage(editingId, selectedImageUri, url -> {
                    b.setImageUrl(url);
                    vm.update(b, callback("Actualizado"));
                });
            } else {
                // conservar la url existente
                if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                    b.setImageUrl(existingImageUrl);
                }
                vm.update(b, callback("Actualizado"));
            }
        }
    }

    private BusinessRepository.OnComplete callback(String okMsg) {
        return new BusinessRepository.OnComplete() {
            @Override public void ok(String id) {
                toast(okMsg);
                finish();
            }
            @Override public void err(Exception e) { toast("Error: " + e.getMessage()); }
        };
    }

    private void uploadBusinessImage(String bizId, Uri uri, OnUrlReady onDone) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference()
                .child("business_images/" + bizId + ".jpg");
                //.child("businesses/" + bizId + "/main.jpg");

        ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    if (onDone != null) onDone.ready(downloadUri.toString());
                })
                .addOnFailureListener(e -> toast("Upload error: " + e.getMessage()));
    }

    private Double parseDouble(String s) {
        try { return s == null || s.trim().isEmpty() ? null : Double.parseDouble(s.trim()); }
        catch (Exception e) { return null; }
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // Callback simple para devolver la URL subida
    private interface OnUrlReady { void ready(String url); }
}

