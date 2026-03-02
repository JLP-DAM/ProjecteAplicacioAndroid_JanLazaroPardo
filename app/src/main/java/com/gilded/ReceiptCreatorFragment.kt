package com.gilded

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.util.Calendar
import java.util.Date

class ReceiptCreatorFragment : Fragment() {

    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()

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

        val calendar = Calendar.getInstance()
        var datePickerDialog: DatePickerDialog
        var timePickerDialog: TimePickerDialog

        val timestamp = HashMap<String, Int>()

        timestamp["year"] = 0
        timestamp["month"] = 0
        timestamp["day"] = 0
        timestamp["hour"] = 0
        timestamp["minute"] = 0

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

            for (entry in timestamp.entries) {
                val key: String? = entry.key
                val value: Int? = entry.value

            }


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
            ).time.toLong()


            val category = categoryEditText.text.toString()

            if (amount == null) {
                Toast.makeText(receiptCreator.context, "Quantitat no valida! Introdueix un nombre.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            receiptsViewModel.addReceipt(Receipt(recipient, amount, timestamp, category))

            goBack()
        }

        dateEditText.setOnClickListener {
            datePickerDialog = DatePickerDialog(receiptCreator.context, {_, year, month, day ->
                dateEditText.text = SpannableStringBuilder("${day}/${month + 1}/${year}")

                timestamp["year"] = year
                timestamp["month"] = month
                timestamp["day"] = day
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        timeEditText.setOnClickListener {
            timePickerDialog = TimePickerDialog(receiptCreator.context, {_, hour, minute ->
                timeEditText.text = SpannableStringBuilder("${hour}:${minute}")

                timestamp["hour"] = hour
                timestamp["minute"] = minute
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePickerDialog.show()
        }

        return receiptCreator
    }
}