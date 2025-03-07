package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CancelBottomSheetDialog(
    private val onConfirmCancel: () -> Unit
) : BottomSheetDialogFragment() {


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_cancel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnConfirmCancel: Button = view.findViewById(R.id.btnConfirmCancel)
        val btnDismissCancel: Button = view.findViewById(R.id.btnDismissCancel)

        btnConfirmCancel.setOnClickListener {
            onConfirmCancel()
            dismiss()
        }

        btnDismissCancel.setOnClickListener {
            dismiss()
        }
    }
}

