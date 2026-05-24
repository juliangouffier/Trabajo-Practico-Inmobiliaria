package com.example.trabajopracticoinmobiliaria.ui.inmuebles;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentCargarInmuebleBinding;

import coil.Coil;
import coil.request.ImageRequest;

public class CargarInmuebleFragment extends Fragment {

    private FragmentCargarInmuebleBinding binding;
    private CargarInmuebleViewModel vm;
    private Uri imagenUri;

    private final ActivityResultLauncher<String> selectorImagen = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imagenUri = uri;
                    cargarPreview(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCargarInmuebleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(CargarInmuebleViewModel.class);

        configurarDropdowns();

        binding.btnSeleccionarImagen.setOnClickListener(v -> selectorImagen.launch("image/*"));
        binding.btnGuardar.setOnClickListener(v -> guardarInmueble());

        vm.getCargando().observe(getViewLifecycleOwner(), cargando -> {
            boolean enCarga = Boolean.TRUE.equals(cargando);
            binding.progressBar.setVisibility(enCarga ? View.VISIBLE : View.GONE);
            binding.btnGuardar.setEnabled(!enCarga);
        });

        vm.getCargaExitosa().observe(getViewLifecycleOwner(), exito -> {
            if (Boolean.TRUE.equals(exito)) {
                Toast.makeText(requireContext(), R.string.inmueble_cargado_ok, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });

        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarDropdowns() {
        String[] usos = getResources().getStringArray(R.array.opciones_uso_inmueble);
        String[] tipos = getResources().getStringArray(R.array.opciones_tipo_inmueble);

        binding.etUso.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, usos));
        binding.etTipo.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, tipos));
    }

    private void cargarPreview(@NonNull Uri uri) {
        ImageRequest request = new ImageRequest.Builder(requireContext())
                .data(uri)
                .placeholder(R.drawable.ic_inmueble_placeholder)
                .error(R.drawable.ic_inmueble_placeholder)
                .target(binding.imgPreview)
                .build();
        Coil.imageLoader(requireContext()).enqueue(request);
    }

    private void guardarInmueble() {
        try {
            String direccion = texto(binding.etDireccion);
            String uso = texto(binding.etUso);
            String tipo = texto(binding.etTipo);
            int ambientes = Integer.parseInt(texto(binding.etAmbientes));
            int superficie = Integer.parseInt(texto(binding.etSuperficie));
            double valor = Double.parseDouble(texto(binding.etValor));
            double latitud = parseDoubleOrDefault(texto(binding.etLatitud), 0.0);
            double longitud = parseDoubleOrDefault(texto(binding.etLongitud), 0.0);
            boolean disponible = binding.switchDisponible.isChecked();

            Inmueble inmueble = Inmueble.crearParaCarga(
                    direccion, uso, tipo, ambientes, superficie, latitud, longitud, valor, disponible);
            vm.cargarInmueble(inmueble, imagenUri);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), R.string.error_campos_numericos, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private String texto(@NonNull android.widget.EditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private double parseDoubleOrDefault(@NonNull String value, double defaultValue) {
        if (value.isEmpty()) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
