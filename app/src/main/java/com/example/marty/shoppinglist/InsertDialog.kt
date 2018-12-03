package com.example.marty.shoppinglist

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import com.example.marty.shoppinglist.data.Item
import kotlinx.android.synthetic.main.layout_insert.view.*

class InsertDialog : DialogFragment() {

    interface ItemHandler {
        fun itemCreated(item: Item)
        fun itemUpdated(item: Item)
    }

    private lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                    getString(R.string.runtimeException))
        }
    }

    private lateinit var etName: EditText
    private lateinit var category: Spinner
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var cbPurchased: CheckBox

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.newItemTitle))

        val rootView = requireActivity().layoutInflater.inflate(R.layout.layout_insert, null)
        etName = rootView.etName
        category = rootView.spinner
        etDescription = rootView.etDescription
        etPrice = rootView.etPrice
        cbPurchased = rootView.cbPurchasedIns
        builder.setView(rootView)

        createSpinner()

        val arguments = this.arguments
        if (arguments != null && arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
            val itemSel = arguments.getSerializable(
                    MainActivity.KEY_ITEM_TO_EDIT
            ) as Item
            etName.setText(itemSel.itemName)
            category.setSelection(itemSel.catergory)
            etDescription.setText(itemSel.description)
            etPrice.setText(itemSel.price.toString())
            cbPurchased.isChecked = itemSel.alreadyPurchased

            builder.setTitle(getString(R.string.editItemTitle))
        }

        builder.setPositiveButton(getString(R.string.btnOk)) {
            dialog, witch -> //empty
        }

        return builder.create()
    }

    private fun createSpinner() {
        val spinner: Spinner = category
        ArrayAdapter.createFromResource(
                context,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etName.text.isNotEmpty() && etPrice.text.isNotEmpty() && etDescription.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null && arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog.dismiss()
            } else if (etName.text.isEmpty()) {
                etName.error = getString(R.string.fieldCannotBeEmpty)
            }
            else if (etPrice.text.isEmpty()) {
                etPrice.error = getString(R.string.fieldCannotBeEmpty)
            }
            else if (etDescription.text.isEmpty()) {
                etDescription.error = getString(R.string.fieldCannotBeEmpty)
            }
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(
                Item(
                        null, etName.text.toString(), category.selectedItemPosition,
                        etDescription.text.toString(), etPrice.text.toString().toFloat(), cbPurchased.isChecked, 1)
        )
    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
                MainActivity.KEY_ITEM_TO_EDIT
        ) as Item
        itemToEdit.itemName = etName.text.toString()
        itemToEdit.catergory = category.selectedItemPosition
        itemToEdit.price = etPrice.text.toString().toFloat()
        itemToEdit.description = etDescription.text.toString()
        itemToEdit.alreadyPurchased = cbPurchased.isChecked

        itemHandler.itemUpdated(itemToEdit)
    }
}