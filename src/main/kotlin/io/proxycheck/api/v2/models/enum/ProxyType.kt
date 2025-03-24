package io.proxycheck.api.v2.models.enum

/**
 * Enum representing the possible proxy types in ProxyCheck.io API responses.
 */
enum class ProxyType(val value: String) {
    /**
     * VPN proxy type.
     */
    VPN("VPN"),

    /**
     * TOR proxy type.
     */
    TOR("TOR"),

    /**
     * Public proxy type.
     */
    PUBLIC("Public"),

    /**
     * Residential proxy type.
     */
    RESIDENTIAL("Residential"),

    /**
     * Web proxy type.
     */
    WEB("Web"),

    /**
     * Hosting provider.
     */
    HOSTING("Hosting"),

    /**
     * Unknown proxy type.
     */
    UNKNOWN("Unknown");

    companion object {
        /**
         * Converts a string proxy type to the corresponding enum value.
         *
         * @param type The proxy type string to convert.
         * @return The corresponding ProxyType enum value, or UNKNOWN if not found.
         */
        fun fromString(type: String?): ProxyType {
            if (type == null) return UNKNOWN
            return values().find { it.value.equals(type, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
