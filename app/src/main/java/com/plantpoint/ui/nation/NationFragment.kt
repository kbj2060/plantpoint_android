package com.plantpoint.ui.nation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.plantpoint.R


class NationFragment : Fragment() {

    private lateinit var nationViewModel: NationViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        nationViewModel =
                ViewModelProvider(this).get(NationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_nation, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        nationViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}