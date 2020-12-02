package com.nsofronovic.task.post

import com.nsofronovic.task.ui.post.PostPartialState
import com.nsofronovic.task.ui.post.PostPresenter
import com.nsofronovic.task.ui.post.PostView
import com.nsofronovic.task.ui.post.PostViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import org.junit.Assert
import java.util.concurrent.TimeUnit

class PostViewRobot(postPresenter: PostPresenter) {
    private var presenter: PostPresenter = postPresenter

    private val statesSubject = ReplaySubject.create<PostPartialState>()

    private val initialSubject = PublishSubject.create<Unit>()
    private val swipeToRefreshSubject = PublishSubject.create<Unit>()
    private val onPostClickSubject = PublishSubject.create<Int>()
    private val onPauseSubject = PublishSubject.create<Unit>()
    private val startServiceSubject = PublishSubject.create<Unit>()

    private val postView = object : PostView {
        override fun render(state: PostViewState) {
            state.lastChangedState?.let { statesSubject.onNext(it) }
        }

        override fun initialIntent(): Observable<Unit> {
            return initialSubject
        }

        override fun swipeToRefreshIntent(): Observable<Unit> {
            return swipeToRefreshSubject
        }

        override fun onPostClickIntent(): Observable<Int> {
            return onPostClickSubject
        }

        override fun onPauseIntent(): Observable<Unit> {
            return onPauseSubject
        }

        override fun startServiceIntent(): Observable<Unit> {
            return startServiceSubject
        }

    }

    init {
        presenter.attachView(postView)
    }

    fun fireInitialIntent() {
        initialSubject.onNext(Unit)
    }

    fun fireSwipeToRefreshIntent() {
        swipeToRefreshSubject.onNext(Unit)
    }

    fun fireOnPauseIntent() {
        onPauseSubject.onNext(Unit)
    }

    fun fireStartServiceIntent() {
        startServiceSubject.onNext(Unit)
    }

    fun assertViewStateRendered(vararg expectedViewState: PostPartialState) {
        statesSubject
            .takeWhile { statesSubject.values.count() < expectedViewState.size }
            .timeout(5, TimeUnit.SECONDS)
            .onErrorResumeNext(Observable.empty())
            .blockingSubscribe()
        Assert.assertEquals(expectedViewState.count(), statesSubject.values.count())
        Assert.assertArrayEquals(expectedViewState, statesSubject.values)
    }
}
