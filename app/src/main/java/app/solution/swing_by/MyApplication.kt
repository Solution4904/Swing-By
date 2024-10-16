package app.solution.swing_by

import android.app.Application
import app.solution.swing_by.constant.KakaoAPI
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, KakaoAPI.NATIVE_APP_KEY)
        KakaoSdk.init(this, KakaoAPI.NATIVE_APP_KEY)
    }
}