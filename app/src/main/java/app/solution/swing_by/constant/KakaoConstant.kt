package app.solution.swing_by.constant

import app.solution.swing_by.BuildConfig


class KakaoConstant {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/v2/local/"

        const val MARKET_SCHEME = "market://details?id=net.daum.android.map"
    }
}

/**
 * Kakao category group code
 * M1   -   대형마트
 * CS2  -   편의점
 * BK9  -   은행
 * PO3  -   공공기관
 * FD6  -   음식점
 * CE7  -   카페
 * PM9  -   약국
 * @constructor Create empty Kakao category group code
 */
enum class KakaoCategoryGroupCode {
    MT1,
    CS2,
    BK9,
    PO3,
    FD6,
    CE7,
    PM9,
}