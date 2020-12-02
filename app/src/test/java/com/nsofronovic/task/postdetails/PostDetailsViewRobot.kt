package com.nsofronovic.task.postdetails

import com.nsofronovic.task.ui.postdetails.PostDetailsPartialState
import com.nsofronovic.task.ui.postdetails.PostDetailsPresenter
import com.nsofronovic.task.ui.postdetails.PostDetailsView
import com.nsofronovic.task.ui.postdetails.PostDetailsViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import org.junit.Assert
import java.util.concurrent.TimeUnit

class PostDetailsViewRobot(postDetailsPresenter: PostDetailsPresenter) {
    private var presenter: PostDetailsPresenter = postDetailsPresenter

    private val statesSubject = ReplaySubject.create<PostDetailsPartialState>()

    private val initialSubject = PublishSubject.create<Unit>()
    private val deletePostSubject = PublishSubject.create<Unit>()

    private val postDetailsView = object : PostDetailsView {
        override fun render(state: PostDetailsViewState) {
            state.lastChangedState?.let { statesSubject.onNext(it) }
        }

        override fun initialIntent(): Observable<Unit> {
            return initialSubject
        }

        override fun deletePostIntent(): Observable<Unit> {
            return deletePostSubject
        }
    }

    init {
        presenter.attachView(postDetailsView)
    }

    fun fireInitialIntent() {
        initialSubject.onNext(Unit)
    }

    fun fireDeletePostIntent() {
        deletePostSubject.onNext(Unit)
    }

    fun assertViewStateRendered(vararg expectedViewState: PostDetailsPartialState) {
        statesSubject
            .takeWhile { statesSubject.values.count() < expectedViewState.size }
            .timeout(5, TimeUnit.SECONDS)
            .onErrorResumeNext(Observable.empty())
            .blockingSubscribe()
        Assert.assertEquals(expectedViewState.count(), statesSubject.values.count())
        Assert.assertArrayEquals(expectedViewState, statesSubject.values)
    }
}
