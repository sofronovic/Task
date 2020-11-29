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
import com.nsofronovic.task.ui.adapters.PostAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import timber.log.Timber

class PostFragment : MviFragment<PostView, PostPresenter>(), PostView, SwipeRefreshLayout.OnRefreshListener {

    private val presenter: PostPresenter by inject()

    private lateinit var rvPosts: RecyclerView

    private lateinit var postAdapter: PostAdapter

    private val initialPublishSubject = PublishSubject.create<Unit>()

    private val swipeToRefreshPublishSubject = PublishSubject.create<Unit>()

    private var _binding: FragmentPostBinding? = null

    private val binding get() = _binding!!

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
            is PostPartialState.ErrorLoadingPosts -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    override fun initialIntent(): Observable<Unit> {
        return initialPublishSubject
    }

    override fun swipeToRefreshIntent(): Observable<Unit> {
        return swipeToRefreshPublishSubject
    }

    override fun onRefresh() {
        binding.swipeToRefresh.isRefreshing = false
        swipeToRefreshPublishSubject.onNext(Unit)
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
