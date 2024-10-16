package app.solution.swing_by

import android.app.Application
import app.solution.swing_by.constant.KakaoMapAPI
import com.kakao.vectormap.KakaoMapSdk


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, KakaoMapAPI.APP_KEY);
    }
}