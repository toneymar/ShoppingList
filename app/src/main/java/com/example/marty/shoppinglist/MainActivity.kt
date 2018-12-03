package com.example.marty.shoppinglist

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.example.marty.shoppinglist.adapter.ListAdapter
import com.example.marty.shoppinglist.data.AppDatabase
import com.example.marty.shoppinglist.data.Item
import com.example.marty.shoppinglist.touch.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class MainActivity : AppCompatActivity(), InsertDialog.ItemHandler {

    companion object {
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    private lateinit var itemAdapter: ListAdapter
    private var editIndex: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {

            showAddItemDialog()
        }

        initRecyclerView()

        if(isFirstStart()) {
            MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.fab)
                    .setPrimaryText(getString(R.string.tutAddItem))
                    .setSecondaryText(getString(R.string.tutText))
                    .show()
            saveStart()
        }
    }

    private val KEY_FIRST = "KEY_FIRST"

    fun isFirstStart() : Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getBoolean(KEY_FIRST, true)
    }

    fun saveStart() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        editor.putBoolean(KEY_FIRST, false)
        editor.apply()
    }

    private fun initRecyclerView() {
        Thread {
            val items = AppDatabase.getInstance(this).itemDao().findAllItems()

            itemAdapter = ListAdapter(this@MainActivity, items)

            runOnUiThread {
                itemRecycler.layoutManager = LinearLayoutManager(this@MainActivity)
                itemRecycler.adapter = itemAdapter
                val callback = ItemTouchHelperCallback(itemAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(itemRecycler)
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteAll -> deleteAll()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddItemDialog() {
        InsertDialog().show(supportFragmentManager, "TAG_CREATE")
    }

    fun showEditItemDialog(itemToEdit: Item, idx: Int) {
        editIndex = idx
        val editItemDialog = InsertDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager,
                "EDITITEMDIALOG")
    }

    override fun itemCreated(item: Item) {
        Thread {
            val id = AppDatabase.getInstance (
                    this@MainActivity).itemDao().insertItem(item)

            item.itemId = id

            runOnUiThread {
                itemAdapter.addItem(item)
            }
        }.start()
    }

    override fun itemUpdated(item: Item) {
        val dbThread = Thread {
            AppDatabase.getInstance(this@MainActivity).itemDao().updateItem(item)

            runOnUiThread { itemAdapter.updateItem(item, editIndex) }
        }
        dbThread.start()
    }

    private fun deleteAll() : Boolean {
        Thread {
            AppDatabase.getInstance(this@MainActivity).itemDao().deleteAll()
            runOnUiThread {
                itemAdapter.removeAll()
            }
        }.start()
        return true
    }
}
