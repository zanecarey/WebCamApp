package zane.carey.webcamapp

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestApi {
    private val camApi: WebCamInteface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://webcamstravel.p.rapidapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        camApi = retrofit.create(WebCamInteface::class.java)
    }

    fun getCams(country: String, category: String) : Deferred<Result> {
        return camApi.getCamResults(country, category)
    }
}