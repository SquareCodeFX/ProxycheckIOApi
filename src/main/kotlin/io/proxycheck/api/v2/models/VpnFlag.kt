package io.proxycheck.api.v2.models

/**
 * Enum representing the VPN query flag values that can be used with the ProxyCheck.io API.
 * The VPN flag can have values 0, 1, 2, or 3, each with a different meaning.
 */
enum class VpnFlag(val value: Int) {
    /**
     * Disable VPN detection.
     */
    DISABLED(0),

    /**
     * Enable basic VPN detection.
     */
    ENABLED(1),

    /**
     * Enable enhanced VPN detection.
     */
    ENHANCED(2),

    /**
     * Enable advanced VPN detection.
     */
    ADVANCED(3);

    companion object {
        /**
         * Get the VpnFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding VpnFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): VpnFlag {
            return values().find { it.value == value } ?: ENABLED
        }
    }
}
