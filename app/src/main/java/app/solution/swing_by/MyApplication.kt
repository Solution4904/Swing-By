package app.solution.swing_by

import android.app.Application
import app.solution.swing_by.constant.KakaoConstant
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk


class MyApplication : Application() {
    companion object {
        var userUid = ""
    }

    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, KakaoConstant.NATIVE_APP_KEY)
        KakaoSdk.init(this, KakaoConstant.NATIVE_APP_KEY)
    }
}