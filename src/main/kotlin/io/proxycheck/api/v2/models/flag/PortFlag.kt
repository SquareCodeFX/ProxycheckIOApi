package io.proxycheck.api.v2.models.flag

/**
 * Enum representing the PORT query flag values that can be used with the ProxyCheck.io API.
 * The PORT flag can have values 0 or 1, each with a different meaning.
 */
enum class PortFlag(override val value: Int) : IntEnumFlag {
    /**
     * Disable port information in the response.
     */
    DISABLED(0),

    /**
     * Enable port information in the response.
     */
    ENABLED(1);

    companion object {
        /**
         * Get the PortFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding PortFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): PortFlag {
            return FlagUtils.fromValue(value, values(), ENABLED)
        }
    }
}
