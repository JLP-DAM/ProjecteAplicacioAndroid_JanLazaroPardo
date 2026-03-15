package com.gilded.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gilded.R
import com.gilded.models.Category
import com.gilded.viewmodels.CategoriesViewModel
import com.gilded.viewmodels.FilterViewModel
import kotlin.getValue

class FilterFragment : Fragment() {
    private val filterViewModel: FilterViewModel by activityViewModels()

    private val categoriesViewModel: CategoriesViewModel by activityViewModels()
    lateinit var filterFragmentView: View

    lateinit var filteredCategorySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        filterFragmentView = inflater.inflate(R.layout.fragment_filter, container, false)

        val incomeVisibleCheckBox: CheckBox = filterFragmentView.findViewById(R.id.income)
        val expensesVisibleCheckBox: CheckBox = filterFragmentView.findViewById(R.id.expenses)
        val resetCardView: CardView = filterFragmentView.findViewById(R.id.reset)
        val closeButton: ImageButton = filterFragmentView.findViewById(R.id.close)

        incomeVisibleCheckBox.setOnCheckedChangeListener { _, check ->
            filterViewModel.setIncomeVisible(check)
        }

        expensesVisibleCheckBox.setOnCheckedChangeListener { _, check ->
            filterViewModel.setExpensesVisible(check)
        }

        filterViewModel.incomeVisible.observe(viewLifecycleOwner) { incomeVisible ->
            incomeVisibleCheckBox.isChecked = incomeVisible
        }

        filterViewModel.expensesVisible.observe(viewLifecycleOwner) { expenseVisible ->
            expensesVisibleCheckBox.isChecked = expenseVisible
        }

        resetCardView.setOnClickListener {
            filterViewModel.reset()
            filteredCategorySpinner.setSelection(0)
        }

        closeButton.setOnClickListener {
            val homeFragment = HomeFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, homeFragment)
                ?.commit()
        }

        setupSpinner()

        return filterFragmentView
    }

    fun setupSpinner() {
        filteredCategorySpinner = filterFragmentView.findViewById(R.id.filteredCategory)

        val categoriesArrayList = ArrayList<String>()

        categoriesArrayList.add("Totes")

        for (category in categoriesViewModel.getCategories()) {
            categoriesArrayList.add(category.name)
        }

        val spinnerArrayAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoriesArrayList)

        filteredCategorySpinner.adapter = spinnerArrayAdapter

        filteredCategorySpinner.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.setSelection(position)

                filterViewModel.setFilteredCategory(if (position == 0) null else categoriesArrayList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                parent?.setSelection(0)

                filterViewModel.setFilteredCategory(null)
            }
        });

        filteredCategorySpinner.setSelection(categoriesArrayList.indexOf(filterViewModel.filteredCategory.value))
    }
}