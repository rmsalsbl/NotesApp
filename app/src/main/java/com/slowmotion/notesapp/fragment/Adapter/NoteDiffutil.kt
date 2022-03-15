package com.slowmotion.notesapp.fragment.Adapter

import android.provider.ContactsContract
import androidx.recyclerview.widget.DiffUtil
import com.slowmotion.notesapp.data.model.NoteData

class NoteDiffutil (
    private val oldList: List<NoteData>,
    private val newist : List<NoteData>,
        ): DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newist.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newist[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newist[newItemPosition].id
                && oldList[oldItemPosition].title == newist[newItemPosition].title
                && oldList[oldItemPosition].description == newist[newItemPosition].description
                && oldList[oldItemPosition].priority == newist[newItemPosition].priority
    }
        }