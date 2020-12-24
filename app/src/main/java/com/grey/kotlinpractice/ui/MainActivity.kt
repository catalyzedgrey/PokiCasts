package com.grey.kotlinpractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.grey.kotlinpractice.R
//import com.grey.kotlinpractice.adapter.PodcastHomeAdapter
import com.grey.kotlinpractice.data.ItunesService
import com.grey.kotlinpractice.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity(), HomeFragment.ItemClickedListener {

    lateinit var binding: ActivityMainBinding
    val homeFragment= HomeFragment()
    val searchFragment= SearchFragment()
    val settingsFragment= SettingsFragment()
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //region frag
        if (savedInstanceState == null) {
            //val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, homeFragment, homeFragment.javaClass.getSimpleName()).commit()
        }


        val bottomNavigationView = view.bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    //val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, homeFragment, homeFragment.javaClass.getSimpleName())
                        .commit()
                    true
                }
                R.id.menu_search -> {
                    //val fragment = SearchFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, searchFragment, searchFragment.javaClass.getSimpleName())
                        .commit()
                    true
                }
                R.id.menu_settings -> {
                    //val fragment = SettingsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, settingsFragment, settingsFragment.javaClass.getSimpleName())
                        .commit()
                    true
                }
                else -> false
            }

            //endregion

        }

        //initUi();
    }


    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is HomeFragment) {
            fragment.setOnItemClickedListener(this)
        }
    }
    override fun sendPodcastIndex(podcastPosIndex: String) {
        //TODO("Not yet implemented")
        val fragment = EpisodeFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.getSimpleName()).addToBackStack("tag")
            .commit()
        fragment.updateText(podcastPosIndex)
    }


    fun initUi() {
//        val searchBtn = findViewById<Button>(R.id.search_btn);
//        searchBtn.setOnClickListener {
//            beginSearch("waypoint");
//        }
    }


}


