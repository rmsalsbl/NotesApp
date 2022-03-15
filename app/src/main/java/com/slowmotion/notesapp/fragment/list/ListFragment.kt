package com.slowmotion.notesapp.fragment.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.slowmotion.notesapp.R
import com.slowmotion.notesapp.Utils.hideKeyboard
import com.slowmotion.notesapp.data.model.NoteData
import com.slowmotion.notesapp.data.viewModelsData.NoteViewModel
import com.slowmotion.notesapp.databinding.FragmentListBinding
import com.slowmotion.notesapp.fragment.Adapter.listAdapter
import com.slowmotion.notesapp.fragment.SharedViewModels
import jp.wasabeef.recyclerview.animators.LandingAnimator


class ListFragment : Fragment(), SearchView.OnQueryTextListener{

    private val mNoteViewModel : NoteViewModel by viewModels()
    private val adapter : listAdapter by lazy { listAdapter() }
    private val mSharedViewModels : SharedViewModels by viewModels()
    private var _listBinding : FragmentListBinding? = null
    private val listBinding get() = _listBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _listBinding = FragmentListBinding.inflate(inflater, container, false)
        listBinding.lifecycleOwner = this
        listBinding.mSharedViewModel = mSharedViewModels

        setUpRecylerView()

        mNoteViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            mSharedViewModels.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())
        return listBinding.root

    }


    private fun setUpRecylerView() {
        listBinding.rvList.adapter = adapter
        listBinding.rvList.layoutManager = StaggeredGridLayoutManager( 2, StaggeredGridLayoutManager.VERTICAL)
        listBinding.rvList.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(listBinding.rvList)
    }

    private fun swipeToDelete(recylerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){
                val deleteItem = adapter.dataList[viewHolder.adapterPosition]
                mNoteViewModel.deleteData(deleteItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoreDeleteData(viewHolder.itemView, deleteItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recylerView)
    }

    private fun restoreDeleteData(view: View, deleteItem: NoteData) {
        val snackbar = Snackbar.make(
            view, "Delete '${deleteItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Undo"){
            mNoteViewModel.insertData(deleteItem)
        }
        snackbar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete_all -> confirmRemoveAll()
            R.id.menu_priority_high -> mNoteViewModel.sortByHighPriority.observe(this, {
                adapter.setData(it)
            })
            R.id.menu_priority_low -> mNoteViewModel.sortByLowPriority.observe(this, {
                adapter.setData(it)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoveAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All?")
            .setMessage("Are you Sure want to remove all?")
            .setPositiveButton("Yes"){ _, _ ->
                mNoteViewModel.deleteAllData()
                Toast.makeText(requireContext(), "Successfully Remove All",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "$query"
        mNoteViewModel.searchDatabase(searchQuery).observe(
            this, {
                    list -> list.let { adapter.setData(it) }
            }
        )
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onDestroy() {
        _listBinding = null
        super.onDestroy()
    }


}
