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

    private val initialSubject = PublishSubject.create<Unit>()
    private val statesSubject = ReplaySubject.create<PostPartialState>()

    private val postView = object : PostView {
        override fun render(state: PostViewState) {
            state.lastChangedState?.let { statesSubject.onNext(it) }
        }

        override fun initialIntent(): Observable<Unit> {
            return initialSubject
        }

    }

    init {
        presenter.attachView(postView)
    }

    fun fireInitialIntent() {
        initialSubject.onNext(Unit)
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