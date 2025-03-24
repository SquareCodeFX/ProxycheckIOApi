package io.proxycheck.api.v2.models

import com.google.gson.annotations.SerializedName

/**
 * Represents usage data exported from the ProxyCheck.io Dashboard API.
 */
data class UsageData(
    /**
     * The daily usage data.
     */
    @SerializedName("daily")
    val dailyUsage: Map<String, DailyUsage>? = null,
    
    /**
     * The monthly usage data.
     */
    @SerializedName("monthly")
    val monthlyUsage: Map<String, MonthlyUsage>? = null,
    
    /**
     * The total usage data.
     */
    @SerializedName("total")
    val totalUsage: TotalUsage? = null
)

/**
 * Represents daily usage data.
 */
data class DailyUsage(
    /**
     * The number of queries made on this day.
     */
    val queries: Int? = null,
    
    /**
     * The number of detections made on this day.
     */
    val detections: Int? = null,
    
    /**
     * The date of this usage data.
     */
    val date: String? = null
)

/**
 * Represents monthly usage data.
 */
data class MonthlyUsage(
    /**
     * The number of queries made in this month.
     */
    val queries: Int? = null,
    
    /**
     * The number of detections made in this month.
     */
    val detections: Int? = null,
    
    /**
     * The month of this usage data.
     */
    val month: String? = null,
    
    /**
     * The year of this usage data.
     */
    val year: String? = null
)

/**
 * Represents total usage data.
 */
data class TotalUsage(
    /**
     * The total number of queries made.
     */
    val queries: Int? = null,
    
    /**
     * The total number of detections made.
     */
    val detections: Int? = null
)
