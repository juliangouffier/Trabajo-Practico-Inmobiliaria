package com.example.trabajopracticoinmobiliaria.ui.perfil;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public final class PerfilViewModel extends ViewModel {

    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public PerfilViewModel(@NonNull String placeholderMessage) {
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
            if (modelClass.isAssignableFrom(PerfilViewModel.class)) {
                return (T) new PerfilViewModel(placeholderMessage);
            }
            throw new IllegalArgumentException("Error ViewModel: " + modelClass);
        }
    }
}
