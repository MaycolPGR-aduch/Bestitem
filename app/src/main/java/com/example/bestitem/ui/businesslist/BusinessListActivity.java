package com.example.bestitem.ui.businesslist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bestitem.R;
import com.example.bestitem.data.model.Business;
import com.example.bestitem.data.repo.BusinessRepository;
import com.example.bestitem.ui.businessform.BusinessFormActivity;
import com.example.bestitem.vm.BusinessViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BusinessListActivity extends AppCompatActivity implements BusinessAdapter.OnItemClick {
    private BusinessViewModel vm;
    private BusinessAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // muestra la flecha
            getSupportActionBar().setTitle("Nuevo negocio");        // opcional
        }

        vm = new ViewModelProvider(this).get(BusinessViewModel.class);

        RecyclerView rv = findViewById(R.id.rvBusinesses);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BusinessAdapter(this);
        rv.setAdapter(adapter);

        vm.getBusinesses().observe(this, adapter::submit);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, BusinessFormActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // vuelve a la anterior (MainActivity o la que abrió esta)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEdit(Business b) {
        Intent i = new Intent(this, BusinessFormActivity.class);
        i.putExtra("id", b.getId());
        i.putExtra("name", b.getName());
        i.putExtra("category", b.getCategory());
        i.putExtra("address", b.getAddress());
        i.putExtra("phone", b.getPhone());
        i.putExtra("lat", b.getLat());
        i.putExtra("lng", b.getLng());
        i.putExtra("description", b.getDescription());
        // --- NUEVO: Añadir la URL de la imagen al Intent ---
        // Esto es crucial para que el formulario pueda mostrar la imagen actual.
        i.putExtra("imageUrl", b.getImageUrl());
        startActivity(i);
    }

    @Override
    public void onDelete(Business b) {
        vm.deleteWithImage(b.getId(), new BusinessRepository.OnComplete() {
            @Override public void ok(String id) { Toast.makeText(BusinessListActivity.this, "Eliminado", Toast.LENGTH_SHORT).show(); }
            @Override public void err(Exception e) { Toast.makeText(BusinessListActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show(); }
        });
    }

    /*
    public void onDelete(Business b) {
        vm.delete(b.getId(), new com.example.bestitem.data.repo.BusinessRepository.OnComplete() {
            @Override public void ok(String id) { Toast.makeText(BusinessListActivity.this, "Eliminado", Toast.LENGTH_SHORT).show(); }
            @Override public void err(Exception e) { Toast.makeText(BusinessListActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show(); }
        });
    }*/

}
