package com.example.trabajopracticoinmobiliaria.ui.contratos;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public final class ContratosViewModel extends ViewModel {

    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public ContratosViewModel(@NonNull String placeholderMessage) {
        mensaje.setValue(placeholderMessage);
    }

    @NonNull
    public LiveData<String> obtenerMensaje() {
        return mensaje;
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final String placeholderMessage;

        public Factory(@NonNull String placeholderMessage) {
            this.placeholderMessage = placeholderMessage;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ContratosViewModel.class)) {
                return (T) new ContratosViewModel(placeholderMessage);
            }
            throw new IllegalArgumentException("Error viewmodel: " + modelClass);
        }
    }
}
