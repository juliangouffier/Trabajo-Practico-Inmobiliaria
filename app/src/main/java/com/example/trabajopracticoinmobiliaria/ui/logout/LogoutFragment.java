package com.example.trabajopracticoinmobiliaria.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.trabajopracticoinmobiliaria.ContenedorAplicacion;
import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentLogoutBinding;
import com.example.trabajopracticoinmobiliaria.ui.login.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LogoutFragment extends Fragment {

    private FragmentLogoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContenedorAplicacion aplicacion = (ContenedorAplicacion) requireActivity().getApplication();
        LogoutViewModel modelo = new ViewModelProvider(
                this,
                new LogoutViewModel.Factory(aplicacion.obtenerAlmacenToken()))
                .get(LogoutViewModel.class);

        if (savedInstanceState == null) {
            mostrarDialogoConfirmacion(modelo);
        }
    }

    private void mostrarDialogoConfirmacion(@NonNull LogoutViewModel modelo) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setPositiveButton(R.string.logout_confirm_yes, (dialog, which) -> {
                    modelo.limpiarSesion();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton(R.string.logout_confirm_cancel, (dialog, which) -> volverAtras())
                .setOnCancelListener(dialog -> volverAtras())
                .show();
    }

    private void volverAtras() {
        if (!isAdded()) {
            return;
        }
        View root = binding != null ? binding.getRoot() : getView();
        if (root != null) {
            Navigation.findNavController(root).popBackStack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
