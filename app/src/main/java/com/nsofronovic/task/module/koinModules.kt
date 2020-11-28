package com.nsofronovic.task.module

import com.nsofronovic.task.network.PostApi
import com.nsofronovic.task.repository.PostRepository
import com.nsofronovic.task.ui.navigation.NavigationManager
import com.nsofronovic.task.ui.post.PostInteractor
import com.nsofronovic.task.ui.post.PostPresenter
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val mviModule = module {
    factory { PostPresenter(get()) }
    factory { PostInteractor(get()) }
}

val appModule = module {
    single { NavigationManager() }
    single { PostRepository(get()) }
}

fun networkModule(baseUrl: String) = module {
    single {
        OkHttpClient.Builder()
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(PostApi::class.java) }
}
