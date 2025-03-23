package io.proxycheck.api.v2.models

/**
 * Enum representing the query flags that can be used with the ProxyCheck.io API.
 */
enum class QueryFlag(val value: String) {
    /**
     * Returns VPN status of the IP address.
     */
    VPN("vpn"),

    /**
     * Returns ASN details of the IP address.
     */
    ASN("asn"),

    /**
     * Returns the node that processed the request.
     */
    NODE("node"),

    /**
     * Returns the time it took to process the request.
     */
    TIME("time"),

    /**
     * Returns the risk score of the IP address.
     */
    RISK("risk"),

    /**
     * Returns the port used by the proxy.
     */
    PORT("port"),

    /**
     * Returns the seen date of the proxy.
     */
    SEEN("seen"),

    /**
     * Returns the days since the proxy was first detected.
     */
    DAYS("days"),

    /**
     * Returns the country of the IP address.
     */
    COUNTRY("country"),

    /**
     * Returns the isocode of the IP address.
     */
    ISOCODE("isocode"),

    /**
     * Returns the proxy type of the IP address.
     */
    PROXY_TYPE("proxy"),

    /**
     * Returns the provider of the IP address.
     */
    PROVIDER("provider"),

    /**
     * Returns the TOR status of the IP address.
     */
    TOR("tor"),

    /**
     * Returns the residential status of the IP address.
     */
    RESIDENTIAL("residential"),

    /**
     * Returns the mobile status of the IP address.
     */
    MOBILE("mobile"),

    /**
     * Returns the hosting status of the IP address.
     */
    HOSTING("hosting"),

    /**
     * Returns the city of the IP address.
     */
    CITY("city"),

    /**
     * Returns the region/state of the IP address.
     */
    REGION("region"),

    /**
     * Returns the organization of the IP address.
     */
    ORGANIZATION("organization"),

    /**
     * Returns the hostname of the IP address.
     */
    HOSTNAME("hostname"),

    /**
     * Returns the ISP of the IP address.
     */
    ISP("isp"),

    /**
     * Enables email checking for disposable email providers.
     */
    MAIL("mail");

    companion object {
        /**
         * Converts a list of query flags to a comma-separated string.
         *
         * @param flags The list of query flags to convert.
         * @return A comma-separated string of query flags.
         */
        fun toQueryString(flags: List<QueryFlag>): String {
            return flags.joinToString(",") { it.value }
        }
    }
}
