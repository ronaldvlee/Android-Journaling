package api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ToiletApi {
    @GET("post/")
    fun get(): Call<ResponseBody>

    @POST("edit/")
    fun upload(@Body toiletData: String): Call<ResponseBody>
}