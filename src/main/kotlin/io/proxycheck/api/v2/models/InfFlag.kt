package io.proxycheck.api.v2.models

/**
 * Enum representing the INF query flag values that can be used with the ProxyCheck.io API.
 * The INF flag can have values 0 or 1, each with a different meaning.
 */
enum class InfFlag(override val value: Int) : IntEnumFlag {
    /**
     * Disable additional information in the response.
     */
    DISABLED(0),

    /**
     * Enable additional information in the response.
     */
    ENABLED(1);

    companion object {
        /**
         * Get the InfFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding InfFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): InfFlag {
            return FlagUtils.fromValue(value, values(), ENABLED)
        }
    }
}
