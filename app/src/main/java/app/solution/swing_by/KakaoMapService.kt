package app.solution.swing_by

import app.solution.swing_by.constant.KakaoMapAPI
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoMapService {
    @GET("search/keyword.json")
    fun getSerchingResult(
        @Header("Authorization") Authorization: String = "KakaoAK ${KakaoMapAPI.REST_API_KEY}",
        @Query("sort") sort: String = "distance",
        @Query("radius") radius: Int = 5000,
        @Query("size") size: Int = 5,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("query") query: String,
    ): Call<KeywordSerchingResultData>
}