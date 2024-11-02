package app.solution.swing_by

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import app.solution.swing_by.feature.MapActivity

// TODO: 특정 기능들을 수정 혹은 작성하고 나면 이미 홈화면에 추가되어있던 위젯 기능이 작동하지 않는 문제가 있음. onupdate에만 위젯 기능들 추가 관련이 있어서 그런거 같음. 확인할 것
class MyAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 위젯 업데이트 로직을 구현
        appWidgetIds.forEach { appWidgetId ->
            // 액티비티를 실행할 인텐트 정의
            val pendingIntent: PendingIntent = Intent(context, MapActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                }

            // 앱 위젯의 레이아웃 가져옴. 버튼에 클릭 리스너 연결
            val views: RemoteViews = RemoteViews(context.packageName, R.layout.widget).apply {
                setOnClickPendingIntent(R.id.btn_widget, pendingIntent)
            }

            // 앱 위젯에서 업데이트 수행하도록 AppWidgetManager 에 알림
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }
}

/*companion object {
    const val ACTION_BUTTON_CLICK = "com.example.widget.ACTION_BUTTON_CLICK"
}

override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
) {
    for (appWidgetId in appWidgetIds) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }
}

private fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // RemoteViews 객체로 위젯 레이아웃 참조
    val views = RemoteViews(context.packageName, R.layout.widget)

    // 버튼 클릭 시 Broadcast를 보낼 PendingIntent 설정
    val intent = Intent(context, MyAppWidgetProvider::class.java).apply {
        action = ACTION_BUTTON_CLICK
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.btn_widget, pendingIntent)

    // 위젯 업데이트 수행
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)

    if (intent.action == ACTION_BUTTON_CLICK) {
        // TODO: 기능 실행 확인했음
        */
/** < 1 안 >
 *   1. 위치 권한 확인
 *   2. 로그인 이력 확인
 *   3. 계정 메모 리스트 불러오기
 *   4. 현재 위치 추적
 *   5. 카카오맵으로 리스트 키워드 검색
 *   6. 검색 결과 푸시알림
 *   7. 푸시 알림 클릭 시 카카오맵 열고 각 위치들에 마커
 *
 *
 *
 *   < 2 안 >
 *   1. 등록해두웠던 장소들을 맵핀해서 보여주는 액티비티로 진입
 *//*

            // 1. 위치 권한 확인
            checkPermissons(context) {
                // 2. 로그인 이력 확인
                checkAccount {
                    // 3. 계정 메모 리스트 불러오기
                    val list = getMemoList()
                    list.forEach { keyword ->
                        Log.d("SOL_LOG", "keyword : $keyword")
                    }

                    // 4. 현재 위치 획득
                    getMyLocation(context)
                }
            }


            // 5. 카카오맵으로 리스트 키워드 검색


            *//*//*/ 버튼 클릭 시 텍스트 변경 및 Toast 메시지 표시
            Toast.makeText(context, "Button Clicked!", Toast.LENGTH_SHORT).show()

            val views = RemoteViews(context.packageName, R.layout.widget)
            views.setTextViewText(R.id.btn_widget, "Clicked!")

            // 위젯 ID 가져오기
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                intent.component
            )

            // 각 위젯에 텍스트 변경 적용
            appWidgetIds.forEach { appWidgetId ->
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }*//*
        }
    }

    private fun checkPermissons(context: Context, success: () -> Unit) {
        TedPermission.create().apply {
            setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                    success.invoke()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                }
            })
            setDeniedMessage("서비스를 이용하시려면 위치 권한이 필요합니다.")
            setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ).check()
        }
    }

    private fun checkAccount(success: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.UID)
        }.invokeOnCompletion {
            success.invoke()
        }
    }

    private fun getMemoList(): Array<String> {
        val keywords = ArrayList<String>()
        Log.d("SOL_LOG", "1111111111111")
        FirebaseAPI.getMemoList(object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(result: DataSnapshot?) {
                Log.d("SOL_LOG", "2222222222222")
                result?.let { dataSnapshot ->
                    dataSnapshot.children.map { snapshot ->
                        Log.d("SOL_LOG", "3333333333333")
                        snapshot.getValue(MemoItem::class.java)?.location?.let { keyword ->
                            keywords.add(keyword)
                        }
                    }
                }
            }

            override fun successCallback(result: Task<AuthResult>) {}
            override fun failureCallback() {
                Log.d("SOL_LOG", "4444444444444")
            }
        })

        return keywords.toTypedArray()
    }

    // # 현재 위치 정보 불러오기
    @SuppressLint("MissingPermission")
    private fun getMyLocation(context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener {
                it?.let {
                    Log.d("SOL_LOG", "getCurrentLocation: ${it.longitude} / ${it.latitude}")
                }
            }
    }
}*/