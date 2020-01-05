package com.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rssclient.controllers.databinding.ActivityMainBinding

class MainActivity: BaseActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                if (LauncherFragment.javaClass.name == className) {
                    return LauncherFragment()
                }

                return super.instantiate(classLoader, className)
            }
        }

        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, LauncherFragment.javaClass.name)
        supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragmentContainerView.id, fragment, "launcher")
                .commitNowAllowingStateLoss()
    }
}