package io.proxycheck.api.v2.exceptions

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
