package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.StudentItemBinding;
import com.balaabirami.abacusandroid.databinding.TransactionItemBinding;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.Student;

import java.util.List;


public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private final List<StockTransaction> stockTransactions;

    public TransactionsAdapter(List<StockTransaction> stockTransactions) {
        this.stockTransactions = stockTransactions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(TransactionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.stockTransaction = stockTransactions.get(position);
        holder.tvDate.setText(holder.stockTransaction.getDate());
        holder.tvName.setText(holder.stockTransaction.getName());
        holder.tvQuantity.setText(String.valueOf(holder.stockTransaction.getQuantity()));
        holder.tvPrevQuantity.setText(String.valueOf(holder.stockTransaction.getPrevQuantity()));
        holder.tvNewQuantity.setText(String.valueOf(holder.stockTransaction.getNewQuantity()));
        if (holder.stockTransaction.getType() == StockTransaction.TYPE.ADD.ordinal()) {
            holder.tvType.setText(R.string.added);
        } else if (holder.stockTransaction.getType() == StockTransaction.TYPE.REMOVE.ordinal()) {
            holder.tvType.setText(R.string.removed);
        }
    }

    @Override
    public int getItemCount() {
        return stockTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName, tvQuantity, tvPrevQuantity, tvNewQuantity, tvDate, tvType;//, tvFranchiseID;
        public StockTransaction stockTransaction;

        public ViewHolder(TransactionItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvName;
            tvQuantity = binding.tvQuantity;
            tvPrevQuantity = binding.tvPrevQuantity;
            tvNewQuantity = binding.tvNewQuantity;
            tvDate = binding.tvDate;
            tvType = binding.tvType;
        }
    }
}