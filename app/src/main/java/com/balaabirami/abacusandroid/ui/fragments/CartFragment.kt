package com.balaabirami.abacusandroid.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.balaabirami.abacusandroid.R
import com.balaabirami.abacusandroid.databinding.FragmentCartBinding
import com.balaabirami.abacusandroid.model.CartOrder
import com.balaabirami.abacusandroid.model.Order
import com.balaabirami.abacusandroid.model.Status
import com.balaabirami.abacusandroid.repository.CartRepository
import com.balaabirami.abacusandroid.ui.activities.HomeActivity
import com.balaabirami.abacusandroid.ui.activities.PaymentActivity
import com.balaabirami.abacusandroid.ui.adapter.CartAdapter
import com.balaabirami.abacusandroid.utils.UIUtils
import com.balaabirami.abacusandroid.viewmodel.CartViewModel

class CartFragment : Fragment(), CartAdapter.CartListener {

    var binding: FragmentCartBinding? = null
    private var cartOrders: MutableList<CartOrder>? = mutableListOf()
    private var cartAdapter: CartAdapter? = null
    var totalAmount = 0
    private var cartViewModlel: CartViewModel? = null
    lateinit var alertDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartOrders = CartRepository.getInstance().cart.orders
        cartViewModlel = ViewModelProvider(this).get(CartViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartOrders?.let {
            cartAdapter = CartAdapter(it, this)
            binding?.rvCart?.layoutManager = (LinearLayoutManager(requireContext()))
            binding?.rvCart?.adapter = cartAdapter
            setAmount()
        }
        binding?.btnPay?.setOnClickListener {
            if (totalAmount > 0) {
                openPaymentActivityForResult()
            }
        }
        alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Failed orders")
        observeForResult()
    }

    private fun observeForResult() {
        cartViewModlel?.orderResult?.let {
            it.observe(viewLifecycleOwner) {
                it?.let { it ->
                    when (it.status) {
                        Status.LOADING -> {
                            showProgress(true)
                        }
                        Status.SUCCESS -> {
                            showProgress(false)
                            it.data?.let {
                                if (it.size == 0) {
                                    CartRepository.getInstance().clearAll()
                                }
                            }
                            UIUtils.showToast(requireContext(), "Order placed successfully.")
                        }
                        Status.ERROR -> {
                            showProgress(false)
                        }
                    }
                    it.data?.let {
                        if (it.size > 0) {
                            showProgress(false)
                            showFailedOrders(it)
                        }
                    }
                }
            }
        }
    }

    private fun showFailedOrders(failedOrders: List<CartOrder>) {
        var message = "Order has been failed for below students. \n"
        for (failedOrder in failedOrders) {
            message = message + failedOrder.student.name + "\n"
        }
        alertDialog.setMessage(message)
        alertDialog.show()
    }

    private fun openPaymentActivityForResult() {
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra("amount", totalAmount.toString())
        someActivityResultLauncher.launch(intent)
    }

    private fun setAmount() {
        totalAmount = 0
        cartOrders?.let {
            it.forEach { order ->
                totalAmount += Order.getOrderValue(order.currentUser).toInt()
            }
            if (totalAmount > 0) {
                binding?.btnPay?.visibility = View.VISIBLE
                binding?.btnPay?.text = "Proceed to pay Rs. $totalAmount"
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = CartFragment()
    }

    override fun onRemoveOrder(
        cartOrder: CartOrder?,
        position: Int
    ) {
        cartOrder?.let {
            cartOrders?.remove(it)
            cartAdapter?.notifyItemRemoved(position)
            setAmount()
        }
    }

    private var someActivityResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                cartOrders?.let {
                    for (order in it) {
                        order.order.date = UIUtils.getDate()
                    }
                }
                cartViewModlel?.orderFromCart(cartOrders)
            }
        }

    fun showProgress(show: Boolean) {
        (requireActivity() as HomeActivity).showProgress(show)
    }
}