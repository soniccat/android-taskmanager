package com.aglushkov.wordteacher.features.definitions.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import com.aglushkov.wordteacher.features.definitions.vm.DefinitionsVM

class DefinitionsFragment: Fragment() {
    private val vm by viewModels<DefinitionsVM>(
        factoryProducer = { SavedStateViewModelFactory(requireActivity().application, this) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm // touch
        viewLifecycleOwnerLiveData.observe(this, Observer {
            if (it == null) return@Observer
            onViewLifecycleOwnerReady(it)
        })
    }

    private fun onViewLifecycleOwnerReady(viewLifecycleOwner: LifecycleOwner) {

    }
}