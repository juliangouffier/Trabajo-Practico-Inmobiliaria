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

import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.modelo.Propietario;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel vm;
    private boolean modoEdicion = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(PerfilViewModel.class);

        vm.getPropietarioMutable().observe(getViewLifecycleOwner(), propietario -> {
            binding.etCodigo.setText(propietario.getIdPropietario());
            binding.etNombre.setText(propietario.getNombre());
            binding.etApellido.setText(propietario.getApellido());
            binding.etDni.setText(propietario.getDni());
            binding.etEmail.setText(propietario.getEmail());
            binding.etTelefono.setText(propietario.getTelefono());
        });

        vm.getActualizacionExitosa().observe(getViewLifecycleOwner(), exito -> {
            if (Boolean.TRUE.equals(exito)) {
                Toast.makeText(requireContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                setModoEdicion(false);
            }
        });

        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnEditar.setOnClickListener(v -> {
            if (!modoEdicion) {
                setModoEdicion(true);
            } else {
                guardar();
            }
        });

        binding.btnCambiarContrasena.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_perfil_to_cambiarContrasena));

        vm.cargarPerfil();
        return binding.getRoot();
    }

    private void setModoEdicion(boolean editar) {
        modoEdicion = editar;
        // el código/id nunca es editable
        binding.etDni.setEnabled(editar);
        binding.etNombre.setEnabled(editar);
        binding.etApellido.setEnabled(editar);
        binding.etEmail.setEnabled(editar);
        binding.etTelefono.setEnabled(editar);
        binding.btnEditar.setText(editar ? "GUARDAR" : "EDITAR");
    }

    private void guardar() {
        Propietario p = new Propietario(
                getText(binding.etCodigo),
                getText(binding.etNombre),
                getText(binding.etApellido),
                getText(binding.etDni),
                getText(binding.etTelefono),
                getText(binding.etEmail));
        vm.actualizarPerfil(p);
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
