package io.proxycheck.api.v2.models

/**
 * Enum representing the SEEN query flag values that can be used with the ProxyCheck.io API.
 * The SEEN flag can have values 0 or 1, each with a different meaning.
 */
enum class SeenFlag(override val value: Int) : IntEnumFlag {
    /**
     * Disable seen date information in the response.
     */
    DISABLED(0),

    /**
     * Enable seen date information in the response.
     */
    ENABLED(1);

    companion object {
        /**
         * Get the SeenFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding SeenFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): SeenFlag {
            return FlagUtils.fromValue(value, values(), ENABLED)
        }
    }
}
