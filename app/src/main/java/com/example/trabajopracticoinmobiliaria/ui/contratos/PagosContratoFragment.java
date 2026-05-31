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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trabajopracticoinmobiliaria.data.modelo.Pago;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentPagosContratoBinding;

import java.util.List;

public class PagosContratoFragment extends Fragment {

    private FragmentPagosContratoBinding binding;
    private PagosContratoViewModel vm;
    private PagoAdapter pagoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPagosContratoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(PagosContratoViewModel.class);

        Bundle args = getArguments();
        if (args == null || !args.containsKey(PagosContratoViewModel.ARG_ID_CONTRATO)) {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }

        int idContrato = args.getInt(PagosContratoViewModel.ARG_ID_CONTRATO);
        String direccion = args.getString(PagosContratoViewModel.ARG_DIRECCION, "");
        if (!direccion.isEmpty()) {
            binding.tvDireccionInmueble.setText(direccion);
        } else {
            binding.tvDireccionInmueble.setVisibility(View.GONE);
        }

        pagoAdapter = new PagoAdapter();
        binding.recyclerPagos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPagos.setAdapter(pagoAdapter);

        vm.getPagos().observe(getViewLifecycleOwner(), this::mostrarPagos);
        vm.getCargando().observe(getViewLifecycleOwner(), cargando ->
                binding.progressBar.setVisibility(Boolean.TRUE.equals(cargando) ? View.VISIBLE : View.GONE));
        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        vm.cargarPagos(idContrato);
    }

    private void mostrarPagos(@Nullable List<Pago> pagos) {
        if (pagos == null) {
            return;
        }
        pagoAdapter.submitList(pagos);
        binding.tvPagosVacio.setVisibility(pagos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
