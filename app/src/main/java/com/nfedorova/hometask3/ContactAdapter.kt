package com.nfedorova.hometask3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nfedorova.hometask3.databinding.ContactItemBinding

class ContactAdapter(private val mainActivity: MainActivity,
                     private val context: Context,
                     private val contactList: MutableList<Contact>
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContactViewHolder(binding)
    }
    override fun getItemCount(): Int = contactList.size
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bind(contact, contact.isDeleted)
        holder.itemView.setOnClickListener {
            mainActivity.showEditContactDialog(position)
        }
        holder.itemView.findViewById<CheckBox>(R.id.contactCheck).apply {
            this.isChecked = contact.isChecked
            setOnClickListener { _ ->
                contact.isChecked = !contact.isChecked
            }
        }
    }

    fun submit(newList: List<Contact>) {
        val diffCallback = ContactDiffUtilCallback(contactList, newList)
        val result = DiffUtil.calculateDiff(diffCallback)
        contactList.clear()
        contactList.addAll(newList)
        result.dispatchUpdatesTo(this)
    }

    fun update(position: Int) {
        notifyItemChanged(position)
    }

    class ContactViewHolder (contactItemBinding: ContactItemBinding) :
    RecyclerView.ViewHolder(contactItemBinding.root){
        private val binding = contactItemBinding
        fun bind (item: Contact, isDeleted: Boolean){
            with(binding) {
                contactPhoneNumberTextView.text = item.phoneNumber
                contactFirstNameTextView.text = item.firstName
                contactLastNameTextView.text = item.lastName
                contactCheck.visibility =
                    if (isDeleted)
                        View.VISIBLE
                    else
                        View.GONE
            }
        }
    }
}

class ContactDiffUtilCallback(
    private val oldList: List<Contact>,
    private val newList: List<Contact>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
