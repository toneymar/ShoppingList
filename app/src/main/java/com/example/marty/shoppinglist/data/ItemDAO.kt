package com.example.marty.shoppinglist.data

import android.arch.persistence.room.*

@Dao
interface ItemDAO {

    @Query("SELECT * FROM item")
    fun findAllItems(): List<Item>

    @Query("DELETE FROM item")
    fun deleteAll()

    @Insert
    fun insertItem(item: Item) : Long

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(item: Item)
}