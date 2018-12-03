package com.example.marty.shoppinglist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marty.shoppinglist.MainActivity
import com.example.marty.shoppinglist.R
import com.example.marty.shoppinglist.data.AppDatabase
import com.example.marty.shoppinglist.data.Item
import com.example.marty.shoppinglist.touch.ItemTouchHelperAdapter
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>, ItemTouchHelperAdapter {

    var shoppingItems = mutableListOf<Item>()
    val context: Context

    constructor(context: Context, items: List<Item>) : super() {
        this.context = context
        this.shoppingItems.addAll(items)
    }

    constructor(context: Context) : super() {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
                R.layout.list_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shoppingItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = shoppingItems[position]

        holder.alreadyPurchased.isChecked = item.alreadyPurchased
        holder.description.text = item.description
        holder.txtPriceData.text = "$ ${item.price * item.quantity}"
        holder.itemTitle.text = item.itemName

        when {
            item.catergory == 0 -> holder.image.setImageResource(R.drawable.foodicon)
            item.catergory == 1 -> holder.image.setImageResource(R.drawable.clothesicon)
            item.catergory == 2 -> holder.image.setImageResource(R.drawable.staplesicon)
            item.catergory == 3 -> holder.image.setImageResource(R.drawable.electronicsiconpng)
        }

        setListeners(holder, item)

        setNumPicker(holder, item)
    }

    fun setListeners(holder: ViewHolder, item: Item) {
        holder.btnEdit.setOnClickListener {
            (context as MainActivity).showEditItemDialog(
                    item, holder.adapterPosition
            )
        }

        holder.alreadyPurchased.setOnClickListener {
            item.alreadyPurchased = holder.alreadyPurchased.isChecked
            Thread {
                AppDatabase.getInstance(context).itemDao().updateItem(item)
            }.start()
        }
    }

    private fun setNumPicker(holder: ViewHolder, item: Item) {
        holder.numPicker.minValue = 1
        holder.numPicker.maxValue = 20
        holder.numPicker.wrapSelectorWheel = false
        holder.numPicker.value = item.quantity

        holder.numPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            item.quantity = newVal
            Thread {
                AppDatabase.getInstance(context).itemDao().updateItem(item)

                (context as MainActivity).runOnUiThread {
                    holder.txtPriceData.text = "$ ${item.price * item.  quantity}"
                }
            }.start()


        }
    }

    fun deleteItem(adapterPosition: Int) {

        Thread {
            AppDatabase.getInstance(context).itemDao().deleteItem(
                    shoppingItems[adapterPosition]
            )

            shoppingItems.removeAt(adapterPosition)

            (context as MainActivity).runOnUiThread {
                notifyItemRemoved(adapterPosition)
            }
        }.start()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemTitle = itemView.itemTitle
        val txtPriceData = itemView.txtPriceData
        val description = itemView.txtDescription
        val alreadyPurchased = itemView.cbPurchased
        val btnEdit = itemView.btnEdit
        val image = itemView.itemImage
        val numPicker = itemView.numPicker
    }

    fun addItem(item: Item) {
        shoppingItems.add(0, item)

        notifyItemInserted(0)
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(shoppingItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun updateItem(item: Item, idx: Int) {
        shoppingItems[idx] = item
        notifyItemChanged(idx)
    }

    fun removeAll() {
        shoppingItems.clear()
        notifyDataSetChanged()
    }
}