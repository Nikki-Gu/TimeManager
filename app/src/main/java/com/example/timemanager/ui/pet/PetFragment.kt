package com.example.timemanager.ui.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timemanager.R
import com.example.timemanager.databinding.FragmentPetBinding

class PetFragment : Fragment() {

    private lateinit var petViewModel: PetViewModel
    private var _binding: FragmentPetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        petViewModel =
            ViewModelProvider(this).get(PetViewModel::class.java)

        _binding = FragmentPetBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val toolbar = binding.homeToolbar

        val textView: TextView = binding.textPet
        petViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // toolbar设置
        toolbar.title = "宠物"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}