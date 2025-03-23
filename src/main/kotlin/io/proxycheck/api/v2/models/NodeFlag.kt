package io.proxycheck.api.v2.models

/**
 * Enum representing the NODE query flag values that can be used with the ProxyCheck.io API.
 * The NODE flag can have values 0 or 1, each with a different meaning.
 */
enum class NodeFlag(val value: Int) {
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
            return values().find { it.value == value } ?: ENABLED
        }
    }
}
