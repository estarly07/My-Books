package com.example.mybooks.View.allBook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.mybooks.R

class AdapterAllBook : PagerAdapter() {


    override fun getCount(): Int = 2

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
        val view=LayoutInflater.from(container.context).inflate(R.layout.pagina_allbook,container,false)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        val view:View=`object` as View
        container.removeView(view)
    }
}