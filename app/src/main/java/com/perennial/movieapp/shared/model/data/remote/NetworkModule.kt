package com.perennial.movieapp.shared.model.data.remote
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.perennial.movieapp.shared.model.data.remote.NetworkConstant.API_KEY
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkConstant {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    val ChuckerInterceptor = named("chuckerInterceptor")
    val LogInterceptor = named("logInterceptor")
    const val API_KEY = "bbf5a3000e95f1dddf266b5e187d4b21"

    const val IMG_BASE_URL = "https://image.tmdb.org/t/p/"
    const val DEFAULT_BACKDROP_URL =
        "http://placehold.jp/36/cccccc/aaaaaa/480x270.png?text=Awesome%20Poster%20Here"
    const val DEFAULT_POSTER_URL =
        "http://placehold.jp/48/cccccc/aaaaaa/320x480.png?text=Awesome%20Poster%20Here"
}

val networkModule = module {

    // Interceptors
    single<Interceptor>(NetworkConstant.ChuckerInterceptor) { ChuckerInterceptor(androidContext()) }
    single<Interceptor>(NetworkConstant.LogInterceptor) {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // OkHttpClients
    single {
        OkHttpClient.Builder()
            .addInterceptor {
                val oldReq = it.request()
                val newUrl = oldReq.url.newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()
                val newReq = oldReq.newBuilder().url(newUrl).build()
                it.proceed(newReq)
            }.build()
    }

    // Service Apis
    single { createRestApiAdapter<MovieAPIService>(get(), NetworkConstant.BASE_URL) }
}

inline fun <reified T> createRestApiAdapter(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}