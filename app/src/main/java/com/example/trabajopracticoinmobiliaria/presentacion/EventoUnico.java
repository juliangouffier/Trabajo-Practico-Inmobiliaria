package com.example.trabajopracticoinmobiliaria.presentacion;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;
public class EventoUnico<T> extends MutableLiveData<T> {

    private final AtomicBoolean pendiente = new AtomicBoolean(false);

    @MainThread
    @Override
    public void observe(@NonNull LifecycleOwner propietario, @NonNull Observer<? super T> observador) {
        super.observe(propietario, valor -> {
            if (pendiente.compareAndSet(true, false)) {
                observador.onChanged(valor);
            }
        });
    }

    @MainThread
    @Override
    public void setValue(@Nullable T valor) {
        pendiente.set(true);
        super.setValue(valor);
    }

    @Override
    public void postValue(@Nullable T valor) {
        pendiente.set(true);
        super.postValue(valor);
    }
}
