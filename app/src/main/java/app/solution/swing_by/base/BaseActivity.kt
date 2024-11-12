package app.solution.swing_by.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import app.solution.swing_by.common.ProgressView

abstract class BaseActivity<B : ViewBinding>(private val bindingFactory: (LayoutInflater) -> B) : AppCompatActivity() {
    protected lateinit var binding: B
    //    protected val binding: B by lazy { bindingFactory(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        beforeSetContentView()

        super.onCreate(savedInstanceState)

        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)

        initView()
        initListener()
    }

    override fun onStart() {
        super.onStart()

        refreshView()
    }

    protected open fun beforeSetContentView() {}
    protected open fun initView() {}
    protected open fun initListener() {}
    protected open fun refreshView() {}
}