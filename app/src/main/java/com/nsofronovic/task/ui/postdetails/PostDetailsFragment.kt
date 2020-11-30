package com.nsofronovic.task.ui.postdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.nsofronovic.task.databinding.FragmentPostDetailsBinding
import com.nsofronovic.task.model.PostDetailsData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import timber.log.Timber

class PostDetailsFragment : MviFragment<PostDetailsView, PostDetailsPresenter>(), PostDetailsView {

    private val presenter: PostDetailsPresenter by inject()

    private val initialPublishSubject = PublishSubject.create<Unit>()

    private var _binding: FragmentPostDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailsBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        return view
    }

    override fun onResume() {
        super.onResume()
        initialPublishSubject.onNext(Unit)
    }

    override fun createPresenter(): PostDetailsPresenter = presenter

    override fun render(state: PostDetailsViewState) {
        Timber.d("Render partial state:${state.lastChangedState}")
        when (state.lastChangedState) {
            is PostDetailsPartialState.LoadingUser -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE
            }
            is PostDetailsPartialState.LoadedPostDetails -> {
                binding.progressBar.visibility = View.GONE
                state.postDetailsData?.let { postDetailsData ->
                    setData(postDetailsData)
                }
            }
            is PostDetailsPartialState.ErrorLoadUser -> {
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    override fun initialIntent(): Observable<Unit> {
        return initialPublishSubject
    }

    private fun setData(postDetails: PostDetailsData) {
        binding.tvTitle.text = postDetails.title
        binding.tvBody.text = postDetails.body
        binding.tvAuthorName.text = postDetails.name
        binding.tvAuthorEmail.text = postDetails.email
    }
}
