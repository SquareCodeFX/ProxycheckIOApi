package io.proxycheck.api.v2.models

/**
 * Enum representing the RISK query flag values that can be used with the ProxyCheck.io API.
 * The RISK flag can have values 0, 1, or 2, each with a different meaning.
 */
enum class RiskFlag(override val value: Int) : IntEnumFlag {
    /**
     * Disable risk information in the response.
     */
    DISABLED(0),

    /**
     * Enable basic risk information in the response.
     */
    ENABLED(1),

    /**
     * Enable enhanced risk information in the response.
     */
    ENHANCED(2);

    companion object {
        /**
         * Get the RiskFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding RiskFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): RiskFlag {
            return FlagUtils.fromValue(value, values(), ENABLED)
        }
    }
}
