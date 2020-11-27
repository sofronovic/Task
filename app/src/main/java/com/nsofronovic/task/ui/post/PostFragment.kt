package com.nsofronovic.task.ui.post

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nsofronovic.task.R
import io.reactivex.Observable
import io.reactivex.Observer
import java.util.concurrent.TimeUnit

class PostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_post, container, false)

        return view
    }
}