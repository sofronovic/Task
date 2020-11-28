package com.nsofronovic.task.post

import com.nsofronovic.task.model.Post
import com.nsofronovic.task.ui.post.PostInteractor
import com.nsofronovic.task.ui.post.PostPartialState
import com.nsofronovic.task.ui.post.PostPresenter
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class PostPresenterTest {
    private lateinit var presenterTest: PostPresenter

    @RelaxedMockK
    private lateinit var interactor: PostInteractor

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockKAnnotations.init(this, relaxUnitFun = true)
        presenterTest = PostPresenter(interactor)
    }

    @Test
    fun shouldReturnPostListOnInitialIntent() {
        val posts = mutableListOf<Post>()
        posts.add(Post(1, 1, "test", "test"))

        every { interactor.generateInitialPartialState() } returns Observable.merge(
            Observable.just(PostPartialState.LoadingPostsPartialState),
            Observable.just(PostPartialState.LoadedPostsPartialState(posts)),
            Observable.just(PostPartialState.ErrorLoadingPostsPartialState)
        )

        val robot = PostViewRobot(presenterTest)
        robot.fireInitialIntent()
        robot.assertViewStateRendered(
            PostPartialState.LoadingPostsPartialState,
            PostPartialState.LoadedPostsPartialState(posts),
            PostPartialState.ErrorLoadingPostsPartialState
        )
    }
}