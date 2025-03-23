package io.proxycheck.api.v2

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonParser.parseString
import io.proxycheck.api.v2.exceptions.*
import io.proxycheck.api.v2.models.DashboardResponse
import io.proxycheck.api.v2.models.ProxyCheckResponse
import io.proxycheck.api.v2.models.QueryFlag
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Main client class for interacting with the ProxyCheck.io API v2.
 *
 * @property apiKey The API key for authentication with ProxyCheck.io.
 * @property client The OkHttpClient instance for making HTTP requests.
 * @property baseUrl The base URL for the ProxyCheck.io API.
 * @property gson The Gson instance for JSON serialization/deserialization.
 */
class ProxyCheckApi(
    private val apiKey: String? = null,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build(),
    private val baseUrl: String = "https://proxycheck.io/v2/",
    private val gson: Gson = Gson()
) {
    /**
     * Checks if the given IP address is a proxy.
     *
     * @param ip The IP address to check.
     * @param flags The query flags to include in the request.
     * @param vpnDetection Whether to enable VPN detection.
     * @param asn Whether to include ASN data in the response.
     * @param node Whether to include the node that processed the request in the response.
     * @param time Whether to include the time it took to process the request in the response.
     * @param risk Whether to include the risk score in the response.
     * @param port Whether to include the port used by the proxy in the response.
     * @param seen Whether to include the seen date of the proxy in the response.
     * @param days Whether to include the days since the proxy was first detected in the response.
     * @param tag A custom tag to identify the request.
     * @param useSSL Whether to use SSL for the request.
     * @return A ProxyCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
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
    ): ProxyCheckResponse {
        val flagsList = flags.toMutableList()

        // Add boolean flags if they are true
        if (vpnDetection) flagsList.add(QueryFlag.VPN)
        if (asn) flagsList.add(QueryFlag.ASN)
        if (node) flagsList.add(QueryFlag.NODE)
        if (time) flagsList.add(QueryFlag.TIME)
        if (risk) flagsList.add(QueryFlag.RISK)
        if (port) flagsList.add(QueryFlag.PORT)
        if (seen) flagsList.add(QueryFlag.SEEN)
        if (days) flagsList.add(QueryFlag.DAYS)

        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("proxycheck.io")
            .addPathSegment("v2")
            .addPathSegment(ip)
            .build()
            .newBuilder()

        // Add API key if provided
        if (apiKey != null) {
            urlBuilder.addQueryParameter("key", apiKey)
        }

        // Add flags if provided
        if (flagsList.isNotEmpty()) {
            urlBuilder.addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
        }

        // Add tag if provided
        if (tag != null) {
            urlBuilder.addQueryParameter("tag", tag)
        }

        // Add SSL parameter
        urlBuilder.addQueryParameter("ssl", if (useSSL) "1" else "0")

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        return executeRequest(request)
    }

    /**
     * Checks multiple IP addresses in a single request.
     *
     * @param ips The list of IP addresses to check.
     * @param flags The query flags to include in the request.
     * @param vpnDetection Whether to enable VPN detection.
     * @param asn Whether to include ASN data in the response.
     * @param node Whether to include the node that processed the request in the response.
     * @param time Whether to include the time it took to process the request in the response.
     * @param risk Whether to include the risk score in the response.
     * @param port Whether to include the port used by the proxy in the response.
     * @param seen Whether to include the seen date of the proxy in the response.
     * @param days Whether to include the days since the proxy was first detected in the response.
     * @param tag A custom tag to identify the request.
     * @param useSSL Whether to use SSL for the request.
     * @return A map of IP addresses to ProxyCheckResponse objects.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    @JvmOverloads
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
    ): Map<String, ProxyCheckResponse> {
        if (ips.isEmpty()) {
            return emptyMap()
        }

        val flagsList = flags.toMutableList()

        // Add boolean flags if they are true
        if (vpnDetection) flagsList.add(QueryFlag.VPN)
        if (asn) flagsList.add(QueryFlag.ASN)
        if (node) flagsList.add(QueryFlag.NODE)
        if (time) flagsList.add(QueryFlag.TIME)
        if (risk) flagsList.add(QueryFlag.RISK)
        if (port) flagsList.add(QueryFlag.PORT)
        if (seen) flagsList.add(QueryFlag.SEEN)
        if (days) flagsList.add(QueryFlag.DAYS)

        val urlBuilder = baseUrl.toHttpUrl().newBuilder()

        // Add API key if provided
        if (apiKey != null) {
            urlBuilder.addQueryParameter("key", apiKey)
        }

        // Add flags if provided
        if (flagsList.isNotEmpty()) {
            urlBuilder.addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
        }

        // Add tag if provided
        if (tag != null) {
            urlBuilder.addQueryParameter("tag", tag)
        }

        // Add SSL parameter
        urlBuilder.addQueryParameter("ssl", if (useSSL) "1" else "0")

        // Add IPs
        urlBuilder.addQueryParameter("ips", ips.joinToString(","))

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        return executeRequestForMultipleIps(request, ips)
    }

    /**
     * Gets the dashboard information for the account.
     *
     * @return A DashboardResponse object containing the dashboard information.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    fun getDashboard(): DashboardResponse {
        if (apiKey == null) {
            throw ApiKeyException("API key is required for dashboard requests")
        }

        val urlBuilder = (baseUrl + "dashboard").toHttpUrl().newBuilder()
        urlBuilder.addQueryParameter("key", apiKey)

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        return executeDashboardRequest(request)
    }

    /**
     * Executes an API request and returns the response.
     *
     * @param request The Request object to execute.
     * @return A ProxyCheckResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private fun executeRequest(request: Request): ProxyCheckResponse {
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = JsonParser.parseString(responseBody).asJsonObject

            // Check for API errors
            if (jsonObject.has("status") && jsonObject.get("status").asString == "error") {
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown error"

                when {
                    message.contains("API key") -> throw ApiKeyException(message)
                    message.contains("rate limit") -> throw RateLimitException(message)
                    else -> throw ApiErrorException(message)
                }
            }

            return gson.fromJson(responseBody, ProxyCheckResponse::class.java)
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
     * @return A map of IP addresses to ProxyCheckResponse objects.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private fun executeRequestForMultipleIps(request: Request, ips: List<String>): Map<String, ProxyCheckResponse> {
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = JsonParser.parseString(responseBody).asJsonObject

            // Check for API errors
            if (jsonObject.has("status") && jsonObject.get("status").asString == "error") {
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown error"

                when {
                    message.contains("API key") -> throw ApiKeyException(message)
                    message.contains("rate limit") -> throw RateLimitException(message)
                    else -> throw ApiErrorException(message)
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
     * Executes a dashboard API request and returns the response.
     *
     * @param request The Request object to execute.
     * @return A DashboardResponse object containing the response from the API.
     * @throws ProxyCheckException If there is an error with the API request.
     */
    private fun executeDashboardRequest(request: Request): DashboardResponse {
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw ApiErrorException("Empty response body")

            val jsonObject = JsonParser.parseString(responseBody).asJsonObject

            // Check for API errors
            if (jsonObject.has("status") && jsonObject.get("status").asString == "error") {
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown error"

                when {
                    message.contains("API key") -> throw ApiKeyException(message)
                    message.contains("rate limit") -> throw RateLimitException(message)
                    else -> throw ApiErrorException(message)
                }
            }

            return gson.fromJson(responseBody, DashboardResponse::class.java)
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: ProxyCheckException) {
            throw e
        } catch (e: Exception) {
            throw ProxyCheckException("Error executing request: ${e.message}", e)
        }
    }
}
