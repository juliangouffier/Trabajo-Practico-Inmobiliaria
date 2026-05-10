package com.example.trabajopracticoinmobiliaria;

import android.app.Application;

import com.example.trabajopracticoinmobiliaria.data.AlmacenToken;
import com.example.trabajopracticoinmobiliaria.data.AlmacenTokenPreferencias;
import com.example.trabajopracticoinmobiliaria.data.ConfiguracionApi;
import com.example.trabajopracticoinmobiliaria.data.remote.PropietariosApiService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ContenedorAplicacion extends Application {

    private AlmacenToken almacenToken;
    private PropietariosApiService propietariosApiService;
    private ExecutorService executorServicio;

    @Override
    public void onCreate() {
        super.onCreate();
        executorServicio = Executors.newFixedThreadPool(4);
        almacenToken = new AlmacenTokenPreferencias(this);
        OkHttpClient clienteHttp = crearClienteHttp();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConfiguracionApi.URL_BASE)
                .client(clienteHttp)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        propietariosApiService = retrofit.create(PropietariosApiService.class);
    }

    private OkHttpClient crearClienteHttp() {
        OkHttpClient.Builder constructor = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor registro = new HttpLoggingInterceptor();
            registro.setLevel(HttpLoggingInterceptor.Level.BASIC);
            constructor.addInterceptor(registro);
        }
        return constructor.build();
    }

    public AlmacenToken obtenerAlmacenToken() {
        return almacenToken;
    }

    public PropietariosApiService obtenerPropietariosApiService() {
        return propietariosApiService;
    }

    public ExecutorService obtenerExecutorServicio() {
        return executorServicio;
    }
}
