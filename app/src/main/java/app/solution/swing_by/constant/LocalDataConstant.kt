package app.solution.swing_by.constant

class LocalDataConstant {
    companion object {
        const val USER_PREFERENCE = "USER_PREFERENCE"
        const val UID = "UID"
        const val ID = "ID"
        const val PASSWORD = "PASSWORD"
        const val ACCOUNT_TYPE = "ACCOUNT_TYPE"

        const val OPTION_SEARCHING_LIMIT = "OPTION_SEARCHING_LIMIT"
        const val OPTION_SEARCHING_DISTANCE = "OPTION_SEARCHING_DISTANCE"
    }
}

enum class ACCOUNT_TYPE {
    EMAIL,
    KAKAO,
}