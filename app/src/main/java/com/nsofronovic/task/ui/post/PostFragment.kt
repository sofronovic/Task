package com.nsofronovic.task.ui.post

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.nsofronovic.task.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class PostFragment : MviFragment<PostView, PostPresenter>(), PostView {

    private val initialPublishSubject = PublishSubject.create<Unit>()

    private val presenter: PostPresenter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_post, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        initialPublishSubject.onNext(Unit)
    }

    override fun createPresenter(): PostPresenter = presenter

    override fun render(state: PostViewState) {
        when (state.lastChangedState) {
            is PostPartialState.InitialPartialState -> {

            }
        }
    }

    override fun initialIntent(): Observable<Unit> {
        return initialPublishSubject
    }
}