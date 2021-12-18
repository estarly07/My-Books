package com.example.mybooks.View.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.mybooks.R

class AdapterAbout(val contents: Array<String>, val titles: Array<String>, val images: Array<Int>) :
    PagerAdapter() {
    override fun getCount(): Int = contents.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view =
            LayoutInflater.from(container.context).inflate(R.layout.about_item, container, false)
        val title = view.findViewById<TextView>(R.id.title)
        title.text = (titles[position])
        val content = view.findViewById<TextView>(R.id.content)
        content.text = (contents[position])
        val image = view.findViewById<ImageView>(R.id.image)
        image.setBackgroundResource(images[position])

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}