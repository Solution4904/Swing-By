package app.solution.swing_by.api

import android.content.Context
import android.util.Log
import app.solution.swing_by.Document
import app.solution.swing_by.KakaoAPIService
import app.solution.swing_by.KeywordSerchingResultData
import app.solution.swing_by.constant.KakaoConstant
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.vectormap.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class KakaoAPI {
    interface KakaoCallBack {
        fun successCallback()
        fun successCallback(array: Array<Document>)
        fun successCallback(accessTokenInfo: AccessTokenInfo?)
        fun failureCallback()
    }


    companion object {
        private const val TAG = "SOL_LOG"
        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(KakaoConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }


        // 로그인
        fun signIn(context: Context, callBack: KakaoCallBack? = null) {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.d(TAG, error.toString())

                    callBack?.failureCallback()
                } else if (token != null) {
                    UserApiClient.instance.accessTokenInfo { tokenInfo, _ ->
                        UserApiClient.instance.me { _, _ ->
                            callBack?.successCallback(tokenInfo)
                        }
                    }
                }
            }

            /*
            *   1. 카카오톡 앱이 설치되어 있다면 카카오톡으로 로그인 시도
            *   2. (1) 실패 시 카카오계정(웹)으로 로그인 시도
            */
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            Log.d(TAG, "카카오계정 로그인 의도적인 취소")
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        Log.d(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }

        // # 주변 키워드 검색
        fun searching(keyword: String, latLng: LatLng, categoryGroupCode: String, callback: KakaoCallBack) {
            val retrofitService = retrofit.create(KakaoAPIService::class.java)
            retrofitService.getSerchingResult(query = keyword, x = latLng.longitude.toString(), y = latLng.latitude.toString(), categoryGroupCode = categoryGroupCode)
                .enqueue(object : Callback<KeywordSerchingResultData> {
                    override fun onResponse(p0: Call<KeywordSerchingResultData>, p1: Response<KeywordSerchingResultData>) {
                        val nearbySerchResults = ArrayList<Document>()
                        for (document in p1.body()!!.documents) {
                            Log.d(TAG, "document\n$document")
                            nearbySerchResults.add(document)
                        }

                        callback.successCallback(nearbySerchResults.toTypedArray())
                    }

                    override fun onFailure(p0: Call<KeywordSerchingResultData>, p1: Throwable) {
                        Log.d(TAG, p1.stackTrace.toString())

                        callback.failureCallback()
                    }
                })
        }
    }
}