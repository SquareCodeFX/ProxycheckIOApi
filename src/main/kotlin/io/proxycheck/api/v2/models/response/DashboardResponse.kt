package io.proxycheck.api.v2.models.response

import com.google.gson.annotations.SerializedName
import io.proxycheck.api.v2.models.CorsStatus
import io.proxycheck.api.v2.models.CustomList
import io.proxycheck.api.v2.models.DetectionData
import io.proxycheck.api.v2.models.TagData
import io.proxycheck.api.v2.models.UsageData
import io.proxycheck.api.v2.models.enum.ResponseStatus
import io.proxycheck.api.v2.models.response.ApiResponse

/**
 * Represents the response from the ProxyCheck.io Dashboard API.
 */
data class DashboardResponse(
    /**
     * The status of the request as a string.
     * @see statusEnum for the enum representation.
     */
    @SerializedName("status")
    private val statusString: String,

    /**
     * The plan name of the account.
     */
    val plan: String? = null,

    /**
     * The email address associated with the account.
     */
    val email: String? = null,

    /**
     * The queries used in the current day.
     */
    @SerializedName("queries_today")
    val queriesToday: Int? = null,

    /**
     * The queries used in the current month.
     */
    @SerializedName("queries_month")
    val queriesMonth: Int? = null,

    /**
     * The maximum number of queries allowed per day.
     */
    @SerializedName("maxQueries_day")
    val maxQueriesDay: Int? = null,

    /**
     * The maximum number of queries allowed per month.
     */
    @SerializedName("maxQueries_month")
    val maxQueriesMonth: Int? = null,

    /**
     * The number of days left in the current billing period.
     */
    @SerializedName("days_until_reset")
    val daysUntilReset: Int? = null,

    /**
     * The exported detection data.
     * Only available when the EXPORT_DETECTIONS flag is used.
     */
    @SerializedName("detections")
    val detections: Map<String, DetectionData>? = null,

    /**
     * The exported usage data.
     * Only available when the EXPORT_USAGE flag is used.
     */
    @SerializedName("usage")
    val usage: UsageData? = null,

    /**
     * The custom lists data.
     * Only available when the CUSTOM_LISTS flag is used.
     */
    @SerializedName("lists")
    val customLists: List<CustomList>? = null,

    /**
     * The CORS status.
     * Only available when the CORS flag is used.
     */
    @SerializedName("cors")
    val corsStatus: CorsStatus? = null,

    /**
     * The exported tag data.
     * Only available when the EXPORT_TAGS flag is used.
     */
    @SerializedName("tags")
    val tags: Map<String, TagData>? = null,

    /**
     * The message from the API if there was an error.
     */
    override val message: String? = null,

    /**
     * The node that processed the request.
     * Not provided by the Dashboard API, included for ApiResponse interface compatibility.
     */
    override val node: String? = null,

    /**
     * The time it took to process the request in milliseconds.
     * Not provided by the Dashboard API, included for ApiResponse interface compatibility.
     */
    override val time: String? = null
) : ApiResponse {
    /**
     * The status of the request as an enum.
     */
    val statusEnum: ResponseStatus
        get() = ResponseStatus.fromString(statusString) ?: ResponseStatus.ERROR

    /**
     * The status of the request.
     * Implementation of ApiResponse interface.
     */
    override val status: ResponseStatus
        get() = statusEnum
}
