package com.gilded.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gilded.R
import com.gilded.models.Receipt
import com.gilded.viewmodels.CategoriesViewModel
import com.gilded.viewmodels.ReceiptsViewModel
import java.util.Calendar
import java.util.Date

class ReceiptCreatorFragment : Fragment() {

    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val receiptCreator = inflater.inflate(R.layout.fragment_receipt_creator, container, false)

        val recipientEditText: EditText = receiptCreator.findViewById(R.id.recipient)
        val amountEditText: EditText = receiptCreator.findViewById(R.id.amount)
        val timeEditText: EditText = receiptCreator.findViewById(R.id.time)
        val dateEditText: EditText = receiptCreator.findViewById(R.id.date)
        val categoryEditText: EditText = receiptCreator.findViewById(R.id.category)

        val createCardView: CardView = receiptCreator.findViewById(R.id.create)
        val goBackButton: ImageButton = receiptCreator.findViewById(R.id.goBack)

        val categorySelectionConstraintLayout: ConstraintLayout = receiptCreator.findViewById(R.id.categorySelection)

        val categoriesGridLayout: GridLayout = categorySelectionConstraintLayout.findViewById(R.id.categories)
        val closeCategorySelectionImageButton: ImageButton = categorySelectionConstraintLayout.findViewById(R.id.closeCategorySelection)

        val calendar = Calendar.getInstance()
        var datePickerDialog: DatePickerDialog
        var timePickerDialog: TimePickerDialog

        val timestamp = HashMap<String, Int>()

        timestamp["year"] = Date().year
        timestamp["month"] = Date().month
        timestamp["day"] = Date().date
        timestamp["hour"] = Date().hours
        timestamp["minute"] = Date().minutes

        fun goBack() {
            val homeFragment = HomeFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, homeFragment)
                ?.commit()
        }

        goBackButton.setOnClickListener { goBack() }

        createCardView.setOnClickListener {
            val recipient = recipientEditText.text.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull()

            val timestampTime = Date(
                timestamp["year"]!!,
                timestamp["month"]!!,
                timestamp["day"]!!,
                timestamp["hour"]!!,
                timestamp["minute"]!!,
            ).time

            val timestamp = Date(
                timestamp["year"]!!,
                timestamp["month"]!!,
                timestamp["day"]!!,
                timestamp["hour"]!!,
                timestamp["minute"]!!,
            ).time


            val category = categoryEditText.text.toString()

            if (amount == null) {
                Toast.makeText(receiptCreator.context, "Quantitat no valida! Introdueix un nombre.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (categoriesViewModel.getCategory(category) == null) {
                Toast.makeText(receiptCreator.context, "Categoria no valida! Introdueix una categoria existent.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            receiptsViewModel.addReceipt(Receipt(recipient, amount, timestamp, category))

            goBack()
        }

        dateEditText.setOnClickListener {
            datePickerDialog = DatePickerDialog(
                receiptCreator.context,
                { _, year, month, day ->
                    dateEditText.text = SpannableStringBuilder("${day}/${month + 1}/${year}")

                    timestamp["year"] = (year - 1900)
                    timestamp["month"] = month
                    timestamp["day"] = day
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        timeEditText.setOnClickListener {
            timePickerDialog = TimePickerDialog(receiptCreator.context, { _, hour, minute ->
                timeEditText.text = SpannableStringBuilder("${hour}:${minute}")

                timestamp["hour"] = hour
                timestamp["minute"] = minute
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePickerDialog.show()
        }

        categoryEditText.setOnClickListener {
            categoriesGridLayout.removeAllViews()
            categoriesGridLayout.invalidate()

            categorySelectionConstraintLayout.visibility = View.VISIBLE

            val addCategoryCardView: View = inflater.inflate(R.layout.category, null)
            val addCategoryNameTextView: TextView = addCategoryCardView.findViewById(R.id.name)
            val addCategoryColorCardView: CardView = addCategoryCardView.findViewById(R.id.color)
            val addCategorySelectButton: Button = addCategoryCardView.findViewById(R.id.select)
            val addCategoryImageView: ImageView = addCategoryCardView.findViewById(R.id.image)

            addCategoryNameTextView.setText("Afegir Categoria")
            addCategoryColorCardView.setCardBackgroundColor(resources.getColor(R.color.green))

            addCategoryImageView.setImageResource(R.drawable.add)

            categoriesGridLayout.addView(addCategoryCardView)

            addCategorySelectButton.setOnClickListener {
                val categoryCreatorFragment = CategoryCreatorFragment()

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainerView, categoryCreatorFragment)
                    ?.commit()
            }

            for (category in categoriesViewModel.getCategories()) {
                val categoryCardView: View = inflater.inflate(R.layout.category, null)
                val categoryNameTextView: TextView = categoryCardView.findViewById(R.id.name)
                val categoryColorCardView: CardView = categoryCardView.findViewById(R.id.color)
                val categorySelectButton: Button = categoryCardView.findViewById(R.id.select)

                categoryNameTextView.text = category.name
                categoryColorCardView.setCardBackgroundColor(category.color)

                categoriesGridLayout.addView(categoryCardView)

                categorySelectButton.setOnClickListener {
                    categorySelectionConstraintLayout.visibility = View.GONE

                    categoryEditText.setText(category.name)
                }
            }
        }

        closeCategorySelectionImageButton.setOnClickListener {
            categorySelectionConstraintLayout.visibility = View.GONE
        }

        return receiptCreator
    }
}