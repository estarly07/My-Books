package com.example.mybooks.View.allBook

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Models.Content
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterContent

class AdapterAllBook : PagerAdapter() {


    private var themes      : List<ThemeEntity> = listOf()
    private var mapContent  = mutableMapOf<String, List<Content>>()


    fun setThemes(themes: List<ThemeEntity>) {
        this.themes         = themes
    }

    fun setContent(content: MutableMap<String, List<Content>>) {
        this.mapContent     = content
    }


    override fun getCount(): Int = themes.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.pagina_allbook, container, false)
        val txtTheme  = view.findViewById<TextView>(R.id.txtNameThemeContent)
        txtTheme.text = themes[position].name

        val adapter   = AdapterContent()
        val recyclerContent: RecyclerView = view.findViewById(R.id.reciclerContent)

        adapter.setList(mapContent[themes[position].name]!!)
        if (mapContent[themes[position].name]!!.isNotEmpty()) {
            recyclerContent.layoutManager =
                LinearLayoutManager(container.context, LinearLayoutManager.VERTICAL, false)
            recyclerContent.setHasFixedSize(true)
            recyclerContent.adapter = adapter
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            recyclerContent.setOnScrollChangeListener { view, x, y, oldX, oldY ->
//                AllBookActivity.getHidePag().invoke(y < oldY)
//            }
//        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view: View = `object` as View
        container.removeView(view)
    }
}