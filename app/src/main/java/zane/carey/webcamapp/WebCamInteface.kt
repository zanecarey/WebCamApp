package zane.carey.webcamapp

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface WebCamInterface {

    @Headers(
        "Content-Type: application/json",
        "x-rapidapi-host: webcamstravel.p.rapidapi.com",
        "x-rapidapi-key: 81e151a732msh48b22660c893e7ap19a45ajsn123eb65fd566"
    )
    @GET("/webcams/list/{areaType}%3D{region}%2Fcategory%3D{category}%2Fproperty%3D{property}?lang=en&show=webcams%3Aimage")
    fun getCamResults(@Path("areaType") areaType: String, @Path("region") region: String, @Path("category") category: String, @Path("property") property: String): Deferred<Result>
}

interface CamIDInterface {
    @Headers(
        "Content-Type: application/json",
        "x-rapidapi-host: webcamstravel.p.rapidapi.com",
        "x-rapidapi-key: 81e151a732msh48b22660c893e7ap19a45ajsn123eb65fd566"
    )
    @GET("/webcams/list/webcam={webcamID}?lang=en&show=webcams%3Aimage%2Clocation%2Cplayer")
    fun getCamWithID(@Path("webcamID") webcamID: String): Deferred<Result>
}
data class Result(
    @SerializedName("result")
    val result: WebCams
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
    val image: Image,
    @SerializedName("location")
    val location: Location,
    @SerializedName("player")
    val player: Player
)

data class Image(
    @SerializedName("current")
    val current: Current
)

data class Current(
    @SerializedName("thumbnail")
    val thumbPic: String,
    @SerializedName("preview")
    val previewPic: String
)

data class Location(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

data class Player(
    @SerializedName("live")
    val live: Live
)

data class Live(
    @SerializedName("embed")
    val embed: String
)