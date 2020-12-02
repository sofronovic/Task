package com.nsofronovic.task.util

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * NetworkUtil handle internet connectivity
 *
 **/
class NetworkUtil {

    /**
     * isConnectedToInternet() check if device is connected to network
     *
     **/
    fun isConnectedToInternet(): Single<Boolean> {
        return ReactiveNetwork.checkInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it }
    }
}