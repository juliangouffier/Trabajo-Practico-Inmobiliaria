package com.example.trabajopracticoinmobiliaria.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.modelo.Propietario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public class ApiClient {

    public static final String URL_BASE = "https://capacitacion.alwaysdata.net";

    public static MiServicioInmobiliaria getServicio(){
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(MiServicioInmobiliaria.class);
    }

    public interface  MiServicioInmobiliaria {
        @FormUrlEncoded
        @POST("/api/Propietarios/login")
        Call<String> login(@Field("Usuario") String usuario, @Field("Clave") String clave);

        @GET("/api/Propietarios")
        Call<Propietario> getPropietario(@Header("Authorization")String token);

        @PUT("/api/Propietarios/actualizar")
        Call<Propietario> actualizarPropietario(@Header("Authorization")String token, @Body Propietario propietario);

        @FormUrlEncoded
        @PUT("/api/Propietarios/changePassword")
        Call<Void> cambiarContrasena(@Header("Authorization") String token,
                                     @Field("currentPassword") String claveActual,
                                     @Field("newPassword") String claveNueva);

        @GET("/api/inmuebles")
        Call<List<Inmueble>> getInmuebles(@Header("Authorization") String token);

        @PUT("/api/Inmuebles/actualizar")
        Call<Inmueble> actualizarInmueble(@Header("Authorization") String token, @Body Inmueble inmueble);

        @Multipart
        @POST("/api/Inmuebles/cargar")
        Call<Inmueble> cargarInmueble(@Header("Authorization") String token,
                                     @Nullable @Part MultipartBody.Part imagen,
                                     @Part("inmueble") RequestBody inmuebleJson);
    }

    public static void crearToken(Context context, String token){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        sp.edit().putString("token", token).apply();
    }

    public static String userToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        String t = sp.getString("token", null);
        return (t == null || t.isEmpty()) ? null : t;
    }

    public static String bearerToken(Context context) {
        String t = userToken(context);
        return t != null ? "Bearer " + t : null;
    }

    public static boolean haySesion(Context context) {
        return userToken(context) != null;
    }

    public static void borrarToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        sp.edit().remove("token").apply();
    }
}
