package app.solution.swing_by.api

import app.solution.swing_by.BuildConfig
import app.solution.swing_by.item.KeywordSerchingResultData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPIService {
    // 카카오맵 장소 검색 결과
    @GET("search/keyword.json")
    fun getSerchingResult(
        @Header("Authorization") authorization: String = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
        @Query("sort") sort: String = "distance",
        @Query("radius") radius: Int = 500,
        @Query("size") size: Int = 3,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("query") query: String,
        @Query("category_group_code") categoryGroupCode: String? = null,
    ): Call<KeywordSerchingResultData>
}