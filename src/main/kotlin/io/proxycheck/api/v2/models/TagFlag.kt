package io.proxycheck.api.v2.models

/**
 * Class representing the TAG query flag value that can be used with the ProxyCheck.io API.
 * The TAG flag can have any string value, used for custom query tagging.
 *
 * @property value The string value for the tag flag.
 */
class TagFlag(override val value: String) : StringFlag {
    companion object {
        /**
         * Create a TagFlag with the specified tag string.
         *
         * @param tag The custom tag string.
         * @return A TagFlag with the specified tag string.
         */
        fun of(tag: String): TagFlag {
            return TagFlag(tag)
        }
    }
}
