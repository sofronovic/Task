package com.nsofronovic.task.module

import android.content.Context
import androidx.room.Room
import com.nsofronovic.task.db.AppDatabase
import com.nsofronovic.task.network.PostApi
import com.nsofronovic.task.repository.local.PostLocalRepository
import com.nsofronovic.task.repository.local.PostLocalRepositoryImpl
import com.nsofronovic.task.repository.local.UserLocalRepository
import com.nsofronovic.task.repository.local.UserLocalRepositoryImpl
import com.nsofronovic.task.repository.remote.PostRepository
import com.nsofronovic.task.repository.remote.UserRepository
import com.nsofronovic.task.service.DatabaseService
import com.nsofronovic.task.service.ServiceManager
import com.nsofronovic.task.ui.navigation.NavigationManager
import com.nsofronovic.task.ui.post.PostInteractor
import com.nsofronovic.task.ui.post.PostPresenter
import com.nsofronovic.task.ui.postdetails.PostDetailsInteractor
import com.nsofronovic.task.ui.postdetails.PostDetailsPresenter
import com.nsofronovic.task.util.NetworkUtil
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val mviModule = module {
    factory { PostPresenter(get()) }
    factory { PostInteractor(get(), get(), get()) }

    factory { PostDetailsPresenter(get()) }
    factory { PostDetailsInteractor(get(), get(), get(), get()) }
}

val appModule = module {
    single { NavigationManager() }
    single { ServiceManager() }

    single { PostLocalRepositoryImpl(get()) as PostLocalRepository }
    single { PostRepository(get()) }

    single { UserLocalRepositoryImpl(get()) as UserLocalRepository }
    single { UserRepository(get()) }
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

    single { NetworkUtil() }
}

fun dbModule(context: Context, dbName: String) = module {
    single {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            dbName
        ).build()
    }

    single { get<AppDatabase>().postDao() }
    single { get<AppDatabase>().userDao() }
}