package com.doaamosallam.trendysteps

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BasicActivity<T : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var bindingApp: T
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        bindingApp = DataBindingUtil.setContentView(this, getLayoutResId())
    }

    abstract fun getLayoutResId(): Int

}