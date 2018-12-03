package com.example.marty.shoppinglist.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item")
data class Item(
        @PrimaryKey(autoGenerate = true) var itemId : Long?,
        @ColumnInfo(name = "itemname") var itemName: String,
        @ColumnInfo(name = "category") var catergory: Int,
        @ColumnInfo(name = "description") var description: String,
        @ColumnInfo(name = "price") var price: Float,
        @ColumnInfo(name = "alreadypurchased") var alreadyPurchased: Boolean,
        @ColumnInfo(name = "quantity") var quantity: Int
) : Serializable