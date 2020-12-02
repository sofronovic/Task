package com.nsofronovic.task.postdetails

import com.nsofronovic.task.model.PostDetailsData
import com.nsofronovic.task.ui.postdetails.PostDetailsInteractor
import com.nsofronovic.task.ui.postdetails.PostDetailsPartialState
import com.nsofronovic.task.ui.postdetails.PostDetailsPresenter
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PostDetailsPresenterTest {
    private lateinit var presenter: PostDetailsPresenter

    @RelaxedMockK
    private lateinit var interactor: PostDetailsInteractor

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockKAnnotations.init(this, relaxUnitFun = true)
        presenter = PostDetailsPresenter(interactor)
    }

    @Test
    fun `should return post details state on initial intent`() {
        val postDetails = PostDetailsData(
            1, "test", "test", "test", "test"
        )

        every {
            interactor.generateInitialPartialState()
        } returns Observable.merge(
            Observable.just(PostDetailsPartialState.LoadingUser),
            Observable.just(PostDetailsPartialState.LoadedPostDetails(postDetails)),
            Observable.just(PostDetailsPartialState.SavedUserToDatabase(1))
        )

        val robot = PostDetailsViewRobot(presenter)
        robot.fireInitialIntent()
        robot.assertViewStateRendered(
            PostDetailsPartialState.LoadingUser,
            PostDetailsPartialState.LoadedPostDetails(postDetails),
            PostDetailsPartialState.SavedUserToDatabase(1)
        )

        Assert.assertSame(postDetails, presenter.currentState.postDetailsData)
    }

    @Test
    fun `should return error state while fetching user from server`() {
        every {
            interactor.generateInitialPartialState()
        } returns Observable.just(
            PostDetailsPartialState.ErrorLoadUser("Error from API")
        )

        val robot = PostDetailsViewRobot(presenter)
        robot.fireInitialIntent()
        robot.assertViewStateRendered(
            PostDetailsPartialState.ErrorLoadUser("Error from API")
        )
    }

    @Test
    fun `should return no internet state if user db table is empty`() {
        every {
            interactor.generateInitialPartialState()
        } returns Observable.just(
            PostDetailsPartialState.NoInternetConnection
        )

        val robot = PostDetailsViewRobot(presenter)
        robot.fireInitialIntent()
        robot.assertViewStateRendered(
            PostDetailsPartialState.NoInternetConnection
        )
    }

    @Test
    fun `should return deleted post state`() {
        val robot = PostDetailsViewRobot(presenter)

        every {
            interactor.generateDeletePostPartialState()
        } returns Observable.just(PostDetailsPartialState.DeletedPostFromDatabase)

        robot.fireDeletePostIntent()
        robot.assertViewStateRendered(PostDetailsPartialState.DeletedPostFromDatabase)
    }

    @Test
    fun `should return failed to delete post state`() {
        val robot = PostDetailsViewRobot(presenter)

        every {
            interactor.generateDeletePostPartialState()
        } returns Observable.just(PostDetailsPartialState.FailedToDeletePost)

        robot.fireDeletePostIntent()
        robot.assertViewStateRendered(PostDetailsPartialState.FailedToDeletePost)
    }
}
