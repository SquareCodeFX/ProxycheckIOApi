package io.proxycheck.api.v2.models

/**
 * Enum representing the possible status values in API responses.
 */
enum class ResponseStatus(val value: String) {
    /**
     * The request was successful.
     */
    SUCCESS("success"),

    /**
     * There was an error with the request.
     */
    ERROR("error"),

    /**
     * The request was denied due to invalid credentials or rate limiting.
     */
    DENIED("denied");

    companion object {
        /**
         * Converts a string status to the corresponding enum value.
         *
         * @param status The status string to convert.
         * @return The corresponding ResponseStatus enum value, or null if not found.
         */
        fun fromString(status: String): ResponseStatus? {
            return values().find { it.value == status.lowercase() }
        }
    }
}
