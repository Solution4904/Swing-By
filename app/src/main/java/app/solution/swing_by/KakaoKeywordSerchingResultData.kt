package app.solution.swing_by

import com.google.gson.annotations.SerializedName


data class KeywordSerchingResultData(
    @SerializedName("documents")
    val documents: List<Document>
)

data class Document(
    @SerializedName("place_name")
    val place_name: String,

    @SerializedName("address_name")
    val address_name: String,

    @SerializedName("category_name")
    val category_name: String,

    @SerializedName("distance")
    val distance: String,

    @SerializedName("x")
    val x: String,   // longitude

    @SerializedName("y")
    val y: String,   // latitude
)
