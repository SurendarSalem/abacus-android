package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.databinding.StockItemBinding;
import com.balaabirami.abacusandroid.model.Stock;

import java.util.List;


public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.ViewHolder> {

    private final List<Stock> stocks;
    StockClickListener stockClickListener;

    public StockListAdapter(List<Stock> stocks, StockClickListener stockClickListener) {
        this.stocks = stocks;
        this.stockClickListener = stockClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(StockItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.stock = stocks.get(position);
        holder.tvName.setText(stocks.get(position).getName());
        holder.tvQuantity.setText(String.valueOf(stocks.get(position).getQuantity()));
        holder.btnUpdate.setOnClickListener(view -> {
            stockClickListener.onUpdateClicked(holder.stock);
        });
        holder.itemView.setOnClickListener(view -> {
            stockClickListener.onStockClicked(holder.stock);
        });
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvQuantity;
        public final AppCompatButton btnUpdate;
        public Stock stock;

        public ViewHolder(StockItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.etName;
            tvQuantity = binding.tvQuantity;
            btnUpdate = binding.btnUpdate;
        }
    }

    public interface StockClickListener {

        void onStockClicked(Stock stock);

        void onUpdateClicked(Stock stock);

        void onStockAdded(Stock stock, int qtyInput);

        void onStockRemoved(Stock stock, int qty);
    }
}