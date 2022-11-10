package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.databinding.StockAdjustListItemBinding;
import com.balaabirami.abacusandroid.databinding.StudentItemBinding;
import com.balaabirami.abacusandroid.model.StockAdjustment;
import com.balaabirami.abacusandroid.model.Student;

import java.util.ArrayList;
import java.util.List;


public class StockAdjustListAdapter extends RecyclerView.Adapter<StockAdjustListAdapter.ViewHolder> {

    private final List<StockAdjustment> stockAdjustments = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(StockAdjustListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(stockAdjustments.get(position));
    }

    @Override
    public int getItemCount() {
        return stockAdjustments.size();
    }

    public void notifyList(List<StockAdjustment> students) {
        this.stockAdjustments.clear();
        this.stockAdjustments.addAll(students);
        notifyDataSetChanged();
    }

    public List<StockAdjustment> getStockAdjustments() {
        return stockAdjustments;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final StockAdjustListItemBinding binding;

        public ViewHolder(StockAdjustListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StockAdjustment stockAdjustment) {
            binding.setTransaction(stockAdjustment);
            StringBuilder value = new StringBuilder();
            for (StockAdjustment.ItemDetail item : stockAdjustment.getItems()) {
                value.append(item.toDisplay()).append("\n");
            }
            if (value.toString().endsWith("\n")) {
                value = new StringBuilder(value.substring(0, value.length() - 1));
            }
            binding.tvItems.setText(value.toString());
            binding.tvAmount.setText(stockAdjustment.getPaymentType() == StockAdjustment.PaymentType.CASH ? String.valueOf(stockAdjustment.getAmount()) : "Free");
        }
    }
}