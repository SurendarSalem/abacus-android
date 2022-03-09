package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.TransactionItemBinding;
import com.balaabirami.abacusandroid.model.StockTransaction;

import java.util.ArrayList;
import java.util.List;


public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private final List<StockTransaction> stockTransactions, filteredStockTransactions = new ArrayList<>();

    public TransactionsAdapter(List<StockTransaction> stockTransactions) {
        this.stockTransactions = stockTransactions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(TransactionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.stockTransaction = filteredStockTransactions.get(position);
        holder.tvDate.setText(holder.stockTransaction.getDate());
        holder.tvName.setText(holder.stockTransaction.getName());
        holder.tvSalesQty.setText(String.valueOf(holder.stockTransaction.getSalesQuantity()));
        holder.tvPurchaseQty.setText(String.valueOf(holder.stockTransaction.getPurchaseQuantity()));
        holder.tvStockQty.setText(String.valueOf(holder.stockTransaction.getStockQuantity()));
        if (holder.stockTransaction.getType() == StockTransaction.TYPE.ADD.ordinal()) {
            holder.tvType.setText(R.string.added);
        } else if (holder.stockTransaction.getType() == StockTransaction.TYPE.REMOVE.ordinal()) {
            holder.tvType.setText(R.string.removed);
        }
        holder.tvTxnNo.setText(String.format("%04d", position + 1));
    }

    @Override
    public int getItemCount() {
        return filteredStockTransactions.size();
    }

    public void applyFilter(String[] filters) {
        if (filters[0] == null && filters[1] == null) {
            //studentClickListener.onError("No Filter selected!");
            filteredStockTransactions.clear();
            filteredStockTransactions.addAll(stockTransactions);
        } else {
            filteredStockTransactions.clear();
            for (StockTransaction stockTransaction : stockTransactions) {
                if (stockTransaction.getStudentState().equalsIgnoreCase(filters[0]) || stockTransaction.getFranchiseName().equalsIgnoreCase(filters[1])) {
                    filteredStockTransactions.add(stockTransaction);
                }
            }
        }
        updateList(filteredStockTransactions);
    }

    public void updateList(List<StockTransaction> filteredStockTransactions) {
        this.filteredStockTransactions.clear();
        this.filteredStockTransactions.addAll(filteredStockTransactions);
        notifyDataSetChanged();
    }

    public void notifyList(List<StockTransaction> stockTransactions) {
        this.filteredStockTransactions.clear();
        this.stockTransactions.clear();
        this.stockTransactions.addAll(stockTransactions);
        filteredStockTransactions.addAll(stockTransactions);
        notifyDataSetChanged();
    }

    public void clearFilter() {
        filteredStockTransactions.clear();
        filteredStockTransactions.addAll(stockTransactions);
        updateList(filteredStockTransactions);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName, tvSalesQty, tvPurchaseQty, tvStockQty, tvDate, tvType, tvTxnNo;//, tvFranchiseID;
        public StockTransaction stockTransaction;

        public ViewHolder(TransactionItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvName;
            tvSalesQty = binding.tvQuantity;
            tvPurchaseQty = binding.tvPrevQuantity;
            tvStockQty = binding.tvNewQuantity;
            tvDate = binding.tvDate;
            tvType = binding.tvType;
            tvTxnNo = binding.tvTxnNo;
        }
    }
}