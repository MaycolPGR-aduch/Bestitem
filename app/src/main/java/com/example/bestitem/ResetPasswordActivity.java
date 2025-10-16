package com.example.bestitem;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private Button btnSend;
    private ProgressBar pb;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        tilEmail = findViewById(R.id.tilEmail);
        etEmail  = findViewById(R.id.etEmail);
        btnSend  = findViewById(R.id.btnSend);
        pb       = findViewById(R.id.pb);

        btnSend.setOnClickListener(v -> sendReset());
    }

    private void sendReset() {
        tilEmail.setError(null);
        String email = String.valueOf(etEmail.getText()).trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo inválido");
            return;
        }

        setLoading(true);
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Correo enviado")
                                .setMessage("Revisa tu bandeja de entrada o spam para restablecer tu contraseña.")
                                .setCancelable(false)
                                .setPositiveButton("Volver al login", (d, w) -> finish())
                                .show();
                    } else {
                        Toast.makeText(this, "No se pudo enviar el enlace", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setLoading(boolean loading) {
        btnSend.setEnabled(!loading);
        pb.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
