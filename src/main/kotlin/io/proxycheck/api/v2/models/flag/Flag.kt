package io.proxycheck.api.v2.models.flag

/**
 * Interface for all flag types used with the ProxyCheck.io API.
 */
interface Flag<T> {
    /**
     * The value of the flag.
     */
    val value: T
}

/**
 * Interface for enum flags with integer values.
 */
interface IntEnumFlag : Flag<Int> {
    companion object {
        /**
         * Generic method to convert an integer value to an enum flag.
         *
         * @param value The integer value to convert.
         * @param values The array of enum values to search.
         * @param defaultValue The default value to return if no match is found.
         * @return The corresponding enum flag, or the default value if no match is found.
         */
        inline fun <reified T> fromValue(value: Int, values: Array<T>, defaultValue: T): T where T : Enum<T>, T : IntEnumFlag {
            return values.find { it.value == value } ?: defaultValue
        }
    }
}

/**
 * Utility class for flag operations.
 */
object FlagUtils {
    /**
     * Generic method to convert an integer value to an enum flag.
     * This is a utility method that can be used by all enum flag classes to reduce repetitive code.
     *
     * @param value The integer value to convert.
     * @param values The array of enum values to search.
     * @param defaultValue The default value to return if no match is found.
     * @return The corresponding enum flag, or the default value if no match is found.
     */
    inline fun <reified T> fromValue(value: Int, values: Array<T>, defaultValue: T): T where T : Enum<T>, T : IntEnumFlag {
        return values.find { it.value == value } ?: defaultValue
    }
}


/**
 * Interface for string value flags.
 */
interface StringFlag : Flag<String>

/**
 * Interface for integer value flags.
 */
interface IntFlag : Flag<Int>
