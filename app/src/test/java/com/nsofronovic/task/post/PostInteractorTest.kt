package com.nsofronovic.task.post

import com.nsofronovic.task.model.Post
import com.nsofronovic.task.repository.local.PostLocalRepository
import com.nsofronovic.task.repository.remote.PostRepository
import com.nsofronovic.task.ui.post.PostInteractor
import com.nsofronovic.task.ui.post.PostPartialState
import com.nsofronovic.task.util.NetworkUtil
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test

class PostInteractorTest {

    private lateinit var interactor: PostInteractor

    @RelaxedMockK
    private lateinit var networkUtil: NetworkUtil

    @RelaxedMockK
    private lateinit var postRepository: PostRepository

    @RelaxedMockK
    private lateinit var postLocalRepository: PostLocalRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this, true)
        interactor = PostInteractor(
            postRepository, postLocalRepository, networkUtil
        )
    }

    @Test
    fun `should return post list when connected to network and save posts to database`() {
        val mockPosts = mutableListOf<Post>(Post(1, 1, "test", "test"))
        val mockInsertedRows = listOf<Long>(1, 2, 3)

        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { postRepository.getPosts() } returns Observable.just(mockPosts)

        every { postLocalRepository.insertAll(*mockPosts.toTypedArray()) } returns Single.just(
            mockInsertedRows
        )

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateInitialPartialState().blockingSubscribe(testObserver)

        testObserver.assertValueCount(3)
        testObserver.assertValues(
            PostPartialState.LoadingPosts,
            PostPartialState.PostsSavedToDatabase,
            PostPartialState.LoadedPosts(mockPosts)
        )

        testObserver.dispose()
    }

    @Test
    fun `should return error if posts api call fails`() {
        val mockThrowable = Throwable("Posts api call error")
        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { postRepository.getPosts() } returns Observable.error(mockThrowable)

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateInitialPartialState().blockingSubscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValues(
            PostPartialState.ErrorLoadingPosts(mockThrowable.message!!)
        )

        testObserver.dispose()
    }

    @Test
    fun `should return error if saving posts to database fails`() {
        val mockPosts = mutableListOf<Post>(Post(1, 1, "test", "test"))
        val mockThrowable = Throwable("Posts insert db error")


        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { postRepository.getPosts() } returns Observable.just(mockPosts)

        every { postLocalRepository.insertAll(*mockPosts.toTypedArray()) } returns Single.error(
            mockThrowable
        )

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateInitialPartialState().blockingSubscribe(testObserver)

        testObserver.assertValueCount(3)
        testObserver.assertValues(
            PostPartialState.LoadingPosts,
            PostPartialState.ErrorSavingPostsInDatabase(mockThrowable.message!!),
            PostPartialState.LoadedPosts(mockPosts)
        )

        testObserver.dispose()
    }

    @Test
    fun `should return post list from database when no network`() {
        val mockPosts = mutableListOf<Post>(Post(1, 1, "test", "test"))

        every { networkUtil.isConnectedToInternet() } returns Single.just(false)

        every { postLocalRepository.getAll() } returns Single.just(mockPosts)

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateInitialPartialState().blockingSubscribe(testObserver)

        testObserver.assertValueCount(2)
        testObserver.assertValues(
            PostPartialState.LoadingPosts,
            PostPartialState.LoadedPostsFromDatabase(mockPosts),
        )

        testObserver.dispose()
    }

    @Test
    fun `should return no internet message if post table is empty in database`() {
        every { networkUtil.isConnectedToInternet() } returns Single.just(false)

        every { postLocalRepository.getAll() } returns Single.just(listOf())

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateInitialPartialState().blockingSubscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValues(
            PostPartialState.NoInternetConnection
        )

        testObserver.dispose()
    }

    @Test
    fun `should return posts after swipe to refresh`() {
        val mockPosts = mutableListOf<Post>(Post(1, 1, "test", "test"))
        val mockInsertedRows = listOf<Long>(1, 2, 3)

        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { postRepository.getPosts() } returns Observable.just(mockPosts)

        every { postLocalRepository.insertAll(*mockPosts.toTypedArray()) } returns Single.just(
            mockInsertedRows
        )

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateSwipeToRefreshPartialState().blockingSubscribe(testObserver)

        testObserver.assertValueCount(4)
        testObserver.assertValues(
            PostPartialState.SwipeToRefresh,
            PostPartialState.LoadingPosts,
            PostPartialState.PostsSavedToDatabase,
            PostPartialState.LoadedPosts(mockPosts)
        )

        testObserver.dispose()

    }

    @Test
    fun `should return post item click state`() {
        val mockPost = Post(1, 1, "test", "test")
        every { postLocalRepository.setCurrentPost(mockPost) } returns Unit

        val testObserver = TestObserver<PostPartialState>()

        interactor.generatePostClickPartialState(mockPost).blockingSubscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValues(
            PostPartialState.PostClicked
        )

        testObserver.dispose()
    }

    @Test
    fun `should load posts from state after onPause`() {
        val mockPost = Post(1, 1, "test", "test")
        val mockPosts = mutableListOf<Post>(mockPost)

        every { postLocalRepository.getDeletedPost() } returns null

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateLoadPostsFromStatePartialState(mockPosts).blockingSubscribe(testObserver)

        testObserver.assertValueCount(2)
        testObserver.assertValues(
            PostPartialState.LoadingPosts,
            PostPartialState.LoadedPostsFromState(mockPosts)
        )

        testObserver.dispose()
    }

    @Test
    fun `should load refresh post list after deleting item`() {
        val mockPost = Post(1, 1, "test", "test")
        val mockPostSecond = Post(1, 1, "test", "test")

        val mockPosts = mutableListOf<Post>(mockPost, mockPostSecond)
        val mockPostsUpdated = mutableListOf<Post>(mockPostSecond)

        every { postLocalRepository.getDeletedPost() } returns mockPost

        every { postLocalRepository.setDeletedPost(null) } returns Unit

        val testObserver = TestObserver<PostPartialState>()

        interactor.generateLoadPostsFromStatePartialState(mockPosts).blockingSubscribe(testObserver)

        testObserver.assertValueCount(2)
        testObserver.assertValues(
            PostPartialState.LoadingPosts,
            PostPartialState.LoadedPostsFromState(mockPostsUpdated)
        )

        testObserver.dispose()
    }

}