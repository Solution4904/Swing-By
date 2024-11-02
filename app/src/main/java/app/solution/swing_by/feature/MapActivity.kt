package app.solution.swing_by.feature

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.Document
import app.solution.swing_by.R
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.api.KakaoAPI
import app.solution.swing_by.constant.KakaoConstant
import app.solution.swing_by.databinding.ActivityMapBinding
import app.solution.swing_by.item.MemoItem
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.mapwidget.InfoWindowLayer
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiImage
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var kakaoMap: KakaoMap
    private lateinit var currentLatLng: LatLng
    private var labelLayer: LabelLayer? = null
    private var infoWindowLayer: InfoWindowLayer? = null
    private val labelDatas: MutableMap<String, LatLng> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapview.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("SOL_LOG", "onMapDestroy: ")
            }

            override fun onMapError(p0: Exception?) {
                Log.d("SOL_LOG", "onMapError: ${p0?.stackTrace}")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                Log.d("SOL_LOG", "onMapReady: ")

                kakaoMap = p0
                labelLayer = p0.labelManager?.layer

                kakaoMap.setOnLabelClickListener { kakaoMap, labelLayer, label ->
                    onInfoWindowClicked(label.labelId)

                    true
                }

                infoWindowLayer = kakaoMap.mapWidgetManager?.infoWindowLayer
                kakaoMap.setOnInfoWindowClickListener { kakaoMap, infoWindow, guiId ->
                    onInfoWindowClicked(infoWindow.id)
                }

                checkPermissions()
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

    private fun checkPermissions() {
        TedPermission.create().apply {
            setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(this@MapActivity, "Permission Granted", Toast.LENGTH_SHORT).show()

                    trackMyLocation()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@MapActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
            setDeniedMessage("서비스를 이용하시려면 권한이 필요합니다.")
            setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ).check()
        }
    }

    @SuppressLint("MissingPermission")
    private fun trackMyLocation() {
        LocationServices.getFusedLocationProviderClient(this).apply {
            getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { result ->
                    result?.let {
//                        Log.d("SOL_LOG", "getCurrentLocation: ${result.longitude} / ${result.latitude}")
                        currentLatLng = LatLng.from(result.latitude, result.longitude)

                        moveToPosition()
                        searching()
                    }
                }
        }
    }

    private fun moveToPosition() {
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLatLng)

        kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
    }

    private fun searching() {
        FirebaseAPI.getMemoList(object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(results: DataSnapshot?) {
                results?.let { result ->
                    result.children.map { snapshot ->
                        val memoItem = snapshot.getValue(MemoItem::class.java)

                        KakaoAPI.searching(this@MapActivity, "${memoItem?.location}", currentLatLng.longitude, currentLatLng.latitude, object : KakaoAPI.KakaoCallBack {
                            override fun successCallback(array: Array<Document>) {
                                array.forEach { index ->
//                                    createLabel(index.place_name, LatLng.from(index.y.toDouble(), index.x.toDouble()))
                                    infoWindowLayer?.addInfoWindow(
                                        getComplexLayout(index, memoItem!!)
                                    )
                                    labelDatas[index.place_name] = LatLng.from(index.y.toDouble(), index.x.toDouble())
                                }
                            }

                            override fun successCallback() {}
                            override fun successCallback(accessTokenInfo: AccessTokenInfo?) {}
                            override fun failureCallback() {}
                        })
                    }
                }
            }

            override fun successCallback(results: Task<AuthResult>) {}
            override fun failureCallback() {}
        })
    }

    private fun onInfoWindowClicked(labelId: String?) {
        // TODO: 설치되어 있어도 packageManager.queryIntentActivities가 null?로 잡혀서 마켓 연결하는 API 수준? 문제가 있음
        // TODO: https://apis.map.kakao.com/android_v2/docs/api-guide/urlscheme/
        if (labelDatas.containsKey(labelId)) {
            val navigationScheme = "kakaomap://route?sp=${currentLatLng.latitude},${currentLatLng.longitude}&ep=${labelDatas[labelId]!!.latitude},${labelDatas[labelId]!!.longitude}&by=FOOT"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(navigationScheme)).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

            // 설치되어 있지 않다면
            if (list.isEmpty()) {
                // 마켓으로 이동
                this.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(KakaoConstant.MARKET_SCHEME))
                )
            } else {
                // URL 스킴 으로 카카오맵 검색
                this.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(navigationScheme))
                )
            }
        }
    }

    private fun getComplexLayout(index: Document, memoItem: MemoItem): InfoWindowOptions {
        // body
        val body = GuiLayout(Orientation.Vertical)
        body.setPadding(15, 15, 15, 13)
        val image = GuiImage(R.drawable.window_body, true)
        image.setFixedArea(7, 7, 7, 7)
        body.setBackground(image)

        // upper layout
        val text = GuiText(index.place_name).apply {
            textSize = 23
            textColor = Color.parseColor("#013ADF")
        }
        text.setTextSize(25)
//        text.paddingRight = 13


        val upperLayout = GuiLayout(Orientation.Horizontal)
        upperLayout.addView(text)
//        upperLayout.addView(GuiImage(R.drawable.choonsik, false))

        // lower layout
        val text2 = GuiText(memoItem.title)
        text2.setTextSize(20)
        text2.paddingTop = 8
        text2.setTextColor(Color.parseColor("#003F63"))

        body.addView(upperLayout)
        body.addView(text2)

        val options = InfoWindowOptions.from(
            index.place_name,
            LatLng.from(index.y.toDouble(), index.x.toDouble())
        )
        options.setBody(body)
        options.setBodyOffset(0f, -4f)
        options.setTail(GuiImage(R.drawable.window_tail, false))
        options.setVisible(true)
        return options
    }
}