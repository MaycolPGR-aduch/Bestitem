// Paquete correcto
package com.example.bestitem;

// 1. Importaciones necesarias que faltaban
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

// Importaciones que ya tenías
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Test {

    // Define una etiqueta constante para los logs, es una buena práctica
    private static final String TAG = "FirestoreTest";

    // 2. Mueve todo el código ejecutable dentro de un método
    // Este método puede ser llamado desde otras partes de tu app
    public void ejecutarPruebaDeConexion() {

        // Obtén la instancia de Firestore dentro del método
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crea un mapa con datos de prueba
        Map<String, Object> test = new HashMap<>();
        test.put("mensaje", "¡Conexión exitosa!");
        test.put("timestamp", System.currentTimeMillis());

        // Añade el documento a una colección de prueba llamada "pruebas"
        db.collection("pruebas")
                .add(test)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Usa la etiqueta TAG para consistencia
                        Log.d(TAG, "Documento añadido con ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Usa la etiqueta TAG
                        Log.w(TAG, "Error al añadir documento", e);
                    }
                });
    }
}

