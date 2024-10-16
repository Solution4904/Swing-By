package app.solution.swing_by.feature

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.solution.swing_by.KakaoMapService
import app.solution.swing_by.KeywordSerchingResultData
import app.solution.swing_by.Place
import app.solution.swing_by.databinding.ActivityMapBinding
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.OptionalConverterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var kakaoMap: KakaoMap
    private lateinit var retrofit: Retrofit
    private val nearbySerchResults = mutableListOf<Place>()
    private var latitude: String = ""
    private var longitude: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()

        binding.mapview.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}

            override fun onMapError(p0: Exception?) {
                Log.d("SOL_LOG", "onMapError: $p0")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                kakaoMap = p0
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

    private fun setButtons() {
        with(binding) {
            btnMovetoposition.setOnClickListener {
                val x = etX.text.toString()
                val y = etY.text.toString()

                moveToPosition(x.toDouble(), y.toDouble())
            }

            btnTracking.setOnClickListener { trackMyLocation() }

            btnSerch.setOnClickListener { serching() }
        }
    }

    private fun serching() {
        retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/v2/local/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(KakaoMapService::class.java)
        retrofitService.getSerchingResult(query = binding.etKeyword.text.toString(), x = longitude, y = latitude).enqueue(object : Callback<KeywordSerchingResultData> {
            override fun onResponse(p0: Call<KeywordSerchingResultData>, p1: Response<KeywordSerchingResultData>) {
//                Log.d("SOL_LOG", p1.body().toString())

                nearbySerchResults.clear()
                for (document in p1.body()!!.documents) {
                    Log.d("SOL_LOG", "document\n$document")
                    nearbySerchResults.add(document)
                }

//                Log.d("SOL_LOG", "검색 결과 갯수 : ${nearbySerchResults.count()}")
            }

            override fun onFailure(p0: Call<KeywordSerchingResultData>, p1: Throwable) {
                Log.d("SOL_LOG", p1.stackTrace.toString())
            }

        })
    }

    private fun moveToPosition(x: Double = 37.402005, y: Double = 127.108621) {
        latitude = x.toString()
        longitude = y.toString()

        val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(x, y))

        kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
    }

    private fun trackMyLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                // TODO: lastLocation이 없는 경우 NPE으로 강제 종료되는 문제가 있음.  
                Log.d("SOL_LOG", "trackMyLocation: ${it.latitude}, ${it.longitude}")
                moveToPosition(it.latitude, it.longitude)
            }
        }
    }
}