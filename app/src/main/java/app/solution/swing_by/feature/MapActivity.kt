package app.solution.swing_by.feature

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.databinding.ActivityMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import java.lang.Exception


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapview.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(p0: Exception?) {
                    Log.d("SOL_LOG", "onMapError: $p0")
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(p0: KakaoMap) {
                    Log.d("SOL_LOG", "onMapReady: $p0")
                }
            })
    }

    override fun onResume() {
        super.onResume()
        binding.mapview.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapview.pause()
    }
}