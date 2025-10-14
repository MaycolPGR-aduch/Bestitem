package com.example.bestitem;

import android.os.Bundle;
import android.util.Log; // Importa la clase Log
import android.view.View;
import android.widget.Button; // Importa Button
import android.widget.Toast; // Importa Toast

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importaciones de Firebase Firestore
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Etiqueta para los logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Asegúrate de tener un layout

        // Ejemplo: Añadir un botón a tu activity_main.xml con el id "myButton"
        // <Button
        //     android:id="@+id/myButton"
        //     android:layout_width="wrap_content"
        //     android:layout_height="wrap_content"
        //     android:text="Guardar en Firestore" />

        Button myButton = findViewById(R.id.myButton); // Asegúrate de tener este botón en tu XML

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatosDeEjemplo();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void guardarDatosDeEjemplo() {
        // 1. Obtener una instancia de la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //FirebaseFirestore db = FirebaseFirestore.getInstance(FirebaseApp.getInstance(), "bdbest");

        // 2. Crear un nuevo "item" con un mapa de datos
        Map<String, Object> item = new HashMap<>();
        item.put("nombre", "Laptop Gamer XYZ");
        item.put("precio", 1500);
        item.put("disponible", true);

        // 3. Añadir un nuevo documento con un ID generado automáticamente a la colección "items"
        db.collection("items")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Se ejecutó correctamente
                        Log.d(TAG, "Documento añadido con ID: " + documentReference.getId());
                        Toast.makeText(MainActivity.this, "¡Item guardado en Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error
                        Log.w(TAG, "Error al añadir el documento", e);
                        Toast.makeText(MainActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
        Log.d("MainActivity", "Firebase projectId=" + opts.getProjectId()
                + ", applicationId=" + opts.getApplicationId()
                + ", gcmSenderId=" + opts.getGcmSenderId());
    }
}
