package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.databinding.HeaderBinding;
import com.balaabirami.abacusandroid.databinding.OrderItemBinding;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.OrderList;

import java.util.ArrayList;
import java.util.List;


public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<OrderList> orders, filteredOrders = new ArrayList<>();

    public OrderListAdapter(List<OrderList> orders) {
        this.orders = orders;
    }

    public static final int TYPE_HEADER = 1;

    public static final int TYPE_ORDER = 2;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ORDER) {
            return new OrderHolder(OrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new HeaderHolder(HeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (filteredOrders.get(position).getHeader() != null) {
            return TYPE_HEADER;
        } else if (filteredOrders.get(position).getOrder() != null) {
            return TYPE_ORDER;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ORDER) {
            ((OrderHolder) holder).bind(filteredOrders.get(position).getOrder());
        } else {
            ((HeaderHolder) holder).bind(filteredOrders.get(position).getHeader());
        }
    }

    @Override
    public int getItemCount() {
        return filteredOrders.size();
    }

    public void updateList(List<OrderList> orders) {
        this.filteredOrders.clear();
        this.filteredOrders.addAll(orders);
        notifyDataSetChanged();
    }

    public void notifyList(List<OrderList> orders) {
        this.filteredOrders.clear();
        this.orders.clear();
        this.orders.addAll(orders);
        this.filteredOrders.addAll(orders);
        notifyDataSetChanged();
    }

    public void clearFilter() {
        updateList(orders);
    }

    public List<OrderList> getOrders() {
        return filteredOrders;
    }


    public class OrderHolder extends RecyclerView.ViewHolder {
        private final OrderItemBinding binding;
        public Order order;

        public OrderHolder(OrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.setOrder(order);
            binding.tvDate.setText(order.getDate());
            binding.tvState.setText(order.getState());
            binding.tvFranchise.setText(order.getFranchiseName());
            binding.tvStudentName.setText(order.getStudentName());
            binding.tvLevel.setText(order.getOrderLevel().getName());
            StringBuilder bookNames = new StringBuilder();
            for (String book : order.getBooks()) {
                bookNames.append("\u2022 ").append(book + "\n");
            }
            binding.tvItems.setText(bookNames.toString());

        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private final HeaderBinding binding;

        public HeaderHolder(HeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String header) {
            binding.setHeader(header);
        }
    }
}