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
        "x-windy-key: W6vo9arrQW6oGhjCpFn8IlSaFw7VYoCx"
        //"x-rapidapi-key: 81e151a732msh48b22660c893e7ap19a45ajsn123eb65fd566"
    )
    @GET("api/webcams/v2/list/{areaType}%3D{region}%2Fcategory%3D{category}%2Fproperty%3D{property}%2Flimit%3D50%2C{offset}?lang=en&show=webcams%3Aimage")
    fun getCamResults(
        @Path("areaType") areaType: String, @Path("region") region: String, @Path("category") category: String, @Path(
            "property"
        ) property: String, @Path("offset") offset: Int
    ): Deferred<Result>
}

interface WebCamInterfaceNoCategory {

    @Headers(
        "Content-Type: application/json",
        "x-windy-key: W6vo9arrQW6oGhjCpFn8IlSaFw7VYoCx"
    //"x-rapidapi-key: 81e151a732msh48b22660c893e7ap19a45ajsn123eb65fd566"
    )
    @GET("api/webcams/v2/list/{areaType}%3D{region}%2Fproperty%3D{property}%2Flimit%3D50%2C{offset}?lang=en&show=webcams%3Aimage")
    fun getCamResultsNoCat(
        @Path("areaType") areaType: String, @Path("region") region: String, @Path("property") property: String, @Path(
            "offset"
        ) offset: Int
    ): Deferred<Result>
}

interface WebCamInterfaceNoCountry {

    @Headers(
        "Content-Type: application/json",
        "x-windy-key: W6vo9arrQW6oGhjCpFn8IlSaFw7VYoCx"
    //"x-rapidapi-key: 81e151a732msh48b22660c893e7ap19a45ajsn123eb65fd566"
    )
    @GET("api/webcams/v2/list/category%3D{category}%2Fproperty%3D{property}%2Flimit%3D50%2C{offset}?lang=en&show=webcams%3Aimage")
    fun getCamResultsNoCountry(
        @Path("category") category: String, @Path("property") property: String, @Path("offset") offset: Int
    ): Deferred<Result>
}

interface WebCamInterfaceNoCountryNoCategory {

    @Headers(
        "Content-Type: application/json",
        "x-windy-key: W6vo9arrQW6oGhjCpFn8IlSaFw7VYoCx"
    )
    @GET("api/webcams/v2/list/property%3D{property}%2Flimit%3D50%2C{offset}?lang=en&show=webcams%3Aimage")
    fun getCamResultsNoCountry(
        @Path("property") property: String, @Path("offset") offset: Int
    ): Deferred<Result>
}

interface CamIDInterface {
    @Headers(
        "Content-Type: application/json",
        "x-windy-key: W6vo9arrQW6oGhjCpFn8IlSaFw7VYoCx"
    )
    @GET("api/webcams/v2/list/webcam={webcamID}?lang=en&show=webcams%3Aimage%2Clocation%2Cplayer%2Cstatistics%2Curl%2Cproperty")
    fun getCamWithID(@Path("webcamID") webcamID: String): Deferred<Result>
}

interface GPSInterface {
    @Headers(
        "Content-Type: application/json",
        "x-windy-key: W6vo9arrQW6oGhjCpFn8IlSaFw7VYoCx"
    )
    @GET("api/webcams/v2/list/nearby={latitude}%2C{longitude}%2C{radius}?lang=en&show=webcams%3Aimage")
    fun getNearbyCams(
        @Path("latitude") latitude: Double, @Path("longitude") longitude: Double, @Path(
            "radius"
        ) radius: Int
    ): Deferred<Result>
}

data class Result(
    @SerializedName("result")
    val result: WebCams

)

data class WebCams(
    @SerializedName("webcams")
    val webcams: List<WebCamInfo>,
    @SerializedName("total")
    val total: Int
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
    val player: Player,
    @SerializedName("url")
    val url: Url,
    @SerializedName("statistics")
    val statistics: Stats,
    @SerializedName("property")
    val property: List<Property>
)

data class Property(
    @SerializedName("id")
    val propertyID: String
)

data class Stats(
    @SerializedName("views")
    val views: Int
)

data class Image(
    @SerializedName("current")
    val current: Current,
    @SerializedName("daylight")
    val daylight: Daylight
)

data class Current(
    @SerializedName("thumbnail")
    val thumbPic: String,
    @SerializedName("preview")
    val previewPic: String
)

data class Daylight(
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

data class Url(
    @SerializedName("current")
    val current: CurrentUrl
)

data class CurrentUrl(
    @SerializedName("mobile")
    val mobileUrl: String
)