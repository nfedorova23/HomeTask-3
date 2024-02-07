package com.nfedorova.hometask3


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nfedorova.hometask3.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactAdapter: ContactAdapter
    private var isDeleted = false
    private var contactList = mutableListOf<Contact>()
    private  var contactService = ContactService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        contactList.addAll(contactService.getRandomContactList())
        makeAdapter()

        binding.contactRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.addContactFAB.hide()
                } else {
                    binding.addContactFAB.show()
                }
            }
        })

        binding.addContactFAB.setOnClickListener {
            showAddContactDialog()
        }
        binding.regButton.setOnClickListener {
            delete()
        }
        binding.cancelButton.setOnClickListener {
            reset()
        }
    }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_contact, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteBtn -> {
                isDeleted = !isDeleted
                visibilityFAB(isDeleted)
                if (!isDeleted) {
                    contactList.forEach { it.isChecked = false }
                }
                val newList =
                    contactList.map { it.copy(isDeleted = isDeleted) }
                val diffCallback = ContactDiffUtilCallback(contactList, newList)
                val result = DiffUtil.calculateDiff(diffCallback)
                contactList.forEach { it.isChecked = !isDeleted }
                contactAdapter.submit(newList)
                result.dispatchUpdatesTo(contactAdapter)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun visibilityFAB(visible: Boolean){
        binding.addContactFAB.visibility =
            if (visible)
                View.GONE
            else
                View.VISIBLE
        binding.regButton.visibility =
            if (visible)
                View.VISIBLE
            else
                View.GONE
        binding.cancelButton.visibility =
            if (visible)
                View.VISIBLE
            else
                View.GONE
    }

    private fun reset() {
        val newList = contactList.map { it.copy(isChecked = false) }
        val diffCallback = ContactDiffUtilCallback(contactList, newList)
        val result = DiffUtil.calculateDiff(diffCallback)
        contactList.forEach { it.isChecked = false }
        contactAdapter.submit(newList)
        result.dispatchUpdatesTo(contactAdapter)
    }

    private fun makeAdapter() {
        contactAdapter = ContactAdapter(this, this, contactList)
        binding.contactRecyclerView.adapter = contactAdapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.contactRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    fun showEditContactDialog(position: Int){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialog = inflater.inflate(R.layout.add_contact, null)
        val firstNameView = dialog.findViewById<EditText>(R.id.contactFirstNameEditText)
        val lastNameView = dialog.findViewById<EditText>(R.id.contactLastNameEditText)
        val contactNumber = dialog.findViewById<EditText>(R.id.contactPhoneNumberEditText)

        val updateContact = contactList[position].copy()
        firstNameView.setText(updateContact.firstName)
        lastNameView.setText(updateContact.lastName)
        contactNumber.setText(updateContact.phoneNumber)

        with(builder) {
            setPositiveButton("Done") { _, _ ->
                val updateFirstName = firstNameView.text.toString()
                val updateLastName = lastNameView.text.toString()
                val updateContactNumber = contactNumber.text.toString()

                if (updateContact.firstName != updateFirstName || updateContact.lastName != updateLastName ||
                    updateContact.phoneNumber != updateContactNumber) {
                    contactList[position].firstName = updateFirstName
                    contactList[position].lastName = updateLastName
                    contactList[position].phoneNumber = updateContactNumber
                    contactAdapter.update(position)
                    val newList = contactList.toList()
                    val diffCallback = ContactDiffUtilCallback(contactList, newList)
                    val result = DiffUtil.calculateDiff(diffCallback)
                    result.dispatchUpdatesTo(contactAdapter)
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialog)
            show()
        }
    }

    private fun showAddContactDialog() {
        val inflater = layoutInflater
        val dialog = inflater.inflate(R.layout.add_contact, null)
        val builder = AlertDialog.Builder(this)

        val firstNameView = dialog.findViewById<EditText>(R.id.contactFirstNameEditText)
        val lastNameView = dialog.findViewById<EditText>(R.id.contactLastNameEditText)
        val contactNumber = dialog.findViewById<EditText>(R.id.contactPhoneNumberEditText)
        with(builder) {
            setPositiveButton("Done") { _, _ ->
                val firstName = firstNameView.text.toString()
                val lastName = lastNameView.text.toString()
                val phone = contactNumber.text.toString()
                val newId = contactList.size + 1
                val newItem = Contact(newId, firstName, lastName, phone)

                val newList = mutableListOf<Contact>().apply {
                    addAll(contactList)
                    add(newItem)
                }

                val diffCallback = ContactDiffUtilCallback(contactList, newList)
                val result = DiffUtil.calculateDiff(diffCallback)
                contactList.add(newItem)
                contactAdapter.submit(newList)
                result.dispatchUpdatesTo(contactAdapter)

                val newPosition = contactAdapter.itemCount - 1

                binding.contactRecyclerView.smoothScrollToPosition(newPosition)
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialog)
            show()
        }
    }

    private fun delete() {
        for (item in contactList.indices.reversed()) {
            if (contactList[item].isChecked) {
                contactList.removeAt(item)
                contactAdapter.notifyItemRemoved(item)
            }
        }

    }
}