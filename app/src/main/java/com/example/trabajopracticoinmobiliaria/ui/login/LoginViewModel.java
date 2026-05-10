package com.example.trabajopracticoinmobiliaria.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.AlmacenToken;
import com.example.trabajopracticoinmobiliaria.data.remote.AccessToken;
import com.example.trabajopracticoinmobiliaria.data.remote.PropietariosApiService;
import com.example.trabajopracticoinmobiliaria.presentacion.EventoUnico;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    private final PropietariosApiService propietariosApiService;
    private final Executor ejecutor;
    private final AlmacenToken almacenToken;

    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final EventoUnico<String> mensajeError = new EventoUnico<>();
    private final EventoUnico<Boolean> loginExitoso = new EventoUnico<>();

    public LoginViewModel(
            @NonNull Application aplicacion,
            @NonNull PropietariosApiService propietariosApiService,
            @NonNull Executor ejecutor,
            @NonNull AlmacenToken almacenToken) {
        super(aplicacion);
        this.propietariosApiService = propietariosApiService;
        this.ejecutor = ejecutor;
        this.almacenToken = almacenToken;
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
            Response<String> respuesta = propietariosApiService.login(usuario, clave).execute();
            if (respuesta.isSuccessful()) {
                String cuerpo = respuesta.body();
                if (cuerpo == null || cuerpo.isEmpty()) {
                    cargando.postValue(false);
                    mensajeError.postValue("Respuesta vacía del servidor");
                    return;
                }
                AccessToken token = AccessToken.fromRawResponse(cuerpo);
                almacenToken.guardar(token.getValue());
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
    public EventoUnico<String> obtenerMensajeError() {
        return mensajeError;
    }

    @NonNull
    public EventoUnico<Boolean> obtenerLoginExitoso() {
        return loginExitoso;
    }
}
