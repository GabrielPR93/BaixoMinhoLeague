package com.example.baixominholeague.ui.menu.Inicio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.baixominholeague.R
import com.example.baixominholeague.databinding.FragmentInicioBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class InicioFragment : Fragment(){

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private var adapter : ViewPagerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val view = binding.root
        viewPager()

        return view
    }

        fun viewPager(){
        val viewPager: ViewPager2 = binding.viewPager
        val tabs: TabLayout = binding.tabs

        val fragments = listOf(EventosFragment(), NovedadesFragment())
        adapter = ViewPagerAdapter(requireActivity(), fragments)
        viewPager.adapter = adapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.eventos)
                1 -> tab.text = getString(R.string.noticias)
            }
        }.attach()
        binding.viewPager.currentItem=0
    }
}