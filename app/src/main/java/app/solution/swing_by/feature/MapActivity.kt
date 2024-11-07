package app.solution.swing_by.feature

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import com.kakao.vectormap.mapwidget.InfoWindowLayer
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiImage
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var kakaoMap: KakaoMap
    private lateinit var kakaomapViewport:Rect
    private lateinit var currentLatLng: LatLng
    private var labelLayer: LabelLayer? = null
    private var infoWindowLayer: InfoWindowLayer? = null
    private val labelDatas: MutableMap<String, LabelData> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        BottomSheetBehavior.from(binding.layoutBottomsheet.root).state = BottomSheetBehavior.STATE_HIDDEN

        binding.mapview.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}
            override fun onMapError(p0: Exception?) {}
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                kakaoMap = p0
                kakaomapViewport = p0.viewport
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
//                    Toast.makeText(this@MapActivity, "Permission Granted", Toast.LENGTH_SHORT).show()

                    trackMyLocation()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
//                    Toast.makeText(this@MapActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
            setDeniedMessage(resources.getString(R.string.permission_is_required_to_use_the_service))
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
                        currentLatLng = LatLng.from(result.latitude, result.longitude)

                        searching()
                        showSharingTransformLabel(currentLatLng)
                    }
                }
        }
    }

    private fun searching() {
        FirebaseAPI.getMemoList(object : FirebaseAPI.CallbackByDataSnapshot {
            override fun successCallback(results: DataSnapshot?) {
                results?.let { result ->
                    result.children.map { snapshot ->
                        val memoItem = snapshot.getValue(MemoItem::class.java)

                        KakaoAPI.searching("${memoItem?.location}", currentLatLng, memoItem?.categoryCode!!, object : KakaoAPI.CallbackByDocuments {
                            override fun successCallback(array: Array<Document>) {
                                array.forEach { index ->
                                    infoWindowLayer?.addInfoWindow(
                                        getComplexLayout(index)
                                    )
                                    labelDatas[index.place_name] = LabelData(index.place_name, memoItem.description!!, LatLng.from(index.y.toDouble(), index.x.toDouble()))
                                }
                            }

                            override fun failureCallback() {}
                        })
                    }
                }
            }

            override fun failureCallback() {}
        })
    }

    private fun onInfoWindowClicked(labelId: String?) {
        if (labelDatas.containsKey(labelId)) {

            showDetailToLocation(labelId!!)
        }

    }

    private fun navigation(labelData: LabelData) {
        // TODO: 설치되어 있어도 packageManager.queryIntentActivities가 null?로 잡혀서 마켓 연결하는 API 수준? 문제가 있음
        // TODO: https://apis.map.kakao.com/android_v2/docs/api-guide/urlscheme/
        val navigationScheme = "kakaomap://route?sp=${currentLatLng.latitude},${currentLatLng.longitude}&ep=${labelData.latLng.latitude},${labelData.latLng.longitude}&by=FOOT"

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

    private fun getComplexLayout(index: Document): InfoWindowOptions {
        // body
        val body = GuiLayout(Orientation.Vertical)
        body.setPadding(15, 15, 15, 13)
        val image = GuiImage(R.drawable.window_body, true)
        image.setFixedArea(7, 7, 7, 7)
        body.setBackground(image)

        // upper layout
        val text = GuiText(index.place_name).apply {
            textSize = 23
            textColor = getColor(R.color.personalBlue)
        }
        text.setTextSize(25)

        val upperLayout = GuiLayout(Orientation.Horizontal)
        upperLayout.addView(text)

        body.addView(upperLayout)

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

    private fun showSharingTransformLabel(latLng: LatLng) {
        val pos = LatLng.from(latLng.latitude, latLng.longitude)

        labelLayer!!.addLabel(
            LabelOptions.from(pos).setRank(101)
                .setStyles(
                    LabelStyle.from(R.drawable.current_location)
                        .setAnchorPoint(0.5f, 0.5f)
                        .setIconTransition(LabelTransition.from(Transition.None, Transition.None))
                )
        )

        kakaoMap.moveCamera(
            CameraUpdateFactory.newCenterPosition(pos, 17),
            CameraAnimation.from(500, true, true)
        )
    }

    private fun showDetailToLocation(labelId: String) {
        with(binding.layoutBottomsheet) {
            tvLocationName.text = labelDatas[labelId]!!.locationName
            tvMemoDescription.text = labelDatas[labelId]!!.description

            BottomSheetBehavior.from(root).peekHeight = kakaomapViewport.height() / 2
            BottomSheetBehavior.from(root).state = BottomSheetBehavior.STATE_COLLAPSED
            setVisible(true)

            btnNavi.setOnClickListener {
                navigation(labelDatas[labelId]!!)
            }
        }
    }
}

data class LabelData(
    val locationName: String,
    val description: String,
    val latLng: LatLng,
)
