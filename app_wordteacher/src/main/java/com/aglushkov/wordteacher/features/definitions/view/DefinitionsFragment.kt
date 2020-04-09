package com.aglushkov.wordteacher.features.definitions.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.aglushkov.wordteacher.databinding.FragmentDefinitionsBinding
import com.aglushkov.wordteacher.features.definitions.vm.DefinitionsVM
import com.aglushkov.modelcore.view.bind

class DefinitionsFragment: Fragment() {
    private lateinit var vm: DefinitionsVM
    private var binding: FragmentDefinitionsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this, SavedStateViewModelFactory(requireActivity().application, this))
                .get(DefinitionsVM::class.java)

        viewLifecycleOwnerLiveData.observe(this, Observer {
            if (it == null) return@Observer
            onViewLifecycleOwnerReady(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDefinitionsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun onViewLifecycleOwnerReady(viewLifecycleOwner: LifecycleOwner) {
        vm.definitions.observe(viewLifecycleOwner, Observer {
            val errorText = vm.getErrorText(it)
            it.bind(binding!!.loadingStatusView, errorText)
        })
    }
}