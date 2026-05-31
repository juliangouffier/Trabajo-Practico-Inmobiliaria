package com.example.trabajopracticoinmobiliaria.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.trabajopracticoinmobiliaria.databinding.ActivityRecuperarClaveBinding;

public class RecuperarClaveActivity extends AppCompatActivity {

    private ActivityRecuperarClaveBinding binding;
    private RecuperarClaveViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarClaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        vm = new ViewModelProvider(this).get(RecuperarClaveViewModel.class);

        binding.btnResetear.setOnClickListener(v -> vm.resetearCredenciales());
        binding.btnVolver.setOnClickListener(v -> finish());

        vm.getCargando().observe(this, cargando -> {
            boolean enCarga = Boolean.TRUE.equals(cargando);
            binding.progressBar.setVisibility(enCarga ? View.VISIBLE : View.GONE);
            binding.btnResetear.setEnabled(!enCarga);
        });

        vm.getMensajeExito().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        vm.getMensajeError().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
