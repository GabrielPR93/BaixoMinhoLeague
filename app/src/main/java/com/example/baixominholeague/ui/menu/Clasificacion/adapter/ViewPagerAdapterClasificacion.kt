package com.example.baixominholeague.ui.menu.Clasificacion.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.baixominholeague.ui.menu.Clasificacion.ClasificacionGeneralFragment
import com.example.baixominholeague.ui.menu.Inicio.NovedadesFragment


class ViewPagerAdapterClasificacion(
    fragmentActivity: FragmentActivity,
    val fragments: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getFragment(position: Int): Fragment {
        return fragments[position]
    }
}