package com.example.skcamotes

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CustomCarouselLayoutManager(context: Context) :
    LinearLayoutManager(context, HORIZONTAL, false) {

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
        scaleChildren()
        return scrolled
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        scaleChildren()
    }

    private fun scaleChildren() {
        val midPoint = width / 2f
        val d0 = 0f
        val d1 = 0.9f * midPoint
        val s0 = 1f
        val s1 = 0.8f

        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childMidPoint = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f
            val d = abs(midPoint - childMidPoint)
            val scale = if (d < d1) {
                s0 + (s1 - s0) * (d - d0) / (d1 - d0)
            } else {
                s1
            }
            child.scaleX = scale
            child.scaleY = scale
        }
    }
}
