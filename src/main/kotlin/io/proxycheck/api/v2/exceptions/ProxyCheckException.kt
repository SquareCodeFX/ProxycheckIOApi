package io.proxycheck.api.v2.exceptions

import com.google.gson.JsonObject

/**
 * Base exception class for all ProxyCheck.io API exceptions.
 *
 * @param message The error message.
 * @param cause The cause of the exception.
 */
open class ProxyCheckException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when the API key is invalid or missing.
 *
 * @param message The error message.
 * @param cause The cause of the exception.
 */
class ApiKeyException(message: String, cause: Throwable? = null) : ProxyCheckException(message, cause)

/**
 * Exception thrown when the API rate limit is exceeded.
 *
 * @param message The error message.
 * @param cause The cause of the exception.
 */
class RateLimitException(message: String, cause: Throwable? = null) : ProxyCheckException(message, cause)

/**
 * Exception thrown when the API request is invalid.
 *
 * @param message The error message.
 * @param cause The cause of the exception.
 */
class InvalidRequestException(message: String, cause: Throwable? = null) : ProxyCheckException(message, cause)

/**
 * Exception thrown when the API returns an error.
 *
 * @param message The error message.
 * @param cause The cause of the exception.
 */
class ApiErrorException(message: String, cause: Throwable? = null) : ProxyCheckException(message, cause)

/**
 * Exception thrown when the API returns a warning.
 *
 * @param message The warning message.
 * @param cause The cause of the exception.
 */
class ApiWarningException(message: String, cause: Throwable? = null) : ProxyCheckException(message, cause)

/**
 * Exception thrown when the API denies the request.
 *
 * @param message The denial message.
 * @param cause The cause of the exception.
 */
class ApiDeniedException(message: String, cause: Throwable? = null) : ProxyCheckException(message, cause)

/**
 * Exception thrown when the plan limits are exceeded.
 *
 * @param message The error message.
 * @param cause The cause of the exception.
 * @param plan The current plan.
 * @param queriesToday The number of queries used today.
 * @param queriesMonth The number of queries used this month.
 * @param maxQueriesDay The maximum number of queries allowed per day.
 * @param maxQueriesMonth The maximum number of queries allowed per month.
 * @param daysUntilReset The number of days until the query count resets.
 */
class PlanLimitException(
    message: String,
    cause: Throwable? = null,
    val plan: String? = null,
    val queriesToday: Int? = null,
    val queriesMonth: Int? = null,
    val maxQueriesDay: Int? = null,
    val maxQueriesMonth: Int? = null,
    val daysUntilReset: Int? = null
) : ProxyCheckException(message, cause)

/**
 * Exception thrown when there is a network error.
 *
 * @param message The error message.
 * @param cause The cause of the exception.
 */
class NetworkException(message: String, cause: Throwable? = null) : ProxyCheckException(message, cause)

/**
 * Utility class for handling ProxyCheck API exceptions.
 */
object ExceptionHandler {
    /**
     * Creates and throws the appropriate exception based on the API response.
     *
     * @param status The API status code.
     * @param message The error message.
     * @param jsonObject The JSON response object, which may contain additional information.
     * @throws ProxyCheckException The appropriate exception based on the status and message.
     */
    fun handleApiError(status: String, message: String, jsonObject: JsonObject? = null) {
        when (status) {
            "error" -> {
                when {
                    message.contains("API key") -> throw ApiKeyException(message)
                    message.contains("rate limit") -> throw RateLimitException(message)
                    message.contains("plan limit") || message.contains("query limit") -> {
                        // Extract plan information if available
                        if (jsonObject != null && jsonObject.has("plan")) {
                            val plan = jsonObject.get("plan").asString
                            val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                            val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                            val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                            val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                            val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                            throw PlanLimitException(
                                message,
                                null,
                                plan,
                                queriesToday,
                                queriesMonth,
                                maxQueriesDay,
                                maxQueriesMonth,
                                daysUntilReset
                            )
                        } else {
                            throw PlanLimitException(message)
                        }
                    }
                    else -> throw ApiErrorException(message)
                }
            }
            "warning" -> throw ApiWarningException(message)
            "denied" -> throw ApiDeniedException(message)
            "ok", "success" -> {
                // These are success statuses, no exception needed
                return
            }
            else -> throw ApiErrorException("Unknown status: $status - $message")
        }
    }
}
