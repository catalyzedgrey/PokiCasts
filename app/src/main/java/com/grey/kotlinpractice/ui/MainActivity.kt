package com.grey.kotlinpractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.ItunesService
import com.grey.kotlinpractice.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //region frag
        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.javaClass.getSimpleName()).commit()
        }


        var bottomNavigationView = view.bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    true
                }
                R.id.menu_search -> {
                    val fragment = SearchFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    true
                }
                R.id.menu_settings -> {
                    val fragment = SettingsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    true
                }
                else -> false
            }

            //endregion

        }

        initUi();
    }

    fun initUi() {
//        val searchBtn = findViewById<Button>(R.id.search_btn);
//        searchBtn.setOnClickListener {
//            beginSearch("waypoint");
//        }
    }


}
