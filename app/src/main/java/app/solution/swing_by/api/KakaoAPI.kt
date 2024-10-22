package app.solution.swing_by.api

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import app.solution.swing_by.Document
import app.solution.swing_by.KakaoAPIService
import app.solution.swing_by.KeywordSerchingResultData
import app.solution.swing_by.MyApplication
import app.solution.swing_by.NotificationManager
import app.solution.swing_by.constant.KakaoConstant
import com.google.android.gms.location.LocationServices
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KakaoAPI {
    interface KakaoCallBack {
        fun successCallback()
        fun failureCallback()
    }

    companion object {
        private const val TAG = "SOL_LOG"
        private var latitude: Double = 0.0
        private var longitude: Double = 0.0
        private lateinit var retrofit: Retrofit


        val KAKAOMAP_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        const val KAKAOMAP_REQUEST_CODE = 100


        // 로그인
        fun signIn(context: Context, callBack: KakaoCallBack? = null) {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.d(TAG, error.toString())

                    callBack?.failureCallback()
                } else if (token != null) {
                    UserApiClient.instance.accessTokenInfo { tokenInfo, _ ->
                        UserApiClient.instance.me { _, _ ->
                            MyApplication.userUid = tokenInfo?.id.toString()
                            callBack?.successCallback()
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

        private fun trackingMyLocation(activity: Activity, context: Context) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { result ->
                    latitude = result.latitude
                    longitude = result.longitude
                }
            }
        }
    }

    fun serching(activity: Activity, context: Context, keyword: String) {
        trackingMyLocation(activity, context)

        retrofit = Retrofit.Builder()
            .baseUrl(KakaoConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(KakaoAPIService::class.java)
        retrofitService.getSerchingResult(query = keyword, x = longitude.toString(), y = latitude.toString())
            .enqueue(object : Callback<KeywordSerchingResultData> {
                override fun onResponse(p0: Call<KeywordSerchingResultData>, p1: Response<KeywordSerchingResultData>) {
//                Log.d("SOL_LOG", p1.body().toString())

                    val nearbySerchResults = ArrayList<Document>()
                    for (document in p1.body()!!.documents) {
                        Log.d("SOL_LOG", "document\n$document")
                        nearbySerchResults.add(document)

                        val notificationManager = NotificationManager(context)
                        notificationManager.showNotification(
                            keyword,
                            "${document.place_name} (${document.distance}m)"
                        )
                    }

//                Log.d("SOL_LOG", "검색 결과 갯수 : ${nearbySerchResults.count()}")
                }

                override fun onFailure(p0: Call<KeywordSerchingResultData>, p1: Throwable) {
                    Log.d("SOL_LOG", p1.stackTrace.toString())
                }
            })
    }
}