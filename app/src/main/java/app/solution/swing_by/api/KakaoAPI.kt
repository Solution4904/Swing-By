package app.solution.swing_by.api

import app.solution.swing_by.item.Document
import app.solution.swing_by.item.KeywordSerchingResultData
import app.solution.swing_by.root.LogType
import app.solution.swing_by.root.MyUtils
import app.solution.swing_by.constant.KakaoConstant
import app.solution.swing_by.constant.LocalDataConstant
import app.solution.swing_by.root.MyApplication
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class KakaoAPI {
    interface CallbackByDocuments {
        fun successCallback(array: Array<Document>)
        fun failureCallback()
    }

    interface CallbackByAccessTokenInfo {
        fun successCallback(accessTokenInfo: AccessTokenInfo?)
        fun failureCallback()
    }


    companion object {
        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(KakaoConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // # 주변 키워드 검색
        fun searching(keyword: String, latLng: LatLng, categoryGroupCode: String, callback: CallbackByDocuments) {
            CoroutineScope(Dispatchers.Main).launch {
                val retrofitService = retrofit.create(KakaoAPIService::class.java)
                retrofitService.getSerchingResult(
                    query = keyword,
                    x = latLng.longitude.toString(),
                    y = latLng.latitude.toString(),
                    size = MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.OPTION_SEARCHING_LIMIT, "3").toInt(),
                    radius = MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.OPTION_SEARCHING_DISTANCE, "500").toInt(),
                    categoryGroupCode = categoryGroupCode
                )
                    .enqueue(object : Callback<KeywordSerchingResultData> {
                        override fun onResponse(p0: Call<KeywordSerchingResultData>, p1: Response<KeywordSerchingResultData>) {
                            MyUtils.log(detail = "${p1.body()!!.documents}")

                            val nearbySerchResults = ArrayList<Document>()

                            for (document in p1.body()!!.documents) {
                                nearbySerchResults.add(document)
                            }

                            callback.successCallback(nearbySerchResults.toTypedArray())
                        }

                        override fun onFailure(p0: Call<KeywordSerchingResultData>, p1: Throwable) {
                            callback.failureCallback()
                            MyUtils.log(LogType.ERROR, p1.stackTrace.toString())
                        }
                    })
            }
        }
    }
}