package com.balaabirami.abacusandroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.balaabirami.abacusandroid.ui.adapter.multiadapter.FilterAdapter

class GenericFilterAdapter<T : Any>(
    private var dataSet: List<T>,
    @LayoutRes val resource: Int,
    private val bindingInterface: GenericFilterBindingInterface<T, ViewDataBinding>,
    private val filterInterface: GenericFilterInterface<T>,
) :
    RecyclerView.Adapter<GenericFilterAdapter.DataBindHolder>() {

    private lateinit var listener: FranchiseListAdapter.FranchiseClickListener
    private var filteredList: MutableList<T> = mutableListOf<T>().apply { addAll(dataSet) }

    class DataBindHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun <T : Any, B : ViewDataBinding> bind(
            item: T,
            bindingInterface: GenericFilterBindingInterface<T, B>,
            binding: B
        ) = bindingInterface.bindData(item, binding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindHolder {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), resource, parent, false)
        return DataBindHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindHolder, position: Int) {
        val item = filteredList[position]
        holder.bind(item, bindingInterface, holder.binding)
    }

    override fun getItemCount(): Int = filteredList.size


    fun filterData(value: String) {
        filteredList.clear()
        filteredList.addAll(filterInterface.getFilteredItems(value, dataSet) as ArrayList<T>)
        notifyDataSetChanged()
    }
}

interface GenericFilterBindingInterface<T, B : ViewDataBinding> {
    fun bindData(item: T, binding: B)
}


interface GenericFilterInterface<T> {
    fun filter(items: List<T>, value: String)
    fun getFilteredItems(value: String, dataSet: List<T>): List<T>
}
