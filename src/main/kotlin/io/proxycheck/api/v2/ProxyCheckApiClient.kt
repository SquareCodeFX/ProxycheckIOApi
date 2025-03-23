package io.proxycheck.api.v2

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.proxycheck.api.v2.cache.ResponseCache
import io.proxycheck.api.v2.exceptions.*
import io.proxycheck.api.v2.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Main client class for interacting with the ProxyCheck.io API v2.
 *
 * @property apiKey The API key for authentication with ProxyCheck.io.
 * @property client The OkHttpClient instance for making HTTP requests.
 * @property baseUrl The base URL for the ProxyCheck.io API.
 * @property gson The Gson instance for JSON serialization/deserialization.
 * @property enableCaching Whether to enable caching of API responses.
 * @property defaultCacheTime The default time to cache API responses.
 * @property defaultCacheTimeUnit The default time unit for caching API responses.
 */
class ProxyCheckApiClient(
    private val apiKey: String? = null,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build(),
    private val baseUrl: String = "https://proxycheck.io/v2/",
    private val gson: Gson = Gson(),
    private val enableCaching: Boolean = false,
    private val defaultCacheTime: Long = 5,
    private val defaultCacheTimeUnit: TimeUnit = TimeUnit.MINUTES
) {
    // Cache for single IP responses
    private val ipCache = ResponseCache<ProxyCheckResponse>()

    // Cache for multiple IP responses
    private val ipsCache = ResponseCache<Map<String, ProxyCheckResponse>>()

    // Cache for dashboard responses
    private val dashboardCache = ResponseCache<DashboardResponse>()

    // Cache for email responses
    private val emailCache = ResponseCache<EmailCheckResponse>()
    /**
     * Extension function to make OkHttp's Call work with coroutines.
     */
    private suspend fun Call.await(): Response = suspendCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        })
    }
    /**
     * Checks if the given IP address is a proxy.
     *
     * @param ip The IP address to check.
     * @param options Optional parameters for the request.
     * @return A ProxyCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    fun checkIp(
        ip: String,
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): ProxyCheckResponse {
        val flagsList = options.flags.toMutableList()

        // Add boolean flags if they are true
        if (options.node) flagsList.add(QueryFlag.NODE)
        if (options.time) flagsList.add(QueryFlag.TIME)
        if (options.risk) flagsList.add(QueryFlag.RISK)
        if (options.port) flagsList.add(QueryFlag.PORT)
        if (options.seen) flagsList.add(QueryFlag.SEEN)
        if (options.days) flagsList.add(QueryFlag.DAYS)

        val url = (baseUrl + ip).toHttpUrl().newBuilder().apply {
            // Add API key if provided
            if (apiKey != null) {
                addQueryParameter("key", apiKey)
            }

            // Add flags if provided
            if (flagsList.isNotEmpty()) {
                addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
            }

            // Add VPN flag with numeric value if provided, otherwise use boolean flag
            if (options.vpnFlag != null) {
                addQueryParameter("vpn", options.vpnFlag.value.toString())
            } else if (options.vpnDetection) {
                addQueryParameter("vpn", "1")
            }

            // Add ASN flag with numeric value if provided, otherwise use boolean flag
            if (options.asnFlag != null) {
                addQueryParameter("asn", options.asnFlag.value.toString())
            } else if (options.asn) {
                addQueryParameter("asn", "1")
            }

            // Add NODE flag with numeric value if provided, otherwise use boolean flag
            if (options.nodeFlag != null) {
                addQueryParameter("node", options.nodeFlag.value.toString())
            } else if (options.node) {
                addQueryParameter("node", "1")
            }

            // Add TIME flag with numeric value if provided, otherwise use boolean flag
            if (options.timeFlag != null) {
                addQueryParameter("time", options.timeFlag.value.toString())
            } else if (options.time) {
                addQueryParameter("time", "1")
            }

            // Add INF flag with numeric value if provided, otherwise use boolean flag
            if (options.infFlag != null) {
                addQueryParameter("inf", options.infFlag.value.toString())
            } else if (options.inf) {
                addQueryParameter("inf", "1")
            }

            // Add RISK flag with numeric value if provided, otherwise use boolean flag
            if (options.riskFlag != null) {
                addQueryParameter("risk", options.riskFlag.value.toString())
            } else if (options.risk) {
                addQueryParameter("risk", "1")
            }

            // Add PORT flag with numeric value if provided, otherwise use boolean flag
            if (options.portFlag != null) {
                addQueryParameter("port", options.portFlag.value.toString())
            } else if (options.port) {
                addQueryParameter("port", "1")
            }

            // Add SEEN flag with numeric value if provided, otherwise use boolean flag
            if (options.seenFlag != null) {
                addQueryParameter("seen", options.seenFlag.value.toString())
            } else if (options.seen) {
                addQueryParameter("seen", "1")
            }

            // Add DAYS flag with numeric value if provided, otherwise use boolean flag
            if (options.daysFlag != null) {
                addQueryParameter("days", options.daysFlag.value.toString())
            } else if (options.days) {
                addQueryParameter("days", "1")
            }

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeRequest(request, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Checks if the given IP address is a proxy asynchronously.
     *
     * @param ip The IP address to check.
     * @param options Optional parameters for the request.
     * @return A ProxyCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    suspend fun checkIpAsync(
        ip: String,
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): ProxyCheckResponse {
        val flagsList = options.flags.toMutableList()

        // Add boolean flags if they are true
        if (options.node) flagsList.add(QueryFlag.NODE)
        if (options.time) flagsList.add(QueryFlag.TIME)
        if (options.risk) flagsList.add(QueryFlag.RISK)
        if (options.port) flagsList.add(QueryFlag.PORT)
        if (options.seen) flagsList.add(QueryFlag.SEEN)
        if (options.days) flagsList.add(QueryFlag.DAYS)

        val url = (baseUrl + ip).toHttpUrl().newBuilder().apply {
            // Add API key if provided
            if (apiKey != null) {
                addQueryParameter("key", apiKey)
            }

            // Add flags if provided
            if (flagsList.isNotEmpty()) {
                addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
            }

            // Add VPN flag with numeric value if provided, otherwise use boolean flag
            if (options.vpnFlag != null) {
                addQueryParameter("vpn", options.vpnFlag.value.toString())
            } else if (options.vpnDetection) {
                addQueryParameter("vpn", "1")
            }

            // Add ASN flag with numeric value if provided, otherwise use boolean flag
            if (options.asnFlag != null) {
                addQueryParameter("asn", options.asnFlag.value.toString())
            } else if (options.asn) {
                addQueryParameter("asn", "1")
            }

            // Add NODE flag with numeric value if provided, otherwise use boolean flag
            if (options.nodeFlag != null) {
                addQueryParameter("node", options.nodeFlag.value.toString())
            } else if (options.node) {
                addQueryParameter("node", "1")
            }

            // Add TIME flag with numeric value if provided, otherwise use boolean flag
            if (options.timeFlag != null) {
                addQueryParameter("time", options.timeFlag.value.toString())
            } else if (options.time) {
                addQueryParameter("time", "1")
            }

            // Add INF flag with numeric value if provided, otherwise use boolean flag
            if (options.infFlag != null) {
                addQueryParameter("inf", options.infFlag.value.toString())
            } else if (options.inf) {
                addQueryParameter("inf", "1")
            }

            // Add RISK flag with numeric value if provided, otherwise use boolean flag
            if (options.riskFlag != null) {
                addQueryParameter("risk", options.riskFlag.value.toString())
            } else if (options.risk) {
                addQueryParameter("risk", "1")
            }

            // Add PORT flag with numeric value if provided, otherwise use boolean flag
            if (options.portFlag != null) {
                addQueryParameter("port", options.portFlag.value.toString())
            } else if (options.port) {
                addQueryParameter("port", "1")
            }

            // Add SEEN flag with numeric value if provided, otherwise use boolean flag
            if (options.seenFlag != null) {
                addQueryParameter("seen", options.seenFlag.value.toString())
            } else if (options.seen) {
                addQueryParameter("seen", "1")
            }

            // Add DAYS flag with numeric value if provided, otherwise use boolean flag
            if (options.daysFlag != null) {
                addQueryParameter("days", options.daysFlag.value.toString())
            } else if (options.days) {
                addQueryParameter("days", "1")
            }

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeRequestAsync(request, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Checks multiple IP addresses in a single request.
     *
     * @param ips The list of IP addresses to check.
     * @param options Optional parameters for the request.
     * @return A map of IP addresses to ProxyCheckResponse objects.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    fun checkIps(
        ips: List<String>,
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): Map<String, ProxyCheckResponse> {
        if (ips.isEmpty()) {
            return emptyMap()
        }

        val flagsList = options.flags.toMutableList()

        // Add boolean flags if they are true
        if (options.node) flagsList.add(QueryFlag.NODE)
        if (options.time) flagsList.add(QueryFlag.TIME)
        if (options.risk) flagsList.add(QueryFlag.RISK)
        if (options.port) flagsList.add(QueryFlag.PORT)
        if (options.seen) flagsList.add(QueryFlag.SEEN)
        if (options.days) flagsList.add(QueryFlag.DAYS)

        val url = baseUrl.toHttpUrl().newBuilder().apply {
            // Add API key if provided
            if (apiKey != null) {
                addQueryParameter("key", apiKey)
            }

            // Add flags if provided
            if (flagsList.isNotEmpty()) {
                addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
            }

            // Add VPN flag with numeric value if provided, otherwise use boolean flag
            if (options.vpnFlag != null) {
                addQueryParameter("vpn", options.vpnFlag.value.toString())
            } else if (options.vpnDetection) {
                addQueryParameter("vpn", "1")
            }

            // Add ASN flag with numeric value if provided, otherwise use boolean flag
            if (options.asnFlag != null) {
                addQueryParameter("asn", options.asnFlag.value.toString())
            } else if (options.asn) {
                addQueryParameter("asn", "1")
            }

            // Add NODE flag with numeric value if provided, otherwise use boolean flag
            if (options.nodeFlag != null) {
                addQueryParameter("node", options.nodeFlag.value.toString())
            } else if (options.node) {
                addQueryParameter("node", "1")
            }

            // Add TIME flag with numeric value if provided, otherwise use boolean flag
            if (options.timeFlag != null) {
                addQueryParameter("time", options.timeFlag.value.toString())
            } else if (options.time) {
                addQueryParameter("time", "1")
            }

            // Add INF flag with numeric value if provided, otherwise use boolean flag
            if (options.infFlag != null) {
                addQueryParameter("inf", options.infFlag.value.toString())
            } else if (options.inf) {
                addQueryParameter("inf", "1")
            }

            // Add RISK flag with numeric value if provided, otherwise use boolean flag
            if (options.riskFlag != null) {
                addQueryParameter("risk", options.riskFlag.value.toString())
            } else if (options.risk) {
                addQueryParameter("risk", "1")
            }

            // Add PORT flag with numeric value if provided, otherwise use boolean flag
            if (options.portFlag != null) {
                addQueryParameter("port", options.portFlag.value.toString())
            } else if (options.port) {
                addQueryParameter("port", "1")
            }

            // Add SEEN flag with numeric value if provided, otherwise use boolean flag
            if (options.seenFlag != null) {
                addQueryParameter("seen", options.seenFlag.value.toString())
            } else if (options.seen) {
                addQueryParameter("seen", "1")
            }

            // Add DAYS flag with numeric value if provided, otherwise use boolean flag
            if (options.daysFlag != null) {
                addQueryParameter("days", options.daysFlag.value.toString())
            } else if (options.days) {
                addQueryParameter("days", "1")
            }

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")

            // Add IPs
            addQueryParameter("ips", ips.joinToString(","))
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeRequestForMultipleIps(request, ips, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Checks multiple IP addresses in a single request asynchronously.
     *
     * @param ips The list of IP addresses to check.
     * @param options Optional parameters for the request.
     * @return A map of IP addresses to ProxyCheckResponse objects.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    suspend fun checkIpsAsync(
        ips: List<String>,
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): Map<String, ProxyCheckResponse> {
        if (ips.isEmpty()) {
            return emptyMap()
        }

        val flagsList = options.flags.toMutableList()

        // Add boolean flags if they are true
        if (options.node) flagsList.add(QueryFlag.NODE)
        if (options.time) flagsList.add(QueryFlag.TIME)
        if (options.risk) flagsList.add(QueryFlag.RISK)
        if (options.port) flagsList.add(QueryFlag.PORT)
        if (options.seen) flagsList.add(QueryFlag.SEEN)
        if (options.days) flagsList.add(QueryFlag.DAYS)

        val url = baseUrl.toHttpUrl().newBuilder().apply {
            // Add API key if provided
            if (apiKey != null) {
                addQueryParameter("key", apiKey)
            }

            // Add flags if provided
            if (flagsList.isNotEmpty()) {
                addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
            }

            // Add VPN flag with numeric value if provided, otherwise use boolean flag
            if (options.vpnFlag != null) {
                addQueryParameter("vpn", options.vpnFlag.value.toString())
            } else if (options.vpnDetection) {
                addQueryParameter("vpn", "1")
            }

            // Add ASN flag with numeric value if provided, otherwise use boolean flag
            if (options.asnFlag != null) {
                addQueryParameter("asn", options.asnFlag.value.toString())
            } else if (options.asn) {
                addQueryParameter("asn", "1")
            }

            // Add NODE flag with numeric value if provided, otherwise use boolean flag
            if (options.nodeFlag != null) {
                addQueryParameter("node", options.nodeFlag.value.toString())
            } else if (options.node) {
                addQueryParameter("node", "1")
            }

            // Add TIME flag with numeric value if provided, otherwise use boolean flag
            if (options.timeFlag != null) {
                addQueryParameter("time", options.timeFlag.value.toString())
            } else if (options.time) {
                addQueryParameter("time", "1")
            }

            // Add INF flag with numeric value if provided, otherwise use boolean flag
            if (options.infFlag != null) {
                addQueryParameter("inf", options.infFlag.value.toString())
            } else if (options.inf) {
                addQueryParameter("inf", "1")
            }

            // Add RISK flag with numeric value if provided, otherwise use boolean flag
            if (options.riskFlag != null) {
                addQueryParameter("risk", options.riskFlag.value.toString())
            } else if (options.risk) {
                addQueryParameter("risk", "1")
            }

            // Add PORT flag with numeric value if provided, otherwise use boolean flag
            if (options.portFlag != null) {
                addQueryParameter("port", options.portFlag.value.toString())
            } else if (options.port) {
                addQueryParameter("port", "1")
            }

            // Add SEEN flag with numeric value if provided, otherwise use boolean flag
            if (options.seenFlag != null) {
                addQueryParameter("seen", options.seenFlag.value.toString())
            } else if (options.seen) {
                addQueryParameter("seen", "1")
            }

            // Add DAYS flag with numeric value if provided, otherwise use boolean flag
            if (options.daysFlag != null) {
                addQueryParameter("days", options.daysFlag.value.toString())
            } else if (options.days) {
                addQueryParameter("days", "1")
            }

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")

            // Add IPs
            addQueryParameter("ips", ips.joinToString(","))
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeRequestForMultipleIpsAsync(request, ips, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Gets the dashboard information for the account.
     *
     * @param options Optional parameters for the request.
     * @return A DashboardResponse object containing the dashboard information.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    fun getDashboard(
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): DashboardResponse {
        if (apiKey == null) {
            throw ApiKeyException("API key is required for dashboard requests")
        }

        val url = (baseUrl + "dashboard").toHttpUrl().newBuilder().apply {
            addQueryParameter("key", apiKey)

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeDashboardRequest(request, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Gets the dashboard information for the account asynchronously.
     *
     * @param options Optional parameters for the request.
     * @return A DashboardResponse object containing the dashboard information.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    suspend fun getDashboardAsync(
        options: ProxyCheckOptions = ProxyCheckOptions()
    ): DashboardResponse {
        if (apiKey == null) {
            throw ApiKeyException("API key is required for dashboard requests")
        }

        val url = (baseUrl + "dashboard").toHttpUrl().newBuilder().apply {
            addQueryParameter("key", apiKey)

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeDashboardRequestAsync(request, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Checks if the given email address is from a disposable email provider.
     *
     * @param email The email address to check.
     * @param options Optional parameters for the request.
     * @return An EmailCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    fun checkEmail(
        email: String,
        options: ProxyCheckOptions = ProxyCheckOptions(flags = listOf(QueryFlag.MAIL))
    ): EmailCheckResponse {
        val flagsList = options.flags.toMutableList()

        // Make sure the MAIL flag is included
        if (!flagsList.contains(QueryFlag.MAIL)) {
            flagsList.add(QueryFlag.MAIL)
        }

        // Add boolean flags if they are true
        if (options.node) flagsList.add(QueryFlag.NODE)
        if (options.time) flagsList.add(QueryFlag.TIME)
        if (options.risk) flagsList.add(QueryFlag.RISK)

        val url = (baseUrl + email).toHttpUrl().newBuilder().apply {
            // Add API key if provided
            if (apiKey != null) {
                addQueryParameter("key", apiKey)
            }

            // Add flags if provided
            if (flagsList.isNotEmpty()) {
                addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
            }

            // Add NODE flag with numeric value if provided, otherwise use boolean flag
            if (options.nodeFlag != null) {
                addQueryParameter("node", options.nodeFlag.value.toString())
            } else if (options.node) {
                addQueryParameter("node", "1")
            }

            // Add TIME flag with numeric value if provided, otherwise use boolean flag
            if (options.timeFlag != null) {
                addQueryParameter("time", options.timeFlag.value.toString())
            } else if (options.time) {
                addQueryParameter("time", "1")
            }

            // Add RISK flag with numeric value if provided, otherwise use boolean flag
            if (options.riskFlag != null) {
                addQueryParameter("risk", options.riskFlag.value.toString())
            } else if (options.risk) {
                addQueryParameter("risk", "1")
            }

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeEmailRequest(request, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Checks if the given email address is from a disposable email provider asynchronously.
     *
     * @param email The email address to check.
     * @param options Optional parameters for the request.
     * @return An EmailCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
    suspend fun checkEmailAsync(
        email: String,
        options: ProxyCheckOptions = ProxyCheckOptions(flags = listOf(QueryFlag.MAIL))
    ): EmailCheckResponse {
        val flagsList = options.flags.toMutableList()

        // Make sure the MAIL flag is included
        if (!flagsList.contains(QueryFlag.MAIL)) {
            flagsList.add(QueryFlag.MAIL)
        }

        // Add boolean flags if they are true
        if (options.node) flagsList.add(QueryFlag.NODE)
        if (options.time) flagsList.add(QueryFlag.TIME)
        if (options.risk) flagsList.add(QueryFlag.RISK)

        val url = (baseUrl + email).toHttpUrl().newBuilder().apply {
            // Add API key if provided
            if (apiKey != null) {
                addQueryParameter("key", apiKey)
            }

            // Add flags if provided
            if (flagsList.isNotEmpty()) {
                addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
            }

            // Add NODE flag with numeric value if provided, otherwise use boolean flag
            if (options.nodeFlag != null) {
                addQueryParameter("node", options.nodeFlag.value.toString())
            } else if (options.node) {
                addQueryParameter("node", "1")
            }

            // Add TIME flag with numeric value if provided, otherwise use boolean flag
            if (options.timeFlag != null) {
                addQueryParameter("time", options.timeFlag.value.toString())
            } else if (options.time) {
                addQueryParameter("time", "1")
            }

            // Add RISK flag with numeric value if provided, otherwise use boolean flag
            if (options.riskFlag != null) {
                addQueryParameter("risk", options.riskFlag.value.toString())
            } else if (options.risk) {
                addQueryParameter("risk", "1")
            }

            // Add tag if provided
            if (options.tag != null) {
                addQueryParameter("tag", options.tag)
            }

            // Add VER flag with date value if provided
            if (options.verFlag != null) {
                addQueryParameter("ver", options.verFlag.value)
            }

            // Add SSL parameter
            addQueryParameter("ssl", if (options.useSSL) "1" else "0")
        }.build()

        val request = Request.Builder()
            .url(url)
            .build()

        return executeEmailRequestAsync(request, options.cacheTime, options.cacheTimeUnit)
    }

    /**
     * Executes an API request and returns the response.
     *
     * @param request The Request object to execute.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return A ProxyCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private fun executeRequest(
        request: Request,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): ProxyCheckResponse {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = ipCache.get(cacheKey)
            if (cachedResponse != null) {
                return cachedResponse
            }
        }

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val proxyCheckResponse = gson.fromJson(responseBody, ProxyCheckResponse::class.java)

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                ipCache.put(cacheKey, proxyCheckResponse, actualCacheTime, actualCacheTimeUnit)
            }

            return proxyCheckResponse
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }

    /**
     * Executes an API request asynchronously and returns the response.
     *
     * @param request The Request object to execute.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return A ProxyCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private suspend fun executeRequestAsync(
        request: Request,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): ProxyCheckResponse = withContext(Dispatchers.IO) {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = ipCache.get(cacheKey)
            if (cachedResponse != null) {
                return@withContext cachedResponse
            }
        }

        try {
            val response = client.newCall(request).await()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val proxyCheckResponse = gson.fromJson(responseBody, ProxyCheckResponse::class.java)

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                ipCache.put(cacheKey, proxyCheckResponse, actualCacheTime, actualCacheTimeUnit)
            }

            return@withContext proxyCheckResponse
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }

    /**
     * Executes an API request for multiple IPs and returns the responses.
     *
     * @param request The Request object to execute.
     * @param ips The list of IP addresses that were requested.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return A map of IP addresses to ProxyCheckResponse objects.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private fun executeRequestForMultipleIps(
        request: Request,
        ips: List<String>,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): Map<String, ProxyCheckResponse> {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = ipsCache.get(cacheKey)
            if (cachedResponse != null) {
                return cachedResponse
            }
        }

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val result = mutableMapOf<String, ProxyCheckResponse>()

            // Extract status
            val status = jsonObject.get("status").asString

            // Process each IP
            for (ip in ips) {
                if (jsonObject.has(ip)) {
                    val ipObject = jsonObject.get(ip).asJsonObject

                    // Create a new JSON object with the status and IP data
                    val fullIpObject = JsonObject()
                    fullIpObject.addProperty("status", status)
                    fullIpObject.addProperty("ip", ip)

                    // Copy all properties from the IP object
                    for ((key, value) in ipObject.entrySet()) {
                        fullIpObject.add(key, value)
                    }

                    // Convert to ProxyCheckResponse
                    val proxyCheckResponse = gson.fromJson(fullIpObject, ProxyCheckResponse::class.java)
                    result[ip] = proxyCheckResponse
                }
            }

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                ipsCache.put(cacheKey, result, actualCacheTime, actualCacheTimeUnit)
            }

            return result
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }

    /**
     * Executes an API request for multiple IPs asynchronously and returns the responses.
     *
     * @param request The Request object to execute.
     * @param ips The list of IP addresses that were requested.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return A map of IP addresses to ProxyCheckResponse objects.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private suspend fun executeRequestForMultipleIpsAsync(
        request: Request,
        ips: List<String>,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): Map<String, ProxyCheckResponse> = withContext(Dispatchers.IO) {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = ipsCache.get(cacheKey)
            if (cachedResponse != null) {
                return@withContext cachedResponse
            }
        }

        try {
            val response = client.newCall(request).await()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val result = mutableMapOf<String, ProxyCheckResponse>()

            // Extract status
            val status = jsonObject.get("status").asString

            // Process each IP
            for (ip in ips) {
                if (jsonObject.has(ip)) {
                    val ipObject = jsonObject.get(ip).asJsonObject

                    // Create a new JSON object with the status and IP data
                    val fullIpObject = JsonObject()
                    fullIpObject.addProperty("status", status)
                    fullIpObject.addProperty("ip", ip)

                    // Copy all properties from the IP object
                    for ((key, value) in ipObject.entrySet()) {
                        fullIpObject.add(key, value)
                    }

                    // Convert to ProxyCheckResponse
                    val proxyCheckResponse = gson.fromJson(fullIpObject, ProxyCheckResponse::class.java)
                    result[ip] = proxyCheckResponse
                }
            }

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                ipsCache.put(cacheKey, result, actualCacheTime, actualCacheTimeUnit)
            }

            result
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }

    /**
     * Executes a dashboard API request and returns the response.
     *
     * @param request The Request object to execute.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return A DashboardResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private fun executeDashboardRequest(
        request: Request,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): DashboardResponse {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = dashboardCache.get(cacheKey)
            if (cachedResponse != null) {
                return cachedResponse
            }
        }

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val dashboardResponse = gson.fromJson(responseBody, DashboardResponse::class.java)

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                dashboardCache.put(cacheKey, dashboardResponse, actualCacheTime, actualCacheTimeUnit)
            }

            return dashboardResponse
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }

    /**
     * Executes a dashboard API request asynchronously and returns the response.
     *
     * @param request The Request object to execute.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return A DashboardResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private suspend fun executeDashboardRequestAsync(
        request: Request,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): DashboardResponse = withContext(Dispatchers.IO) {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = dashboardCache.get(cacheKey)
            if (cachedResponse != null) {
                return@withContext cachedResponse
            }
        }

        try {
            val response = client.newCall(request).await()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val dashboardResponse = gson.fromJson(responseBody, DashboardResponse::class.java)

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                dashboardCache.put(cacheKey, dashboardResponse, actualCacheTime, actualCacheTimeUnit)
            }

            return@withContext dashboardResponse
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }

    /**
     * Executes an email check API request and returns the response.
     *
     * @param request The Request object to execute.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return An EmailCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private fun executeEmailRequest(
        request: Request,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): EmailCheckResponse {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = emailCache.get(cacheKey)
            if (cachedResponse != null) {
                return cachedResponse
            }
        }

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val emailCheckResponse = gson.fromJson(responseBody, EmailCheckResponse::class.java)

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                emailCache.put(cacheKey, emailCheckResponse, actualCacheTime, actualCacheTimeUnit)
            }

            return emailCheckResponse
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }

    /**
     * Executes an email check API request asynchronously and returns the response.
     *
     * @param request The Request object to execute.
     * @param cacheTime The time to cache the response, or null to use the default.
     * @param cacheTimeUnit The time unit for cacheTime, or null to use the default.
     * @return An EmailCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private suspend fun executeEmailRequestAsync(
        request: Request,
        cacheTime: Long? = null,
        cacheTimeUnit: TimeUnit? = null
    ): EmailCheckResponse = withContext(Dispatchers.IO) {
        // If caching is enabled, check the cache first
        if (enableCaching) {
            val cacheKey = request.url.toString()
            val cachedResponse = emailCache.get(cacheKey)
            if (cachedResponse != null) {
                return@withContext cachedResponse
            }
        }

        try {
            val response = client.newCall(request).await()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            // Check for API status codes
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                when (status) {
                    "error" -> {
                        when {
                            message.contains("API key") -> throw ApiKeyException(message)
                            message.contains("rate limit") -> throw RateLimitException(message)
                            message.contains("plan limit") || message.contains("query limit") -> {
                                // Extract plan information if available
                                if (jsonObject.has("plan")) {
                                    val plan = jsonObject.get("plan").asString
                                    val queriesToday = if (jsonObject.has("queries_today")) jsonObject.get("queries_today").asInt else null
                                    val queriesMonth = if (jsonObject.has("queries_month")) jsonObject.get("queries_month").asInt else null
                                    val maxQueriesDay = if (jsonObject.has("maxQueries_day")) jsonObject.get("maxQueries_day").asInt else null
                                    val maxQueriesMonth = if (jsonObject.has("maxQueries_month")) jsonObject.get("maxQueries_month").asInt else null
                                    val daysUntilReset = if (jsonObject.has("days_until_reset")) jsonObject.get("days_until_reset").asInt else null

                                    throw PlanLimitException(
                                        message,
                                        null,
                                        plan,
                                        queriesToday,
                                        queriesMonth,
                                        maxQueriesDay,
                                        maxQueriesMonth,
                                        daysUntilReset
                                    )
                                } else {
                                    throw PlanLimitException(message)
                                }
                            }
                            else -> throw ApiErrorException(message)
                        }
                    }
                    "warning" -> throw ApiWarningException(message)
                    "denied" -> throw ApiDeniedException(message)
                }
            }

            val emailCheckResponse = gson.fromJson(responseBody, EmailCheckResponse::class.java)

            // If caching is enabled, cache the response
            if (enableCaching) {
                val cacheKey = request.url.toString()
                val actualCacheTime = cacheTime ?: defaultCacheTime
                val actualCacheTimeUnit = cacheTimeUnit ?: defaultCacheTimeUnit
                emailCache.put(cacheKey, emailCheckResponse, actualCacheTime, actualCacheTimeUnit)
            }

            emailCheckResponse
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }
}
