package com.experienceconnect.qrscanner.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.experienceconnect.qrscanner.R
import com.experienceconnect.qrscanner.data.entities.Settings
import com.experienceconnect.qrscanner.databinding.MainFragmentBinding
import com.experienceconnect.qrscanner.ui.viewmodels.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val vm: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = MainFragmentBinding.inflate(inflater,container,false)
        binding.viewModel = vm
        binding.btnScan.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_mainFragment_to_scannerFragment)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<View>(R.id.btn_scan)?.requestFocus()
    }

}
