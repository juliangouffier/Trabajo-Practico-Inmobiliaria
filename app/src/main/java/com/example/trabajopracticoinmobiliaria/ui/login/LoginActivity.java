package com.example.trabajopracticoinmobiliaria.ui.login;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.trabajopracticoinmobiliaria.MainActivity;
import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;
import com.example.trabajopracticoinmobiliaria.databinding.ActivityLoginBinding;

import static android.content.Context.SENSOR_SERVICE;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding enlace;
    private SensorManager sensorManager;
    private ShakeToDialListener shakeListener;

    private LoginViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ApiClient.haySesion(this)) {
            irAPrincipalYFinalizar();
            return;
        }

        enlace = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(enlace.getRoot());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager != null
                ? sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                : null;
        if (accelerometer != null) {
            shakeListener = new ShakeToDialListener(this::openAgencyDialer);
        }

        vm = new ViewModelProvider(this).get(LoginViewModel.class);

        enlace.botonEntrar.setOnClickListener(v -> {
            CharSequence usuario = enlace.campoUsuario.getText();
            CharSequence clave = enlace.campoClave.getText();
            vm.iniciarSesion(
                    usuario != null ? usuario.toString() : "",
                    clave != null ? clave.toString() : "");
        });

        enlace.btnOlvideClave.setOnClickListener(v ->
                startActivity(new Intent(this, RecuperarClaveActivity.class)));

        vm.obtenerCargando().observe(this, cargando -> {
            boolean visible = Boolean.TRUE.equals(cargando);
            enlace.indicadorCarga.setVisibility(visible ? View.VISIBLE : View.GONE);
            enlace.botonEntrar.setEnabled(!visible);
        });

        vm.obtenerMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        vm.obtenerLoginExitoso().observe(this, exito -> {
            if (Boolean.TRUE.equals(exito)) {
                irAPrincipalYFinalizar();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && shakeListener != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(
                        shakeListener,
                        accelerometer,
                        SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null && shakeListener != null) {
            sensorManager.unregisterListener(shakeListener);
        }
    }

    private void openAgencyDialer() {
        String number = getString(R.string.agency_phone_number);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void irAPrincipalYFinalizar() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
