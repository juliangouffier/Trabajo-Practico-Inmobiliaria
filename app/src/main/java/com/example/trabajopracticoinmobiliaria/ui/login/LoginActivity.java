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

import com.example.trabajopracticoinmobiliaria.ContenedorAplicacion;
import com.example.trabajopracticoinmobiliaria.MainActivity;
import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding enlace;
    private SensorManager sensorManager;
    private ShakeToDialListener shakeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContenedorAplicacion aplicacion = (ContenedorAplicacion) getApplication();
        if (aplicacion.obtenerAlmacenToken().haySesion()) {
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

        LoginViewModel modelo = new ViewModelProvider(
                this,
                new LoginViewModelFactory(getApplication())
        ).get(LoginViewModel.class);

        enlace.botonEntrar.setOnClickListener(v -> {
            CharSequence usuario = enlace.campoUsuario.getText();
            CharSequence clave = enlace.campoClave.getText();
            modelo.iniciarSesion(
                    usuario != null ? usuario.toString() : "",
                    clave != null ? clave.toString() : "");
        });

        modelo.obtenerCargando().observe(this, cargando -> {
            boolean visible = Boolean.TRUE.equals(cargando);
            enlace.indicadorCarga.setVisibility(visible ? View.VISIBLE : View.GONE);
            enlace.botonEntrar.setEnabled(!visible);
        });

        modelo.obtenerMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        modelo.obtenerLoginExitoso().observe(this, exito -> {
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
