package com.example.trabajopracticoinmobiliaria.ui.inquilinos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inquilino;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentDetalleInquilinoBinding;

public class DetalleInquilinoFragment extends Fragment {

    private FragmentDetalleInquilinoBinding binding;
    private DetalleInquilinoViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleInquilinoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(DetalleInquilinoViewModel.class);

        Inmueble inmueble = obtenerInmuebleDesdeArgs();
        if (inmueble == null) {
            Toast.makeText(requireContext(), R.string.error_inquilino_no_encontrado, Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }
        vm.setInmueble(inmueble);
        binding.tvDireccionInmueble.setText(inmueble.getDireccion());

        vm.getInquilino().observe(getViewLifecycleOwner(), this::mostrarInquilino);
        vm.getCargando().observe(getViewLifecycleOwner(), cargando ->
                binding.progressBar.setVisibility(Boolean.TRUE.equals(cargando) ? View.VISIBLE : View.GONE));
        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        vm.cargarInquilino();
    }

    @Nullable
    private Inmueble obtenerInmuebleDesdeArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return null;
        }
        Object valor = args.getSerializable(DetalleInquilinoViewModel.ARG_INMUEBLE);
        if (valor instanceof Inmueble) {
            return (Inmueble) valor;
        }
        return null;
    }

    private void mostrarInquilino(@Nullable Inquilino inquilino) {
        if (inquilino == null) {
            return;
        }
        binding.cardInquilino.setVisibility(View.VISIBLE);
        binding.tvInquilinoNombre.setText(getString(R.string.inquilino_nombre,
                inquilino.getNombre() != null ? inquilino.getNombre() : "",
                inquilino.getApellido() != null ? inquilino.getApellido() : ""));
        binding.tvInquilinoDni.setText(getString(R.string.inquilino_dni,
                inquilino.getDni() != null ? inquilino.getDni() : "-"));
        binding.tvInquilinoTelefono.setText(getString(R.string.inquilino_telefono,
                inquilino.getTelefono() != null ? inquilino.getTelefono() : "-"));
        binding.tvInquilinoEmail.setText(getString(R.string.inquilino_email,
                inquilino.getEmail() != null ? inquilino.getEmail() : "-"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
