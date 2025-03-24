package io.proxycheck.api.v2.models.flag

import io.proxycheck.api.v2.models.flag.FlagUtils
import io.proxycheck.api.v2.models.flag.IntEnumFlag

/**
 * Enum representing the NODE query flag values that can be used with the ProxyCheck.io API.
 * The NODE flag can have values 0 or 1, each with a different meaning.
 */
enum class NodeFlag(override val value: Int) : IntEnumFlag {
    /**
     * Disable node information in the response.
     */
    DISABLED(0),

    /**
     * Enable node information in the response.
     */
    ENABLED(1);

    companion object {
        /**
         * Get the NodeFlag enum from an integer value.
         *
         * @param value The integer value to convert.
         * @return The corresponding NodeFlag enum, or ENABLED if the value is not valid.
         */
        fun fromValue(value: Int): NodeFlag {
            return FlagUtils.fromValue(value, values(), ENABLED)
        }
    }
}
