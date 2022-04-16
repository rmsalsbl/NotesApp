package com.slowmotion.notesapp.fragment.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Update
import com.slowmotion.notesapp.R
import com.slowmotion.notesapp.data.model.NoteData
import com.slowmotion.notesapp.data.viewModelsData.NoteViewModel
import com.slowmotion.notesapp.databinding.FragmentUpdateBinding
import com.slowmotion.notesapp.fragment.SharedViewModels

class UpdateFragment : androidx.fragment.app.Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModels: SharedViewModels by viewModels()
    private val mNotesViewModel: NoteViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args
        binding.spUpdate.onItemSelectedListener = mSharedViewModels.listener
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemRemoval() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete '${args.currentItem.title}' ?")
            .setMessage("Are you sure want to remove '${args.currentItem.title}' ?")
            .setPositiveButton("Yes"){_, _ ->
                mNotesViewModel.deleteData(args.currentItem)
                Toast.makeText(
                    requireContext(), "Succesfully Removed : ${args.currentItem.title}",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton("No") {_, _ ->}
            .create()
            .show()

    }

    private fun updateItem() {
        val title = binding.etUptitle.text.toString()
        val description = binding.etDescUpdate.text.toString()
        val getPriority = binding.spUpdate.selectedItem.toString()

        val validation = mSharedViewModels.verifyDataFromUser(title, description)
        if (validation){
            val updateItem = NoteData(
                args.currentItem.id,
                title,
                mSharedViewModels.parsePriority(getPriority),
                description
            )
            mNotesViewModel.updateData(updateItem)
            Toast.makeText(requireContext(), "Berhasil di update", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(), "Tolong isi semua persyaratan", Toast.LENGTH_SHORT)
                .show()
        }

    }

}