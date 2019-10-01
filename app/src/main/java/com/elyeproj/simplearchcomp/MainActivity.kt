package com.elyeproj.simplearchcomp

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.my_container
import kotlinx.android.synthetic.main.activity_main.my_text

class MainActivity : AppCompatActivity() {
    private val viewModel: MyViewModel by lazy {
        MyViewModel.getViewModel(application, this)
    }

    private val changeObserver =
            Observer<Int> { value -> value?.let { incrementCount(value) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.changeRegister.observe(this, changeObserver)
        my_container.setOnClickListener { viewModel.increment() }
    }

    private fun incrementCount(value: Int) {
        my_text.text = (value).toString()
    }
}

class MyViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        const val COUNT_KEY = "CountKey"
        fun getViewModel(application: Application, fragmentActivity: FragmentActivity): MyViewModel {
            val defaultState = Bundle().apply { putInt(COUNT_KEY, 0) }
            val factory = SavedStateViewModelFactory(application, fragmentActivity, defaultState)
            return ViewModelProviders.of(fragmentActivity, factory).get(MyViewModel::class.java)
        }
    }

    private val changeNotifier: MutableLiveData<Int> = savedStateHandle.getLiveData(COUNT_KEY)
    val changeRegister: LiveData<Int>
        get() = changeNotifier

    fun increment() {
        var count = changeNotifier.value ?: 0
        savedStateHandle.set(COUNT_KEY, ++count)
    }
}
