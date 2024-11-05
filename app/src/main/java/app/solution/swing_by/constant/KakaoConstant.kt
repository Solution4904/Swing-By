package app.solution.swing_by.constant


class KakaoConstant {
    companion object {
        const val NATIVE_APP_KEY = "1cd12ba439971ae8db70eda0ffcf0119"
        const val REST_API_KEY = "11ca0d7f7cc9fcbbae804994189a5bea"

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