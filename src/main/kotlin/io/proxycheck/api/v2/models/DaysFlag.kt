package io.proxycheck.api.v2.models

/**
 * Class representing the DAYS query flag value that can be used with the ProxyCheck.io API.
 * The DAYS flag can have any integer value, representing the number of days.
 *
 * @property value The integer value for the days flag.
 */
class DaysFlag(override val value: Int) : IntFlag {
    companion object {
        /**
         * Create a DaysFlag with the specified number of days.
         *
         * @param days The number of days.
         * @return A DaysFlag with the specified number of days.
         */
        fun of(days: Int): DaysFlag {
            return DaysFlag(days)
        }

        /**
         * Create a DaysFlag with a default value of 7 days.
         *
         * @return A DaysFlag with a default value of 7 days.
         */
        fun default(): DaysFlag {
            return DaysFlag(7)
        }
    }
}
