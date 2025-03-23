package io.proxycheck.api.v2.models

/**
 * Enum representing the possible proxy status values in ProxyCheck.io API responses.
 */
enum class ProxyStatus(val value: String) {
    /**
     * The IP address is a proxy.
     */
    YES("yes"),

    /**
     * The IP address is not a proxy.
     */
    NO("no"),

    /**
     * The proxy status of the IP address is unknown.
     */
    UNKNOWN("unknown");

    companion object {
        /**
         * Converts a string proxy status to the corresponding enum value.
         *
         * @param status The proxy status string to convert.
         * @return The corresponding ProxyStatus enum value, or UNKNOWN if not found.
         */
        fun fromString(status: String?): ProxyStatus {
            if (status == null) return UNKNOWN
            return values().find { it.value.equals(status, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
