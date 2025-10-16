package com.example.bestitem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextInputLayout tilEmail, tilPass;
    private EditText etEmail, etPass;
    private Button btnLogin;
    private ProgressBar pb;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        tilEmail = findViewById(R.id.tilEmail);
        tilPass  = findViewById(R.id.tilPass);
        etEmail  = findViewById(R.id.etEmail);
        etPass   = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        pb       = findViewById(R.id.pb);

        btnLogin.setOnClickListener(v -> doLogin());
        findViewById(R.id.tvRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        findViewById(R.id.tvForgot).setOnClickListener(v ->
                startActivity(new Intent(this, ResetPasswordActivity.class)));
    }

    @Override protected void onStart() {
        super.onStart();
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) goHome();
    }

    private void doLogin() {
        tilEmail.setError(null); tilPass.setError(null);
        String email = etEmail.getText().toString().trim();
        String pass  = etPass.getText().toString().trim();

        boolean ok = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            tilEmail.setError("Correo inválido");
            ok = false;
        }
        if (pass.length() < 6){
            tilPass.setError("Mínimo 6 caracteres");
            ok = false;
        }
        if (!ok) return;

        setLoading(true);
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            setLoading(false);
            if (task.isSuccessful()) {
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle("¡Bienvenida!")
                        .setMessage("Has iniciado sesión correctamente.")
                        .setCancelable(false)
                        .setPositiveButton("Continuar", (d, w) -> goHome())
                        .show();
            } else {
                Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setLoading(boolean loading){
        btnLogin.setEnabled(!loading);
        pb.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void goHome(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
