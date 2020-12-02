package com.nsofronovic.task.postdetails

import com.nsofronovic.task.model.Post
import com.nsofronovic.task.model.PostDetailsData
import com.nsofronovic.task.model.User
import com.nsofronovic.task.repository.local.PostLocalRepository
import com.nsofronovic.task.repository.local.UserLocalRepository
import com.nsofronovic.task.repository.remote.UserRepository
import com.nsofronovic.task.ui.postdetails.PostDetailsInteractor
import com.nsofronovic.task.ui.postdetails.PostDetailsPartialState
import com.nsofronovic.task.util.NetworkUtil
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test

class PostDetailsInteractorTest {

    private lateinit var interactor: PostDetailsInteractor

    @RelaxedMockK
    private lateinit var networkUtil: NetworkUtil

    @RelaxedMockK
    private lateinit var userRepository: UserRepository

    @RelaxedMockK
    private lateinit var userLocalRepository: UserLocalRepository

    @RelaxedMockK
    private lateinit var postLocalRepository: PostLocalRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this, true)
        interactor = PostDetailsInteractor(
            userRepository, postLocalRepository, userLocalRepository, networkUtil
        )
    }

    @Test
    fun `should return post details data when connected to network and save user to database`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockUser = User(1, "testUserName", "testUserName")
        val mockInsertedRows = 1L

        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { userRepository.getUser(mockCurrentPost.userId) } returns Observable.just(mockUser)

        every { userLocalRepository.getUserById(mockCurrentPost.userId) } returns Maybe.empty()

        every { userLocalRepository.insert(mockUser) } returns Single.just(mockInsertedRows)

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateInitialPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(3)
        testObserver.assertValues(
            PostDetailsPartialState.LoadingUser,
            PostDetailsPartialState.SavedUserToDatabase(mockUser.id.toLong()),
            PostDetailsPartialState.LoadedPostDetails(
                PostDetailsData(
                    mockCurrentPost.id,
                    mockCurrentPost.title,
                    mockCurrentPost.body,
                    mockUser.name,
                    mockUser.email
                )
            )

        )

        testObserver.dispose()
    }

    @Test
    fun `should return post details data when connected to network and user already exists in db`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockUser = User(1, "testUserName", "testUserName")

        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { userRepository.getUser(mockCurrentPost.userId) } returns Observable.just(mockUser)

        every { userLocalRepository.getUserById(mockCurrentPost.userId) } returns Maybe.just(
            mockUser
        )

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateInitialPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(3)
        testObserver.assertValues(
            PostDetailsPartialState.LoadingUser,
            PostDetailsPartialState.UserAlreadyExistsInDatabase,
            PostDetailsPartialState.LoadedPostDetails(
                PostDetailsData(
                    mockCurrentPost.id,
                    mockCurrentPost.title,
                    mockCurrentPost.body,
                    mockUser.name,
                    mockUser.email
                )
            )

        )

        testObserver.dispose()
    }

    @Test
    fun `should return post details from database when no network`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockUser = User(1, "testUserName", "testUserEmail")
        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { networkUtil.isConnectedToInternet() } returns Single.just(false)

        every { userLocalRepository.getUserById(mockCurrentPost.userId) } returns Maybe.just(
            mockUser
        )

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateInitialPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(2)
        testObserver.assertValues(
            PostDetailsPartialState.LoadingUser,
            PostDetailsPartialState.LoadedPostDetails(
                PostDetailsData(
                    mockCurrentPost.id,
                    mockCurrentPost.title,
                    mockCurrentPost.body,
                    mockUser.name,
                    mockUser.email
                )
            )
        )

        testObserver.dispose()
    }

    @Test
    fun `should return no internet message when no user in database`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockThrowable = Throwable()
        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { networkUtil.isConnectedToInternet() } returns Single.just(false)

        every { userLocalRepository.getUserById(mockCurrentPost.userId) } returns Maybe.error(
            mockThrowable
        )

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateInitialPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValues(
            PostDetailsPartialState.NoInternetConnection,
        )

        testObserver.dispose()
    }

    @Test
    fun `should return error if user api call fails`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockThrowable = Throwable("api call error")


        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { userRepository.getUser(mockCurrentPost.userId) } returns Observable.error(
            mockThrowable
        )

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateInitialPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValues(
            PostDetailsPartialState.ErrorLoadUser(mockThrowable.message!!)
        )

        testObserver.dispose()
    }

    @Test
    fun `should return error while saving user to database`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockUser = User(1, "testUserName", "testUserName")
        val mockThrowable = Throwable("database insert error")

        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { networkUtil.isConnectedToInternet() } returns Single.just(true)

        every { userRepository.getUser(mockCurrentPost.userId) } returns Observable.just(mockUser)

        every { userLocalRepository.getUserById(mockCurrentPost.userId) } returns Maybe.empty()

        every { userLocalRepository.insert(mockUser) } returns Single.error(mockThrowable)

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateInitialPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(3)
        testObserver.assertValues(
            PostDetailsPartialState.LoadingUser,
            PostDetailsPartialState.ErrorSavingUserInDatabase(mockThrowable.message!!),
            PostDetailsPartialState.LoadedPostDetails(
                PostDetailsData(
                    mockCurrentPost.id,
                    mockCurrentPost.title,
                    mockCurrentPost.body,
                    mockUser.name,
                    mockUser.email
                )
            )

        )

        testObserver.dispose()
    }

    @Test
    fun `should return deleted post partial state`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockRowsDeleted = 1

        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { postLocalRepository.delete(mockCurrentPost) } returns Single.just(mockRowsDeleted)

        every { postLocalRepository.setDeletedPost(mockCurrentPost) } returns Unit

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateDeletePostPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValues(
            PostDetailsPartialState.DeletedPostFromDatabase
        )

        testObserver.dispose()
    }

    @Test
    fun `should return failed to delete post partial state`() {
        val mockCurrentPost = Post(1, 1, "test", "test")
        val mockRowsDeleted = 0

        every { postLocalRepository.getCurrentPost() } returns mockCurrentPost

        every { postLocalRepository.delete(mockCurrentPost) } returns Single.just(mockRowsDeleted)

        val testObserver = TestObserver<PostDetailsPartialState>()

        interactor.generateDeletePostPartialState()
            .blockingSubscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValues(
            PostDetailsPartialState.FailedToDeletePost
        )

        testObserver.dispose()
    }

}