package com.example.trabajopracticoinmobiliaria.ui.contratos;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trabajopracticoinmobiliaria.R;
import com.example.trabajopracticoinmobiliaria.data.modelo.Pago;
import com.example.trabajopracticoinmobiliaria.databinding.ItemPagoBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class PagoAdapter extends ListAdapter<Pago, PagoAdapter.PagoViewHolder> {

    private static final DiffUtil.ItemCallback<Pago> DIFF_CALLBACK = new DiffUtil.ItemCallback<Pago>() {
        @Override
        public boolean areItemsTheSame(@NonNull Pago oldItem, @NonNull Pago newItem) {
            return oldItem.getIdPago() == newItem.getIdPago();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pago oldItem, @NonNull Pago newItem) {
            return oldItem.getMonto() == newItem.getMonto()
                    && oldItem.isEstado() == newItem.isEstado()
                    && equals(oldItem.getFechaPago(), newItem.getFechaPago())
                    && equals(oldItem.getDetalle(), newItem.getDetalle());
        }

        private boolean equals(String a, String b) {
            if (a == null) {
                return b == null;
            }
            return a.equals(b);
        }
    };

    public PagoAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPagoBinding binding = ItemPagoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PagoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class PagoViewHolder extends RecyclerView.ViewHolder {

        private final ItemPagoBinding binding;

        PagoViewHolder(@NonNull ItemPagoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull Pago pago) {
            binding.tvPagoId.setText("Pago #" + pago.getIdPago());
            binding.tvPagoMonto.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"))
                    .format(pago.getMonto()));
            binding.tvPagoFecha.setText(binding.getRoot().getContext()
                    .getString(R.string.pago_fecha, pago.getFechaPago() != null ? pago.getFechaPago() : "-"));
            binding.tvPagoDetalle.setText(binding.getRoot().getContext()
                    .getString(R.string.pago_detalle, pago.getDetalle() != null ? pago.getDetalle() : "-"));

            if (pago.isEstado()) {
                binding.tvPagoEstado.setText(R.string.pago_estado_ok);
                binding.tvPagoEstado.setTextColor(color(R.color.estado_disponible));
            } else {
                binding.tvPagoEstado.setText(R.string.pago_estado_pendiente);
                binding.tvPagoEstado.setTextColor(color(R.color.estado_no_disponible));
            }
        }

        @ColorInt
        private int color(int resId) {
            return ContextCompat.getColor(binding.getRoot().getContext(), resId);
        }
    }
}
