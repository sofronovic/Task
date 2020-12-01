package com.nsofronovic.task.ui.postdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.clicks
import com.nsofronovic.task.R
import com.nsofronovic.task.databinding.FragmentPostDetailsBinding
import com.nsofronovic.task.model.PostDetailsData
import com.nsofronovic.task.ui.navigation.NavigationManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PostDetailsFragment : MviFragment<PostDetailsView, PostDetailsPresenter>(), PostDetailsView {

    private val presenter: PostDetailsPresenter by inject()

    private val initialPublishSubject = PublishSubject.create<Unit>()

    private val deletePostPublishSubject = PublishSubject.create<Unit>()

    private var _binding: FragmentPostDetailsBinding? = null

    private val binding get() = _binding!!

    private val navigationManager: NavigationManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailsBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDeletePost.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe(deletePostPublishSubject)
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
                binding.clNoInternet.root.visibility = View.GONE
            }
            is PostDetailsPartialState.LoadedPostDetails -> {
                binding.progressBar.visibility = View.GONE
                binding.btnDeletePost.visibility = View.VISIBLE
                state.postDetailsData?.let { postDetailsData ->
                    setData(postDetailsData)
                }
            }
            is PostDetailsPartialState.ErrorLoadUser -> {
                binding.tvError.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.btnDeletePost.visibility = View.GONE
                binding.clNoInternet.root.visibility = View.GONE
            }
            is PostDetailsPartialState.NoInternetConnection -> {
                binding.tvError.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.clContentView.visibility = View.GONE
                binding.clNoInternet.root.visibility = View.VISIBLE
            }
            is PostDetailsPartialState.DeletedPostFromDatabase -> {
                navigationManager.goBack()
            }
        }
    }

    override fun initialIntent(): Observable<Unit> {
        return initialPublishSubject
    }

    override fun deletePostIntent(): Observable<Unit> {
        return deletePostPublishSubject
    }

    private fun setData(postDetails: PostDetailsData) {
        binding.tvTitle.text = postDetails.title
        binding.tvBody.text = postDetails.body
        binding.tvAuthorName.text = postDetails.name
        binding.tvAuthorEmail.text = postDetails.email
    }
}
