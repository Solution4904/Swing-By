package app.solution.swing_by

import app.solution.swing_by.constant.KakaoAPI
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface KakaoAPIService {
    // 카카오맵 장소 검색 결과
    @GET("search/keyword.json")
    fun getSerchingResult(
        @Header("Authorization") Authorization: String = "KakaoAK ${KakaoAPI.REST_API_KEY}",
        @Query("sort") sort: String = "distance",
        @Query("radius") radius: Int = 5000,
        @Query("size") size: Int = 5,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("query") query: String,
    ): Call<KeywordSerchingResultData>
}