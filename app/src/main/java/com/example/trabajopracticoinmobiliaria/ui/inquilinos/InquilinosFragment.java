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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.databinding.FragmentInquilinosBinding;
import com.example.trabajopracticoinmobiliaria.ui.inmuebles.InmuebleAdapter;
import com.example.trabajopracticoinmobiliaria.ui.inmuebles.InmueblesAlquiladosViewModel;

import java.util.List;

public class InquilinosFragment extends Fragment {

    private FragmentInquilinosBinding binding;
    private InmueblesAlquiladosViewModel vm;
    private InmuebleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInquilinosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(InmueblesAlquiladosViewModel.class);

        adapter = new InmuebleAdapter(this::navegarADetalleInquilino);
        binding.recyclerInquilinos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerInquilinos.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> vm.cargarInmueblesAlquilados());

        vm.getInmueblesAlquilados().observe(getViewLifecycleOwner(), this::mostrarInmuebles);
        vm.getCargando().observe(getViewLifecycleOwner(), this::mostrarCargando);
        vm.getMensajeError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        vm.cargarInmueblesAlquilados();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (vm != null) {
            vm.cargarInmueblesAlquilados();
        }
    }

    private void navegarADetalleInquilino(@NonNull Inmueble inmueble) {
        Bundle args = new Bundle();
        args.putSerializable(DetalleInquilinoViewModel.ARG_INMUEBLE, inmueble);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_inquilinos_to_detalle_inquilino, args);
    }

    private void mostrarInmuebles(@Nullable List<Inmueble> lista) {
        if (lista == null) {
            return;
        }
        adapter.submitList(lista);
        binding.tvListaVacia.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarCargando(@Nullable Boolean cargando) {
        boolean enCarga = Boolean.TRUE.equals(cargando);
        binding.progressBar.setVisibility(enCarga ? View.VISIBLE : View.GONE);
        binding.swipeRefresh.setRefreshing(false);
        if (enCarga && adapter.getItemCount() == 0) {
            binding.tvListaVacia.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
