package com.example.trabajopracticoinmobiliaria.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);

    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loginExitoso = new MutableLiveData<>();
    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();

    public LoginViewModel(@NonNull Application aplicacion) {
        super(aplicacion);
    }

    public void iniciarSesion(@NonNull String usuario, @NonNull String clave) {
        String u = usuario.trim();
        if (u.isEmpty()) {
            mensajeError.setValue("Ingrese el usuario o correo");
            return;
        }
        if (clave.isEmpty()) {
            mensajeError.setValue("Ingrese la clave");
            return;
        }

        cargando.setValue(true);
        ejecutor.execute(() -> ejecutarLogin(u, clave));
    }

    private void ejecutarLogin(@NonNull String usuario, @NonNull String clave) {
        try {
            Response<String> respuesta = ApiClient.getServicio().login(usuario, clave).execute();
            if (respuesta.isSuccessful()) {
                String cuerpo = respuesta.body();
                if (cuerpo == null || cuerpo.isEmpty()) {
                    cargando.postValue(false);
                    mensajeError.postValue("Respuesta vacía del servidor");
                    return;
                }
                String token = cuerpo.trim();
                if (token.length() >= 2 && token.startsWith("\"") && token.endsWith("\"")) {
                    token = token.substring(1, token.length() - 1).trim();
                }
                ApiClient.crearToken(getApplication(), token);
                cargando.postValue(false);
                loginExitoso.postValue(true);
            } else {
                cargando.postValue(false);
                mensajeError.postValue(leerCuerpoError(respuesta));
            }
        } catch (IOException e) {
            cargando.postValue(false);
            String mensaje = e.getMessage() != null ? e.getMessage() : "Error de conexión";
            mensajeError.postValue(mensaje);
        }
    }

    @NonNull
    private static String leerCuerpoError(@NonNull Response<?> respuesta) {
        ResponseBody cuerpoError = respuesta.errorBody();
        if (cuerpoError != null) {
            try {
                String texto = cuerpoError.string();
                if (texto != null && !texto.isEmpty()) {
                    return texto;
                }
            } catch (IOException ignored) {
            }
        }
        return "Credenciales inválidas o error del servidor";
    }

    @NonNull
    public LiveData<Boolean> obtenerCargando() {
        return cargando;
    }

    @NonNull
    public MutableLiveData<String> obtenerMensajeError() {
        return mensajeError;
    }

    @NonNull
    public MutableLiveData<Boolean> obtenerLoginExitoso() {
        return loginExitoso;
    }
}
