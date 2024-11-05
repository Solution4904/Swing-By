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