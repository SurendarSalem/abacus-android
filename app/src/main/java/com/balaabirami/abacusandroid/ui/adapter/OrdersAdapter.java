package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.OrderItemBinding;
import com.balaabirami.abacusandroid.databinding.TransactionItemBinding;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final List<Order> orders, filteredOrders = new ArrayList<>();

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(OrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredOrders.size();
    }

   /* public void applyFilter(String[] filters) {
        if (filters[0] == null && filters[1] == null) {
            //studentClickListener.onError("No Filter selected!");
            filteredOrders.clear();
            filteredOrders.addAll(orders);
        } else {
            filteredOrders.clear();
            for (Order order : orders) {
                if (order.getStudentState().equalsIgnoreCase(filters[0]) || order.getFranchiseName().equalsIgnoreCase(filters[1])) {
                    filteredOrders.add(order);
                }
            }
        }
        updateList(filteredOrders);
    }*/

    public void updateList(List<Order> orders) {
        this.filteredOrders.clear();
        this.filteredOrders.addAll(orders);
        notifyDataSetChanged();
    }

    public void notifyList(List<Order> orders) {
        this.filteredOrders.clear();
        this.orders.clear();
       /* for (Order order : orders) {
            if (order.getDate().equals(UIUtils.getDate())) {
                this.orders.add(order);
                this.filteredOrders.addAll(orders);
            }
        }*/

        this.orders.addAll(orders);
        this.filteredOrders.addAll(orders);
        notifyDataSetChanged();
    }

    public void clearFilter() {
        filteredOrders.clear();
        filteredOrders.addAll(orders);
        updateList(filteredOrders);
    }

    public List<Order> getOrders() {
        return filteredOrders;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final OrderItemBinding binding;
        public Order order;

        public ViewHolder(OrderItemBinding binding) {
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
           /* String books = null;
            for (String bookName : order.getBooks()) {
                books = bookName + ",";
            }*/
            binding.tvItems.setText(order.getBooks().toString());
        }
    }
}