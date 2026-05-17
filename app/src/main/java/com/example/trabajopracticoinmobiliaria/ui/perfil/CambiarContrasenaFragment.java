package com.example.trabajopracticoinmobiliaria.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.trabajopracticoinmobiliaria.databinding.FragmentCambiarContrasenaBinding;

public class CambiarContrasenaFragment extends Fragment {

    private FragmentCambiarContrasenaBinding binding;
    private CambiarContrasenaViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCambiarContrasenaBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(CambiarContrasenaViewModel.class);

        vm.getCargando().observe(getViewLifecycleOwner(), cargando -> {
            boolean visible = Boolean.TRUE.equals(cargando);
            binding.indicadorCarga.setVisibility(visible ? View.VISIBLE : View.GONE);
            binding.btnGuardar.setEnabled(!visible);
        });

        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        vm.getCambioExitoso().observe(getViewLifecycleOwner(), exito -> {
            if (Boolean.TRUE.equals(exito)) {
                Toast.makeText(requireContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });

        binding.btnGuardar.setOnClickListener(v -> {
            String claveActual = getText(binding.etClaveActual);
            String claveNueva = getText(binding.etClaveNueva);
            vm.cambiarContrasena(claveActual, claveNueva);
        });

        return binding.getRoot();
    }

    private String getText(com.google.android.material.textfield.TextInputEditText field) {
        CharSequence text = field.getText();
        return text != null ? text.toString() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
