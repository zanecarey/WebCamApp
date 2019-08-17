package zane.carey.webcamapp

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WebCamInteface {

    @Headers("Content-Type: application/json","x-rapidapi-host: webcamstravel.p.rapidapi.com", "x-rapidapi-key: 81e151a732msh48b22660c893e7ap19a45ajsn123eb65fd566")
    @GET("/webcams/list/country%3DIT%2Fcategory%3Dbeach?lang=en&show=webcams%3Aimage")
    fun getCamResults(): Deferred<Result>
}

data class Result(
    @SerializedName("result")
    val result: List<WebCams>
)

data class WebCams(
    @SerializedName("webcams")
    val webcams: List<WebCamInfo>
)

data class WebCamInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: Image
)

data class Image(
    @SerializedName("current")
    val current: Current
)

data class Current(
    @SerializedName("thumbnail")
    val thumbPic: String
)
