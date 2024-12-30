package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var btnLeftArrow: ImageButton
    private lateinit var btnRightArrow: ImageButton
    private lateinit var layoutManager: CustomCarouselLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize the RecyclerView and buttons
        carouselRecyclerView = view.findViewById(R.id.carouselRecyclerView)
        btnLeftArrow = view.findViewById(R.id.btnLeftArrow)
        btnRightArrow = view.findViewById(R.id.btnRightArrow)

        // Define your image list
        val imageList = listOf(
            R.drawable.img_saw,
            R.drawable.img_chairr,
            R.drawable.img_notebook
        )

        // Set up the RecyclerView with the custom layout manager and adapter
        layoutManager = CustomCarouselLayoutManager(requireContext())
        carouselRecyclerView.layoutManager = layoutManager

        val adapter = CarouselAdapter(imageList)
        carouselRecyclerView.adapter = adapter

        // Scroll to the middle image
        val middlePosition = imageList.size / 2
        carouselRecyclerView.scrollToPosition(middlePosition)

        // Update arrow visibility on creation
        updateArrowVisibility()

        // Arrow button click listeners
        btnLeftArrow.setOnClickListener {
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            if (currentPosition > 0) {
                carouselRecyclerView.smoothScrollToPosition(currentPosition - 1)
                updateArrowVisibility(currentPosition - 1, imageList.size)
            }
        }

        btnRightArrow.setOnClickListener {
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            if (currentPosition < adapter.itemCount - 1) {
                carouselRecyclerView.smoothScrollToPosition(currentPosition + 1)
                updateArrowVisibility(currentPosition + 1, imageList.size)
            }
        }

        // Listen for scroll changes to update arrow visibility
        carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                updateArrowVisibility()
            }
        })

        return view
    }

    private fun updateArrowVisibility(currentPosition: Int? = null, itemCount: Int? = null) {
        val position = currentPosition ?: layoutManager.findFirstVisibleItemPosition()
        val totalItems = itemCount ?: (carouselRecyclerView.adapter?.itemCount ?: 0)

        btnLeftArrow.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        btnRightArrow.visibility = if (position < totalItems - 1) View.VISIBLE else View.INVISIBLE
    }
}
