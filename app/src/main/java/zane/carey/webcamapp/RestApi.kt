package zane.carey.webcamapp

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestApi {
    private val camApi: WebCamInterface
    private val idApi: CamIDInterface
    private val gpsApi: GPSInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://webcamstravel.p.rapidapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        camApi = retrofit.create(WebCamInterface::class.java)
        idApi = retrofit.create(CamIDInterface::class.java)
        gpsApi = retrofit.create(GPSInterface::class.java)
    }

    fun getCams(areaType: String, region: String, category: String, property: String) : Deferred<Result> {
        return camApi.getCamResults(areaType, region, category, property)
    }

    fun getCamInfo(webcamID: String): Deferred<Result> {
        return idApi.getCamWithID(webcamID)
    }

    fun getNearbyCams(latitude: Double, longitude: Double, radius: Int) : Deferred<Result> {
        return gpsApi.getNearbyCams(latitude,longitude,radius)
    }
}