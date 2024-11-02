package app.solution.swing_by

import android.app.Application
import android.widget.Toast
import app.solution.swing_by.constant.KakaoConstant
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

        KakaoMapSdk.init(this, KakaoConstant.NATIVE_APP_KEY)
        KakaoSdk.init(this, KakaoConstant.NATIVE_APP_KEY)

//        Toast.makeText(this, "init", Toast.LENGTH_SHORT).show()
    }

    fun getLocalDataManager(): LocalDataManager = localDataManager
}