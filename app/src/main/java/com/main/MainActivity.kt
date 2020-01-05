package com.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rssclient.controllers.databinding.ActivityMainBinding
import com.rssclient.rssfeed.view.RssMainFragment

class MainActivity: BaseActivity(), LauncherFragment.Listener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                if (LauncherFragment::class.java.name == className) {
                    return LauncherFragment()
                }
                if (RssMainFragment::class.java.name == className) {
                    return RssMainFragment()
                }

                return super.instantiate(classLoader, className)
            }
        }

        if (supportFragmentManager.findFragmentByTag("launcher") == null) {
            val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, LauncherFragment::class.java.name)
            supportFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(binding.fragmentContainerView.id, fragment, "launcher")
                    .commitAllowingStateLoss()
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment is LauncherFragment) {
            fragment.listener = this
        }
    }

    override fun onOpenRssPressed() {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, RssMainFragment::class.java.name)
        supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(binding.fragmentContainerView.id, fragment, "rssMain")
                .addToBackStack("rssMain")
                .commitAllowingStateLoss()
    }
}