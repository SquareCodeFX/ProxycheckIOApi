package io.proxycheck.api.v2

import io.proxycheck.api.v2.models.DashboardResponse
import io.proxycheck.api.v2.models.EmailCheckResponse
import io.proxycheck.api.v2.models.ProxyCheckResponse
import io.proxycheck.api.v2.models.QueryFlag

/**
 * Interface for the ProxyCheck.io API client.
 */
interface ProxyCheckApiInterface {
    /**
     * Checks a single IP address for proxy information.
     *
     * @param ip The IP address to check.
     * @param flags The query flags to use.
     * @param vpnDetection Whether to enable VPN detection.
     * @param asn Whether to include ASN information.
     * @param node Whether to include the node that processed the request.
     * @param time Whether to include the time it took to process the request.
     * @param risk Whether to include the risk score.
     * @param port Whether to include the port used by the proxy.
     * @param seen Whether to include the seen date of the proxy.
     * @param days Whether to include the days since the proxy was first detected.
     * @param tag A custom tag for the request.
     * @param useSSL Whether to use SSL for the request.
     * @return A ProxyCheckResponse object containing the proxy information.
     */
    fun checkIp(
        ip: String,
        flags: List<QueryFlag> = emptyList(),
        vpnDetection: Boolean = false,
        asn: Boolean = false,
        node: Boolean = false,
        time: Boolean = false,
        risk: Boolean = false,
        port: Boolean = false,
        seen: Boolean = false,
        days: Boolean = false,
        tag: String? = null,
        useSSL: Boolean = true
    ): ProxyCheckResponse

    /**
     * Checks multiple IP addresses for proxy information.
     *
     * @param ips The list of IP addresses to check.
     * @param flags The query flags to use.
     * @param vpnDetection Whether to enable VPN detection.
     * @param asn Whether to include ASN information.
     * @param node Whether to include the node that processed the request.
     * @param time Whether to include the time it took to process the request.
     * @param risk Whether to include the risk score.
     * @param port Whether to include the port used by the proxy.
     * @param seen Whether to include the seen date of the proxy.
     * @param days Whether to include the days since the proxy was first detected.
     * @param tag A custom tag for the request.
     * @param useSSL Whether to use SSL for the request.
     * @return A map of IP addresses to ProxyCheckResponse objects.
     */
    fun checkIps(
        ips: List<String>,
        flags: List<QueryFlag> = emptyList(),
        vpnDetection: Boolean = false,
        asn: Boolean = false,
        node: Boolean = false,
        time: Boolean = false,
        risk: Boolean = false,
        port: Boolean = false,
        seen: Boolean = false,
        days: Boolean = false,
        tag: String? = null,
        useSSL: Boolean = true
    ): Map<String, ProxyCheckResponse>

    /**
     * Gets the dashboard information for the account.
     *
     * @return A DashboardResponse object containing the dashboard information.
     */
    fun getDashboard(): DashboardResponse

    /**
     * Checks if the given email address is from a disposable email provider.
     *
     * @param email The email address to check.
     * @param flags The query flags to include in the request.
     * @param node Whether to include the node that processed the request in the response.
     * @param time Whether to include the time it took to process the request in the response.
     * @param risk Whether to include the risk score in the response.
     * @param tag A custom tag to identify the request.
     * @param useSSL Whether to use SSL for the request.
     * @return An EmailCheckResponse object containing the response from the API.
     */
    fun checkEmail(
        email: String,
        flags: List<QueryFlag> = listOf(QueryFlag.MAIL),
        node: Boolean = false,
        time: Boolean = false,
        risk: Boolean = false,
        tag: String? = null,
        useSSL: Boolean = true
    ): EmailCheckResponse
}
