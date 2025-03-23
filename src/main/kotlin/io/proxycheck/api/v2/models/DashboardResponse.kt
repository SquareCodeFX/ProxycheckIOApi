package io.proxycheck.api.v2.models

import com.google.gson.annotations.SerializedName

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
