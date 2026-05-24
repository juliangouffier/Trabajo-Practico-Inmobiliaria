package com.example.trabajopracticoinmobiliaria.ui.inmuebles;

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
import com.example.trabajopracticoinmobiliaria.data.modelo.Propietario;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentDetalleInmuebleBinding;

import coil.Coil;
import coil.request.ImageRequest;

import java.text.NumberFormat;
import java.util.Locale;

public class DetalleInmuebleFragment extends Fragment {

    private FragmentDetalleInmuebleBinding binding;
    private DetalleInmuebleViewModel vm;
    private boolean bloquearSwitch = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleInmuebleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(DetalleInmuebleViewModel.class);

        Inmueble inmueble = obtenerInmuebleDesdeArgs();
        if (inmueble == null) {
            Toast.makeText(requireContext(), R.string.error_inmueble_no_encontrado, Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }
        vm.setInmueble(inmueble);

        vm.getInmueble().observe(getViewLifecycleOwner(), this::mostrarInmueble);
        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        binding.switchDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Inmueble actual = vm.getInmueble().getValue();
            if (!bloquearSwitch && actual != null && isChecked != actual.isDisponible()) {
                vm.cambiarDisponibilidad();
            }
        });
    }

    @Nullable
    private Inmueble obtenerInmuebleDesdeArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return null;
        }
        Object valor = args.getSerializable(DetalleInmuebleViewModel.ARG_INMUEBLE);
        if (valor instanceof Inmueble) {
            return (Inmueble) valor;
        }
        return null;
    }

    private void mostrarInmueble(@Nullable Inmueble inmueble) {
        if (inmueble == null || binding == null) {
            return;
        }

        binding.tvDireccion.setText(inmueble.getDireccion());
        binding.tvValor.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"))
                .format(inmueble.getValor()));
        binding.tvIdInmueble.setText(getString(R.string.detalle_id_inmueble, inmueble.getIdInmueble()));
        binding.tvTipo.setText(getString(R.string.detalle_tipo, valorSeguro(inmueble.getTipo())));
        binding.tvUso.setText(getString(R.string.detalle_uso, valorSeguro(inmueble.getUso())));
        binding.tvAmbientes.setText(getString(R.string.detalle_ambientes, inmueble.getAmbientes()));
        binding.tvSuperficie.setText(getString(R.string.detalle_superficie, inmueble.getSuperficie()));
        binding.tvCoordenadas.setText(getString(R.string.detalle_coordenadas,
                inmueble.getLatitud(), inmueble.getLongitud()));
        binding.tvPropietario.setText(obtenerNombrePropietario(inmueble));

        if (inmueble.isTieneContratoVigente()) {
            binding.tvEstadoContrato.setVisibility(View.VISIBLE);
        } else {
            binding.tvEstadoContrato.setVisibility(View.GONE);
        }

        bloquearSwitch = true;
        binding.switchDisponible.setChecked(inmueble.isDisponible());
        binding.switchDisponible.setEnabled(!inmueble.isTieneContratoVigente());
        bloquearSwitch = false;

        cargarImagen(inmueble.getImagen());
    }

    @NonNull
    private String valorSeguro(@Nullable String valor) {
        return valor != null && !valor.isEmpty() ? valor : "-";
    }

    @NonNull
    private String obtenerNombrePropietario(@NonNull Inmueble inmueble) {
        Propietario duenio = inmueble.getDuenio();
        if (duenio == null) {
            return getString(R.string.detalle_propietario_id, inmueble.getIdPropietario());
        }
        String nombre = valorSeguro(duenio.getNombre());
        String apellido = valorSeguro(duenio.getApellido());
        if ("-".equals(nombre) && "-".equals(apellido)) {
            return getString(R.string.detalle_propietario_id, inmueble.getIdPropietario());
        }
        return nombre + " " + apellido;
    }

    private void cargarImagen(@Nullable String imagen) {
        String url = resolverUrlImagen(imagen);
        ImageRequest request = new ImageRequest.Builder(requireContext())
                .data(url)
                .placeholder(R.drawable.ic_inmueble_placeholder)
                .error(R.drawable.ic_inmueble_placeholder)
                .target(binding.imgInmueble)
                .build();
        Coil.imageLoader(requireContext()).enqueue(request);
    }

    @NonNull
    private String resolverUrlImagen(@Nullable String imagen) {
        if (imagen == null || imagen.isEmpty()) {
            return "";
        }
        if (imagen.startsWith("http://") || imagen.startsWith("https://")) {
            return imagen;
        }
        if (imagen.startsWith("/")) {
            return ApiClient.URL_BASE + imagen;
        }
        return ApiClient.URL_BASE + "/" + imagen;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
