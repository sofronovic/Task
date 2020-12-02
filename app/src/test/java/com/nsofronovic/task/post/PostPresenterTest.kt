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
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PostPresenterTest {
    private lateinit var presenter: PostPresenter

    @RelaxedMockK
    private lateinit var interactor: PostInteractor

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockKAnnotations.init(this, relaxUnitFun = true)
        presenter = PostPresenter(interactor)
    }

    @Test
    fun `should return post list on initial intent`() {
        val testPosts = mutableListOf<Post>()
        testPosts.add(Post(1, 1, "test", "test"))

        every {
            interactor.generateInitialPartialState()
        } returns Observable.merge(
            Observable.just(PostPartialState.LoadingPosts),
            Observable.just(PostPartialState.LoadedPosts(testPosts)),
            Observable.just(PostPartialState.PostsSavedToDatabase)
        )

        val robot = PostViewRobot(presenter)
        robot.fireInitialIntent()
        robot.assertViewStateRendered(
            PostPartialState.LoadingPosts,
            PostPartialState.LoadedPosts(testPosts),
            PostPartialState.PostsSavedToDatabase
        )

        Assert.assertSame(testPosts, presenter.currentState.posts)
    }

    @Test
    fun `should return post list on initial intent from database`() {
        val testPosts = mutableListOf<Post>()
        testPosts.add(Post(1, 1, "test", "test"))

        every {
            interactor.generateInitialPartialState()
        } returns Observable.merge(
            Observable.just(PostPartialState.LoadingPosts),
            Observable.just(PostPartialState.LoadedPostsFromDatabase(testPosts))
        )


        val robot = PostViewRobot(presenter)
        robot.fireInitialIntent()
        robot.assertViewStateRendered(
            PostPartialState.LoadingPosts,
            PostPartialState.LoadedPostsFromDatabase(testPosts)
        )

        Assert.assertSame(testPosts, presenter.currentState.posts)
    }

    @Test
    fun `should return post list after swipe to refresh`() {
        val testPosts = mutableListOf<Post>()
        testPosts.add(Post(1, 1, "test", "test"))

        every {
            interactor.generateSwipeToRefreshPartialState()
        } returns Observable.merge(
            Observable.just(PostPartialState.SwipeToRefresh),
            Observable.just(PostPartialState.LoadingPosts),
            Observable.just(PostPartialState.LoadedPosts(testPosts)),
            Observable.just(PostPartialState.PostsSavedToDatabase)
        )

        val robot = PostViewRobot(presenter)
        robot.fireSwipeToRefreshIntent()
        robot.assertViewStateRendered(
            PostPartialState.SwipeToRefresh,
            PostPartialState.LoadingPosts,
            PostPartialState.LoadedPosts(testPosts),
            PostPartialState.PostsSavedToDatabase
        )

        Assert.assertSame(testPosts, presenter.currentState.posts)
    }

    @Test
    fun `should start database service`() {
        val robot = PostViewRobot(presenter)

        every { interactor.generateStartServiceIntent() } returns
                Observable.just(PostPartialState.StartDatabaseService)

        robot.fireStartServiceIntent()
        robot.assertViewStateRendered(PostPartialState.StartDatabaseService)
    }

    @Test
    fun `should return pause state`() {
        val robot = PostViewRobot(presenter)

        every { interactor.generateOnPausePartialState() } returns
                Observable.just(PostPartialState.OnPause)

        robot.fireOnPauseIntent()
        robot.assertViewStateRendered(PostPartialState.OnPause)
    }
}
