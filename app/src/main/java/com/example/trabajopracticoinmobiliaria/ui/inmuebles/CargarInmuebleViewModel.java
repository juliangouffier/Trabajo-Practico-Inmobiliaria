package com.example.trabajopracticoinmobiliaria.ui.inmuebles;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CargarInmuebleViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> cargaExitosa = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public CargarInmuebleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<Boolean> getCargaExitosa() {
        return cargaExitosa;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void cargarInmueble(@NonNull Inmueble inmueble, @Nullable Uri imagenUri) {
        String errorValidacion = validar(inmueble);
        if (errorValidacion != null) {
            mensajeError.setValue(errorValidacion);
            return;
        }

        cargando.setValue(true);
        String token = ApiClient.bearerToken(getApplication());
        String json = inmueble.toJsonCarga().toString();
        RequestBody inmuebleJson = RequestBody.create(json, MediaType.parse("application/json"));
        MultipartBody.Part imagenPart;
        try {
            imagenPart = crearParteImagen(imagenUri);
        } catch (IOException e) {
            cargando.setValue(false);
            mensajeError.setValue("No se pudo preparar la imagen: " + e.getMessage());
            return;
        }

        ApiClient.getServicio().cargarInmueble(token, imagenPart, inmuebleJson)
                .enqueue(new Callback<Inmueble>() {
                    @Override
                    public void onResponse(@NonNull Call<Inmueble> call, @NonNull Response<Inmueble> response) {
                        cargando.postValue(false);
                        if (response.isSuccessful()) {
                            cargaExitosa.postValue(true);
                        } else {
                            String msg = "Error al cargar inmueble";
                            if (response.errorBody() != null) {
                                try {
                                    String body = response.errorBody().string();
                                    if (body != null && !body.isEmpty()) {
                                        msg = body;
                                    }
                                } catch (IOException ignored) {
                                }
                            }
                            mensajeError.postValue(msg);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Inmueble> call, @NonNull Throwable t) {
                        cargando.postValue(false);
                        mensajeError.postValue("Error de conexión: " + t.getMessage());
                    }
                });
    }

    @Nullable
    private String validar(@NonNull Inmueble inmueble) {
        if (inmueble.getDireccion() == null || inmueble.getDireccion().trim().isEmpty()) {
            return "Ingrese la dirección";
        }
        if (inmueble.getUso() == null || inmueble.getUso().trim().isEmpty()) {
            return "Ingrese el uso";
        }
        if (inmueble.getTipo() == null || inmueble.getTipo().trim().isEmpty()) {
            return "Ingrese el tipo";
        }
        if (inmueble.getAmbientes() <= 0) {
            return "Los ambientes deben ser mayor a 0";
        }
        if (inmueble.getSuperficie() <= 0) {
            return "La superficie debe ser mayor a 0";
        }
        if (inmueble.getValor() <= 0) {
            return "El valor debe ser mayor a 0";
        }
        return null;
    }

    @NonNull
    private MultipartBody.Part crearParteImagen(@Nullable Uri imagenUri) throws IOException {
        if (imagenUri != null) {
            RequestBody requestBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse("image/*");
                }

                @Override
                public void writeTo(@NonNull BufferedSink sink) throws IOException {
                    try (InputStream inputStream = getApplication().getContentResolver().openInputStream(imagenUri)) {
                        if (inputStream == null) {
                            throw new IOException("No se pudo leer la imagen seleccionada");
                        }
                        byte[] buffer = new byte[8192];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            sink.write(buffer, 0, read);
                        }
                    }
                }
            };
            return MultipartBody.Part.createFormData("imagen", "inmueble.jpg", requestBody);
        }
        return crearImagenPorDefecto();
    }

    @NonNull
    private MultipartBody.Part crearImagenPorDefecto() {
        Drawable drawable = ContextCompat.getDrawable(getApplication(), R.drawable.ic_inmueble_placeholder);
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (drawable != null) {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        RequestBody requestBody = RequestBody.create(stream.toByteArray(), MediaType.parse("image/png"));
        return MultipartBody.Part.createFormData("imagen", "inmueble_default.png", requestBody);
    }
}
