package io.proxycheck.api.v2.models

/**
 * Enum representing the ASN query flag values that can be used with the ProxyCheck.io API.
 * The ASN flag can have values 0 or 1, each with a different meaning.
 */
enum class AsnFlag(override val value: Int) : IntEnumFlag {
    /**
     * Disable ASN data in the response.
     */
    DISABLED(0),

    /**
     * Enable ASN data in the response.
     */
    ENABLED(1);

    companion object {
        /**
         * Get the AsnFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding AsnFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): AsnFlag {
            return FlagUtils.fromValue(value, values(), ENABLED)
        }
    }
}
