package com.aglushkov.wordteacher.features.definitions.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.modelcore.resource.Resource
import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.databinding.FragmentDefinitionsBinding
import com.aglushkov.wordteacher.features.definitions.vm.DefinitionsVM
import com.aglushkov.modelcore_ui.view.bind
import com.aglushkov.wordteacher.features.definitions.adapter.DefinitionsAdapter
import com.aglushkov.wordteacher.features.definitions.adapter.DefinitionsBinder
import com.aglushkov.wordteacher.features.definitions.vm.DefinitionsDisplayMode

class DefinitionsFragment: Fragment() {
    private lateinit var vm: DefinitionsVM
    private var binding: FragmentDefinitionsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        vm = ViewModelProviders.of(this, SavedStateViewModelFactory(requireActivity().application, this))
                .get(DefinitionsVM::class.java)
        Configuration.SCREENLAYOUT_SIZE_LARGE
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwnerLiveData.observe(this, Observer {
            if (it == null) return@Observer
            onViewLifecycleOwnerReady(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDefinitionsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {
        val binding = this.binding!!

        binding.list.apply {
            layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        }

        binding.loadingStatusView.setOnTryAgainListener {
            vm.onTryAgainClicked()
        }

        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                vm.onWordSubmitted(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun onViewLifecycleOwnerReady(viewLifecycleOwner: LifecycleOwner) {
        vm.definitions.observe(viewLifecycleOwner, Observer {
            showDefinitions(it)
        })
    }

    private fun showDefinitions(it: Resource<List<BaseViewItem<*>>>) {
        val binding = this.binding!!

        val errorText = vm.getErrorText(it)
        it.bind(binding.loadingStatusView, errorText)

        updateListAdapter(it)
    }

    private fun updateListAdapter(it: Resource<List<BaseViewItem<*>>>) {
        val binding = this.binding!!

        if (binding.list.adapter != null) {
            (binding.list.adapter as DefinitionsAdapter).submitList(it.data())
        } else {
            val binder = DefinitionsBinder()
            binder.listener = object : DefinitionsBinder.Listener {
                override fun onDisplayModeChanged(mode: DefinitionsDisplayMode) {
                    vm.onDisplayModeChanged(mode)
                }
            }

            binding.list.adapter = DefinitionsAdapter(binder).apply {
                submitList(it.data())
            }
        }
    }
}