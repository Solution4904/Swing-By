package app.solution.swing_by

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk


class MyApplication : Application() {
    private lateinit var localDataManager: LocalDataManager

    companion object {
        private lateinit var myApplication: MyApplication
        fun getInstance(): MyApplication = myApplication
    }

    override fun onCreate() {
        super.onCreate()

        myApplication = this
        localDataManager = LocalDataManager(this)

        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    fun getLocalDataManager(): LocalDataManager = localDataManager
}