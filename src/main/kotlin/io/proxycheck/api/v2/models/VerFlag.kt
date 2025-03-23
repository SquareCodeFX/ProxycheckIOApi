package io.proxycheck.api.v2.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Class representing the VER query flag value that can be used with the ProxyCheck.io API.
 * The VER flag should be a date in the format DD-Month-YYYY (e.g., 17-August-2021).
 *
 * @property value The date string in the format DD-Month-YYYY.
 */
class VerFlag(val value: String) {
    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd-MMMM-yyyy", Locale.US)

        /**
         * Create a VerFlag with the specified date.
         *
         * @param date The date to use.
         * @return A VerFlag with the specified date.
         */
        fun of(date: Date): VerFlag {
            return VerFlag(DATE_FORMAT.format(date))
        }

        /**
         * Create a VerFlag with the specified date string.
         * The date string should be in the format DD-Month-YYYY (e.g., 17-August-2021).
         *
         * @param dateString The date string to use.
         * @return A VerFlag with the specified date string.
         * @throws IllegalArgumentException If the date string is not in the correct format.
         */
        fun of(dateString: String): VerFlag {
            try {
                // Validate the date string by parsing it
                DATE_FORMAT.parse(dateString)
                return VerFlag(dateString)
            } catch (e: Exception) {
                throw IllegalArgumentException("Date string must be in the format DD-Month-YYYY (e.g., 17-August-2021)", e)
            }
        }

        /**
         * Create a VerFlag with the current date.
         *
         * @return A VerFlag with the current date.
         */
        fun now(): VerFlag {
            return of(Date())
        }
    }
}
