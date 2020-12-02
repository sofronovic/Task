package com.nsofronovic.task.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.nsofronovic.task.databinding.FragmentPostBinding
import com.nsofronovic.task.model.Post
import com.nsofronovic.task.service.ServiceManager
import com.nsofronovic.task.ui.adapters.PostAdapter
import com.nsofronovic.task.ui.navigation.NavigationManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import timber.log.Timber

class PostFragment : MviFragment<PostView, PostPresenter>(), PostView,
    SwipeRefreshLayout.OnRefreshListener {

    private val presenter: PostPresenter by inject()

    private lateinit var rvPosts: RecyclerView

    private lateinit var postAdapter: PostAdapter

    private val initialPublishSubject = PublishSubject.create<Unit>()

    private val swipeToRefreshPublishSubject = PublishSubject.create<Unit>()

    private val onPausePublishSubject = PublishSubject.create<Unit>()

    private val startServicePublishSubject = PublishSubject.create<Unit>()

    private var _binding: FragmentPostBinding? = null

    private val binding get() = _binding!!

    private val navigationManager: NavigationManager by inject()

    private val serviceManager: ServiceManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.swipeToRefresh.setOnRefreshListener(this)

        initPostAdapter()

        return view
    }

    override fun onResume() {
        super.onResume()
        initialPublishSubject.onNext(Unit)
    }

    override fun onPause() {
        super.onPause()
        onPausePublishSubject.onNext(Unit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun createPresenter(): PostPresenter = presenter

    override fun render(state: PostViewState) {
        Timber.d("Render partial state:${state.lastChangedState}")
        when (state.lastChangedState) {
            is PostPartialState.LoadingPosts -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.clNoInternet.root.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            }
            is PostPartialState.LoadedPosts -> {
                binding.progressBar.visibility = View.GONE
                state.posts?.let { data ->
                    setData(data)
                }
            }
            is PostPartialState.LoadedPostsFromDatabase -> {
                binding.progressBar.visibility = View.GONE
                state.posts?.let { data ->
                    setData(data)
                }
            }
            is PostPartialState.PostsSavedToDatabase -> {
                startServicePublishSubject.onNext(Unit)
            }
            is PostPartialState.StartDatabaseService -> {
                serviceManager.startService()
            }
            is PostPartialState.ErrorLoadingPosts -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.clNoInternet.root.visibility = View.GONE
            }
            is PostPartialState.PostClicked -> {
                navigationManager.openPostDetailsScreen()
            }
            is PostPartialState.LoadedPostsFromState -> {
                binding.progressBar.visibility = View.GONE
                state.posts?.let { posts ->
                    setData(posts)
                }
            }
            is PostPartialState.NoInternetConnection -> {
                if (state.posts.isNullOrEmpty()) {
                    binding.clNoInternet.root.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
            }
        }
    }

    override fun initialIntent(): Observable<Unit> {
        return initialPublishSubject
    }

    override fun swipeToRefreshIntent(): Observable<Unit> {
        return swipeToRefreshPublishSubject
    }

    override fun onPostClickIntent(): Observable<Int> {
        return postAdapter.postClickListener
    }

    override fun onRefresh() {
        binding.swipeToRefresh.isRefreshing = false
        swipeToRefreshPublishSubject.onNext(Unit)
    }

    override fun onPauseIntent(): Observable<Unit> {
        return onPausePublishSubject
    }

    override fun startServiceIntent(): Observable<Unit> {
        return startServicePublishSubject
    }

    private fun initPostAdapter() {
        postAdapter = PostAdapter(requireContext())
        rvPosts = binding.rvPostList
        rvPosts.layoutManager = LinearLayoutManager(requireContext())
        rvPosts.adapter = postAdapter
    }

    private fun setData(posts: List<Post>) {
        postAdapter.setData(posts)
    }
}
