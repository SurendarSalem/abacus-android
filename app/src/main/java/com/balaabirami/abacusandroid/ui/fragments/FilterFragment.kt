package com.balaabirami.abacusandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.balaabirami.abacusandroid.R
import com.balaabirami.abacusandroid.databinding.FragmentFilterBinding
import com.balaabirami.abacusandroid.databinding.FranchiseItemBinding
import com.balaabirami.abacusandroid.model.Student
import com.balaabirami.abacusandroid.model.User
import com.balaabirami.abacusandroid.repository.FranchiseRepository
import com.balaabirami.abacusandroid.repository.StudentsRepository
import com.balaabirami.abacusandroid.ui.adapter.GenericFilterAdapter
import com.balaabirami.abacusandroid.ui.adapter.GenericFilterBindingInterface
import com.balaabirami.abacusandroid.ui.adapter.GenericFilterInterface


class FilterFragment : Fragment() {

    companion object {
        const val FILTER_TYPE = "type"
        const val FILTER_STUDENT = 1
        const val FILTER_FRANCHISE = 2
    }

    private var con: ViewGroup? = null
    private var type: Int? = null
    lateinit var binding: FragmentFilterBinding
    var filterSelectionListener: FilterSelectionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(FILTER_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)
        con = container
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvItems.layoutManager = LinearLayoutManager(context)

        when (type) {
            FILTER_STUDENT -> {
                setStudentAdapter()
            }
            FILTER_FRANCHISE -> {
                setFranchiseAdapter()
            }
        }
    }

    private fun setStudentAdapter() {

        var adapter: GenericFilterAdapter<Student>? = null
        adapter = GenericFilterAdapter(getStudents(),
            R.layout.franchise_item,
            object : GenericFilterBindingInterface<Student, ViewDataBinding> {
                override fun bindData(item: Student, binding: ViewDataBinding) {
                    (binding as FranchiseItemBinding).tvName.text = item.name
                    binding.root.setOnClickListener {
                        filterSelectionListener?.onStudentSelected(item)
                        parentFragmentManager.popBackStack()
                    }
                }
            },
            object : GenericFilterInterface<Student> {
                override fun filter(items: List<Student>, value: String) {
                    getFilteredStudents(items, value)
                    adapter?.notifyDataSetChanged()
                }

                override fun getFilteredItems(
                    value: String,
                    dataSet: List<Student>
                ): List<Student> {
                    return dataSet.filter {
                        it.name.lowercase().contains(value)
                    }
                }

            }
        )
        binding.rvItems.adapter = adapter
        binding.etSearch.addTextChangedListener {
            val value = it.toString()
            adapter.filterData(value)
        }
    }

    private fun setFranchiseAdapter() {
        var adapter: GenericFilterAdapter<User>? = null
        adapter = GenericFilterAdapter(getFranchises(),
            R.layout.franchise_item,
            object : GenericFilterBindingInterface<User, ViewDataBinding> {
                override fun bindData(item: User, binding: ViewDataBinding) {
                    (binding as FranchiseItemBinding).tvName.text = item.name
                    binding.root.setOnClickListener {
                        filterSelectionListener?.onFranchiseSelected(item)
                        parentFragmentManager.popBackStack()
                    }
                }
            },
            object : GenericFilterInterface<User> {
                override fun filter(items: List<User>, value: String) {
                    getFilteredFranchise(items, value)
                    adapter?.notifyDataSetChanged()
                }

                override fun getFilteredItems(
                    value: String,
                    dataSet: List<User>
                ): List<User> {
                    return dataSet.filter {
                        it.name.lowercase().contains(value)
                    }
                }

            }
        )
        binding.rvItems.adapter = adapter
        binding.etSearch.addTextChangedListener {
            val value = it.toString()
            adapter.filterData(value)
        }
    }

    private fun getFilteredStudents(students: List<Student>, value: String) {
        students.filter {
            it.name.lowercase().contains(value)
        }
    }

    private fun getFilteredFranchise(franchises: List<User>, value: String) {
        franchises.filter {
            it.name.lowercase().contains(value)
        }
    }

    private fun getStudents(): List<Student> {
        var students = StudentsRepository.getInstance().students
        if (students == null) {
            students = emptyList()
        }
        return students
    }

    private fun getFranchises(): List<User> {
        var franchises = emptyList<User>()
        if (FranchiseRepository.getInstance().franchises != null && !FranchiseRepository.getInstance().franchises.isEmpty()) {
            franchises = ArrayList(FranchiseRepository.getInstance().franchises)
            franchises.removeAt(0)
        }
        return franchises
    }

    interface FilterSelectionListener {
        fun onStudentSelected(student: Student)
        fun onFranchiseSelected(franchises: User)
    }


}
