package com.gilded.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import com.gilded.R
import com.gilded.models.Category
import com.gilded.viewmodels.CategoriesViewModel
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import kotlin.getValue

class CategoryCreatorFragment : Fragment() {
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val categoryCreatorView = inflater.inflate(R.layout.fragment_category_creator, container, false)

        val nameEditText: EditText = categoryCreatorView.findViewById(R.id.name)
        val pickColorButton: Button = categoryCreatorView.findViewById(R.id.pickColor)
        val colorCardView: CardView = categoryCreatorView.findViewById(R.id.color)
        val goBackButton: ImageButton = categoryCreatorView.findViewById(R.id.goBack)

        val createCategoryCardView: CardView = categoryCreatorView.findViewById(R.id.create)

        var currentlySelectedColor: Int = resources.getColor(R.color.black_tonal1)

        fun goBack() {
            val receiptCreatorFragment = ReceiptCreatorFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, receiptCreatorFragment)
                ?.commit()
        }

        pickColorButton.setOnClickListener {
            ColorPickerDialog
                .Builder(requireContext())
                .setTitle("")
                .setColorShape(ColorShape.SQAURE)
                .setDefaultColor(currentlySelectedColor)
                .setColorListener { color, _ ->
                    currentlySelectedColor = color

                    colorCardView.setCardBackgroundColor(currentlySelectedColor)
                }
                .show()
        }

        goBackButton.setOnClickListener { goBack() }

        createCategoryCardView.setOnClickListener {
            val name = nameEditText.text.toString()

            if (categoriesViewModel.getCategory(name) != null) {
                Toast.makeText(categoryCreatorView.context, "Categoria no valida! Una categoria amb aquest nom ja existeix.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            categoriesViewModel.addCategory(Category(name, currentlySelectedColor))

            goBack()
        }

        return categoryCreatorView
    }
}