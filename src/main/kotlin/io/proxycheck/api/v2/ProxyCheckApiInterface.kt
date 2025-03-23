package io.proxycheck.api.v2

import io.proxycheck.api.v2.models.*

/**
 * Interface for the ProxyCheck.io API client.
 */
interface ProxyCheckApiInterface {
    /**
     * Checks a single IP address for proxy information.
     *
     * @param ip The IP address to check.
     * @param options Optional parameters for the request.
     * @return A ProxyCheckResponse object containing the proxy information.
     */
    fun checkIp(
        ip: String,
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): ProxyCheckResponse

    /**
     * Checks multiple IP addresses for proxy information.
     *
     * @param ips The list of IP addresses to check.
     * @param options Optional parameters for the request.
     * @return A map of IP addresses to ProxyCheckResponse objects.
     */
    fun checkIps(
        ips: List<String>,
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): Map<String, ProxyCheckResponse>

    /**
     * Gets the dashboard information for the account.
     *
     * @param options Optional parameters for the request.
     * @return A DashboardResponse object containing the dashboard information.
     */
    fun getDashboard(
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): DashboardResponse

    /**
     * Checks if the given email address is from a disposable email provider.
     *
     * @param email The email address to check.
     * @param options Optional parameters for the request.
     * @return An EmailCheckResponse object containing the response from the API.
     */
    fun checkEmail(
        email: String,
        options: ProxyCheckOptions = ProxyCheckOptions(flags = listOf(QueryFlag.MAIL))
    ): EmailCheckResponse
}
