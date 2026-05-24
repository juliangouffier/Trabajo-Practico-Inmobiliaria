package com.example.trabajopracticoinmobiliaria.ui.inmuebles;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;
import com.example.trabajopracticoinmobiliaria.databinding.ItemInmuebleBinding;

import coil.Coil;
import coil.request.ImageRequest;

import java.text.NumberFormat;
import java.util.Locale;

public class InmuebleAdapter extends ListAdapter<Inmueble, InmuebleAdapter.InmuebleViewHolder> {

    public interface OnInmuebleClickListener {
        void onInmuebleClick(Inmueble inmueble);
    }

    private final OnInmuebleClickListener listenerClick;

    public InmuebleAdapter(@NonNull OnInmuebleClickListener listenerClick) {
        super(DIFF_CALLBACK);
        this.listenerClick = listenerClick;
    }

    private static final DiffUtil.ItemCallback<Inmueble> DIFF_CALLBACK = new DiffUtil.ItemCallback<Inmueble>() {
        @Override
        public boolean areItemsTheSame(@NonNull Inmueble oldItem, @NonNull Inmueble newItem) {
            return oldItem.getIdInmueble() == newItem.getIdInmueble();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Inmueble oldItem, @NonNull Inmueble newItem) {
            return _stringEquals(oldItem.getDireccion(), newItem.getDireccion())
                    && oldItem.isDisponible() == newItem.isDisponible()
                    && oldItem.isTieneContratoVigente() == newItem.isTieneContratoVigente()
                    && oldItem.getValor() == newItem.getValor()
                    && _stringEquals(oldItem.getImagen(), newItem.getImagen());
        }

        private boolean _stringEquals(String a, String b) {
            if (a == null) {
                return b == null;
            }
            return a.equals(b);
        }
    };

    @NonNull
    @Override
    public InmuebleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInmuebleBinding binding = ItemInmuebleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new InmuebleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InmuebleViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class InmuebleViewHolder extends RecyclerView.ViewHolder {

        private final ItemInmuebleBinding binding;

        InmuebleViewHolder(@NonNull ItemInmuebleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull Inmueble inmueble) {
            binding.tvDireccion.setText(inmueble.getDireccion());
            binding.tvDetalle.setText(String.format(Locale.getDefault(), "%s · %s · %d amb · %d m²",
                    inmueble.getTipo() != null ? inmueble.getTipo() : "-",
                    inmueble.getUso() != null ? inmueble.getUso() : "-",
                    inmueble.getAmbientes(),
                    inmueble.getSuperficie()));
            binding.tvValor.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"))
                    .format(inmueble.getValor()));

            if (inmueble.isTieneContratoVigente()) {
                binding.tvEstadoContrato.setText(binding.getRoot().getContext()
                        .getString(R.string.inmueble_con_contrato_vigente));
                binding.tvEstadoContrato.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvEstadoContrato.setVisibility(android.view.View.GONE);
            }

            mostrarDisponibilidad(inmueble.isDisponible());

            binding.getRoot().setOnClickListener(v -> listenerClick.onInmuebleClick(inmueble));

            cargarImagen(inmueble);
        }

        private void mostrarDisponibilidad(boolean disponible) {
            if (disponible) {
                binding.tvDisponibilidad.setText(R.string.estado_disponible);
                binding.tvDisponibilidad.setTextColor(color(R.color.estado_disponible));
            } else {
                binding.tvDisponibilidad.setText(R.string.estado_no_disponible);
                binding.tvDisponibilidad.setTextColor(color(R.color.estado_no_disponible));
            }
        }

        @ColorInt
        private int color(int resId) {
            return ContextCompat.getColor(binding.getRoot().getContext(), resId);
        }

        private void cargarImagen(@NonNull Inmueble inmueble) {
            String url = resolverUrlImagen(inmueble.getImagen());
            ImageRequest.Builder builder = new ImageRequest.Builder(binding.getRoot().getContext())
                    .data(url)
                    .placeholder(R.drawable.ic_inmueble_placeholder)
                    .error(R.drawable.ic_inmueble_placeholder)
                    .target(binding.imgInmueble);
            Coil.imageLoader(binding.getRoot().getContext()).enqueue(builder.build());
        }

        @NonNull
        private String resolverUrlImagen(String imagen) {
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
    }
}
