package com.example.trabajopracticoinmobiliaria.ui.contratos;

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
import com.example.trabajopracticoinmobiliaria.data.modelo.Contrato;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inquilino;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentDetalleContratoBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class DetalleContratoFragment extends Fragment {

    private FragmentDetalleContratoBinding binding;
    private DetalleContratoViewModel vm;
    private Inmueble inmuebleActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleContratoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(DetalleContratoViewModel.class);

        inmuebleActual = obtenerInmuebleDesdeArgs();
        if (inmuebleActual == null) {
            Toast.makeText(requireContext(), R.string.error_contrato_no_encontrado, Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }
        vm.setInmueble(inmuebleActual);
        binding.tvDireccionInmueble.setText(inmuebleActual.getDireccion());

        vm.getContrato().observe(getViewLifecycleOwner(), this::mostrarContrato);
        vm.getCargando().observe(getViewLifecycleOwner(), cargando ->
                binding.progressBar.setVisibility(Boolean.TRUE.equals(cargando) ? View.VISIBLE : View.GONE));
        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnVerPagos.setOnClickListener(v -> navegarAPagos());

        vm.cargarContrato();
    }

    @Nullable
    private Inmueble obtenerInmuebleDesdeArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return null;
        }
        Object valor = args.getSerializable(DetalleContratoViewModel.ARG_INMUEBLE);
        if (valor instanceof Inmueble) {
            return (Inmueble) valor;
        }
        return null;
    }

    private void mostrarContrato(@Nullable Contrato contrato) {
        if (contrato == null) {
            return;
        }
        binding.cardContrato.setVisibility(View.VISIBLE);
        binding.btnVerPagos.setVisibility(View.VISIBLE);
        binding.tvContratoId.setText(getString(R.string.contrato_id, contrato.getIdContrato()));
        binding.tvFechaInicio.setText(getString(R.string.contrato_fecha_inicio,
                contrato.getFechaInicio() != null ? contrato.getFechaInicio() : "-"));
        binding.tvFechaFin.setText(getString(R.string.contrato_fecha_fin,
                contrato.getFechaFinalizacion() != null ? contrato.getFechaFinalizacion() : "-"));
        binding.tvMontoAlquiler.setText(getString(R.string.contrato_monto_alquiler,
                NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"))
                        .format(contrato.getMontoAlquiler())));
        binding.tvEstadoContrato.setText(contrato.isEstado()
                ? R.string.contrato_estado_activo
                : R.string.contrato_estado_inactivo);

        Inquilino inquilino = contrato.getInquilino();
        if (inquilino != null) {
            binding.cardInquilino.setVisibility(View.VISIBLE);
            binding.tvInquilinoNombre.setText(getString(R.string.inquilino_label_nombre,
                    inquilino.getNombre() != null ? inquilino.getNombre() : "-"));
            binding.tvInquilinoApellido.setText(getString(R.string.inquilino_label_apellido,
                    inquilino.getApellido() != null ? inquilino.getApellido() : "-"));
        } else {
            binding.cardInquilino.setVisibility(View.GONE);
        }
    }

    private void navegarAPagos() {
        Contrato contrato = vm.getContrato().getValue();
        if (contrato == null || inmuebleActual == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putInt(PagosContratoViewModel.ARG_ID_CONTRATO, contrato.getIdContrato());
        args.putString(PagosContratoViewModel.ARG_DIRECCION, inmuebleActual.getDireccion());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_detalle_contrato_to_pagos, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
