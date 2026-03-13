package com.gilded.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableString
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
import com.gilded.viewmodels.CurrentReceiptViewModel
import com.gilded.viewmodels.ReceiptsViewModel
import java.util.Calendar
import java.util.Date

class ReceiptEditorFragment : Fragment() {

    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val currentReceiptViewModel: CurrentReceiptViewModel by activityViewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val receiptEditor = inflater.inflate(R.layout.fragment_receipt_editor, container, false)

        val recipientEditText: EditText = receiptEditor.findViewById(R.id.recipient)
        val amountEditText: EditText = receiptEditor.findViewById(R.id.amount)
        val timeEditText: EditText = receiptEditor.findViewById(R.id.time)
        val dateEditText: EditText = receiptEditor.findViewById(R.id.date)
        val categoryEditText: EditText = receiptEditor.findViewById(R.id.category)

        val editCardView: CardView = receiptEditor.findViewById(R.id.edit)
        val goBackButton: ImageButton = receiptEditor.findViewById(R.id.goBack)

        val categorySelectionConstraintLayout: ConstraintLayout = receiptEditor.findViewById(R.id.categorySelection)

        val categoriesGridLayout: GridLayout = categorySelectionConstraintLayout.findViewById(R.id.categories)
        val closeCategorySelectionImageButton: ImageButton = categorySelectionConstraintLayout.findViewById(R.id.closeCategorySelection)

        val calendar = Calendar.getInstance()
        var datePickerDialog: DatePickerDialog
        var timePickerDialog: TimePickerDialog

        val timestamp = HashMap<String, Int>()

        val currentReceipt = currentReceiptViewModel.receipt.value!!

        timestamp["year"] = Date(currentReceipt.timestamp).year
        timestamp["month"] = Date(currentReceipt.timestamp).month
        timestamp["day"] = Date(currentReceipt.timestamp).date
        timestamp["hour"] = Date(currentReceipt.timestamp).hours
        timestamp["minute"] = Date(currentReceipt.timestamp).minutes

        recipientEditText.text = SpannableStringBuilder(currentReceipt.recipient)
        amountEditText.text = SpannableStringBuilder(currentReceipt.amount.toString())
        dateEditText.text = SpannableStringBuilder("${timestamp["day"]}/${timestamp["month"]!! + 1}/${timestamp["year"]}")
        timeEditText.text = SpannableStringBuilder("${timestamp["hour"]}:${timestamp["minute"]}")
        categoryEditText.text = SpannableStringBuilder(currentReceipt.category)

        fun goBack() {
            val receiptViewerFragment = ReceiptViewerFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, receiptViewerFragment)
                ?.commit()
        }

        goBackButton.setOnClickListener { goBack() }

        editCardView.setOnClickListener {
            val recipient = recipientEditText.text.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull()

            val timestamp = Date(
                timestamp["year"]!!,
                timestamp["month"]!!,
                timestamp["day"]!!,
                timestamp["hour"]!!,
                timestamp["minute"]!!,
            ).time

            val category = categoryEditText.text.toString()

            if (amount == null) {
                Toast.makeText(receiptEditor.context, "Quantitat no valida! Introdueix un nombre.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (categoriesViewModel.getCategory(category) == null) {
                Toast.makeText(receiptEditor.context, "Categoria no valida! Introdueix una categoria existent.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            receiptsViewModel.updateReceipt(Receipt(currentReceipt.id, recipient, amount, timestamp, category))

            goBack()
        }

        dateEditText.setOnClickListener {
            datePickerDialog = DatePickerDialog(
                receiptEditor.context,
                { _, year, month, day ->
                    dateEditText.text = SpannableStringBuilder("${day}/${month + 1}/${year}")

                    timestamp["year"] = (year - 1900)
                    timestamp["month"] = month
                    timestamp["day"] = day
                },

                Date(currentReceipt.timestamp).year,
                Date(currentReceipt.timestamp).month,
                Date(currentReceipt.timestamp).date
            )

            datePickerDialog.show()
        }

        timeEditText.setOnClickListener {
            timePickerDialog = TimePickerDialog(receiptEditor.context, { _, hour, minute ->
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

        return receiptEditor
    }
}