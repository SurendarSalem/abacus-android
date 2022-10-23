package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.databinding.CartItemBinding;
import com.balaabirami.abacusandroid.model.CartOrder;
import com.balaabirami.abacusandroid.model.Order;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartOrder> cartOrders;
    CartListener cartListener;

    public CartAdapter(List<CartOrder> cartOrders, CartListener cartListener) {
        this.cartOrders = cartOrders;
        this.cartListener = cartListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CartViewHolder(CartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, int position) {
        holder.cartOrder = cartOrders.get(position);
        holder.tvName.setText(cartOrders.get(position).getOrder().getStudentName());
        holder.tvType.setText("Type - " + cartOrders.get(position).getOrderType().name());
        holder.tvAmount.setText("Rs. " + Order.getOrderValue(cartOrders.get(position).getCurrentUser()));
        holder.ivRemove.setOnClickListener(view -> cartListener.onRemoveOrder(holder.cartOrder, position));
    }

    @Override
    public int getItemCount() {
        return cartOrders.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvType;
        public final TextView tvAmount;
        public final AppCompatImageView ivRemove;
        public CartOrder cartOrder;

        public CartViewHolder(CartItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvName;
            tvType = binding.tvType;
            tvAmount = binding.tvAmount;
            ivRemove = binding.ivRemove;
        }
    }

    public interface CartListener {
        void onRemoveOrder(CartOrder cartOrder, int position);
    }
}