package com.example.marty.shoppinglist.touch

interface ItemTouchHelperAdapter {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}