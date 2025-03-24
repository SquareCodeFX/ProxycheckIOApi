package io.proxycheck.api.v2.models.flag

import io.proxycheck.api.v2.models.flag.FlagUtils
import io.proxycheck.api.v2.models.flag.IntEnumFlag

/**
 * Enum representing the TIME query flag values that can be used with the ProxyCheck.io API.
 * The TIME flag can have values 0 or 1, each with a different meaning.
 */
enum class TimeFlag(override val value: Int) : IntEnumFlag {
    /**
     * Disable time information in the response.
     */
    DISABLED(0),

    /**
     * Enable time information in the response.
     */
    ENABLED(1);

    companion object {
        /**
         * Get the TimeFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding TimeFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): TimeFlag {
            return FlagUtils.fromValue(value, values(), ENABLED)
        }
    }
}
