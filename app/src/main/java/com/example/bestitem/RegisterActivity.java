package com.example.bestitem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputLayout tilName, tilEmail, tilPass, tilPass2;
    private TextInputEditText etName, etEmail, etPass, etPass2;
    private Button btnRegister;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        tilName  = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPass  = findViewById(R.id.tilPass);
        tilPass2 = findViewById(R.id.tilPass2);

        etName  = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPass  = findViewById(R.id.etPass);
        etPass2 = findViewById(R.id.etPass2);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        // limpia errores
        tilName.setError(null); tilEmail.setError(null);
        tilPass.setError(null); tilPass2.setError(null);

        String name  = String.valueOf(etName.getText()).trim();
        String email = String.valueOf(etEmail.getText()).trim();
        String p1    = String.valueOf(etPass.getText()).trim();
        String p2    = String.valueOf(etPass2.getText()).trim();

        boolean ok = true;
        if (name.isEmpty()) { tilName.setError("Requerido"); ok = false; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Correo inválido"); ok = false; }
        if (p1.length() < 6) { tilPass.setError("Mínimo 6 caracteres"); ok = false; }
        if (!p1.equals(p2)) { tilPass2.setError("No coincide"); ok = false; }
        if (!ok) return;

        setLoading(true);

        auth.createUserWithEmailAndPassword(email, p1).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                setLoading(false);
                Toast.makeText(this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser u = auth.getCurrentUser();
            if (u == null) {
                setLoading(false);
                Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualiza el nombre visible (si se ingresó)
            if (!name.isEmpty()) {
                u.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build()).addOnCompleteListener(t -> {
                    guardarPerfilEnFirestore(u, name, email);
                });
            } else {
                guardarPerfilEnFirestore(u, name, email);
            }
        });
    }

    private void guardarPerfilEnFirestore(FirebaseUser u, String name, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> profile = new HashMap<>();
        profile.put("uid", u.getUid());
        profile.put("name", name);
        profile.put("email", email);
        profile.put("role", "cliente");                 // 👈 rol por defecto
        profile.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(u.getUid()).set(profile)
                .addOnSuccessListener(v -> {
                    setLoading(false);
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("¡Cuenta creada!")
                            .setMessage("Bienvenida, " + name + ". Tu cuenta fue creada con éxito.")
                            .setCancelable(false)
                            .setPositiveButton("Ir al inicio", (d, w) -> {
                                startActivity(new Intent(this, HomeActivity.class));
                                finishAffinity();
                            })
                            .show();

                    
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "No se pudo guardar tu perfil", Toast.LENGTH_SHORT).show();
                });
    }



    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        findViewById(R.id.pb).setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}
