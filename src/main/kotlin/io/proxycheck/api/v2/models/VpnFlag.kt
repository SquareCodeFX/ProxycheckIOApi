package io.proxycheck.api.v2.models

/**
 * Enum representing the VPN query flag values that can be used with the ProxyCheck.io API.
 * The VPN flag can have values 0 or 1, each with a different meaning.
 */
enum class VpnFlag(val value: Int) {
    /**
     * Disable VPN detection.
     */
    DISABLED(0),

    /**
     * Enable VPN detection.
     */
    ENABLED(1);

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
