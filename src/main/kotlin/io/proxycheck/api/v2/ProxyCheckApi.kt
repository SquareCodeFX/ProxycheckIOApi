package io.proxycheck.api.v2

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonParser.parseString
import io.proxycheck.api.v2.exceptions.*
import io.proxycheck.api.v2.models.*
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

        // Add VPN flag with numeric value if provided, otherwise use boolean flag
        if (options.vpnFlag != null) {
            urlBuilder.addQueryParameter("vpn", options.vpnFlag.value.toString())
        } else if (options.vpnDetection) {
            urlBuilder.addQueryParameter("vpn", "1")
        }

        // Add ASN flag with numeric value if provided, otherwise use boolean flag
        if (options.asnFlag != null) {
            urlBuilder.addQueryParameter("asn", options.asnFlag.value.toString())
        } else if (options.asn) {
            urlBuilder.addQueryParameter("asn", "1")
        }

        // Add NODE flag with numeric value if provided, otherwise use boolean flag
        if (options.nodeFlag != null) {
            urlBuilder.addQueryParameter("node", options.nodeFlag.value.toString())
        } else if (options.node) {
            urlBuilder.addQueryParameter("node", "1")
        }

        // Add TIME flag with numeric value if provided, otherwise use boolean flag
        if (options.timeFlag != null) {
            urlBuilder.addQueryParameter("time", options.timeFlag.value.toString())
        } else if (options.time) {
            urlBuilder.addQueryParameter("time", "1")
        }

        // Add INF flag with numeric value if provided, otherwise use boolean flag
        if (options.infFlag != null) {
            urlBuilder.addQueryParameter("inf", options.infFlag.value.toString())
        } else if (options.inf) {
            urlBuilder.addQueryParameter("inf", "1")
        }

        // Add RISK flag with numeric value if provided, otherwise use boolean flag
        if (options.riskFlag != null) {
            urlBuilder.addQueryParameter("risk", options.riskFlag.value.toString())
        } else if (options.risk) {
            urlBuilder.addQueryParameter("risk", "1")
        }

        // Add PORT flag with numeric value if provided, otherwise use boolean flag
        if (options.portFlag != null) {
            urlBuilder.addQueryParameter("port", options.portFlag.value.toString())
        } else if (options.port) {
            urlBuilder.addQueryParameter("port", "1")
        }

        // Add SEEN flag with numeric value if provided, otherwise use boolean flag
        if (options.seenFlag != null) {
            urlBuilder.addQueryParameter("seen", options.seenFlag.value.toString())
        } else if (options.seen) {
            urlBuilder.addQueryParameter("seen", "1")
        }

        // Add DAYS flag with numeric value if provided, otherwise use boolean flag
        if (options.daysFlag != null) {
            urlBuilder.addQueryParameter("days", options.daysFlag.value.toString())
        } else if (options.days) {
            urlBuilder.addQueryParameter("days", "1")
        }

        // Add tag if provided
        if (options.tag != null) {
            urlBuilder.addQueryParameter("tag", options.tag)
        }

        // Add VER flag with date value if provided
        if (options.verFlag != null) {
            urlBuilder.addQueryParameter("ver", options.verFlag.value)
        }

        // Add SSL parameter
        urlBuilder.addQueryParameter("ssl", if (options.useSSL) "1" else "0")

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        return executeRequest(request)
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

        val urlBuilder = baseUrl.toHttpUrl().newBuilder()

        // Add API key if provided
        if (apiKey != null) {
            urlBuilder.addQueryParameter("key", apiKey)
        }

        // Add flags if provided
        if (flagsList.isNotEmpty()) {
            urlBuilder.addQueryParameter("flags", QueryFlag.toQueryString(flagsList))
        }

        // Add VPN flag with numeric value if provided, otherwise use boolean flag
        if (options.vpnFlag != null) {
            urlBuilder.addQueryParameter("vpn", options.vpnFlag.value.toString())
        } else if (options.vpnDetection) {
            urlBuilder.addQueryParameter("vpn", "1")
        }

        // Add ASN flag with numeric value if provided, otherwise use boolean flag
        if (options.asnFlag != null) {
            urlBuilder.addQueryParameter("asn", options.asnFlag.value.toString())
        } else if (options.asn) {
            urlBuilder.addQueryParameter("asn", "1")
        }

        // Add NODE flag with numeric value if provided, otherwise use boolean flag
        if (options.nodeFlag != null) {
            urlBuilder.addQueryParameter("node", options.nodeFlag.value.toString())
        } else if (options.node) {
            urlBuilder.addQueryParameter("node", "1")
        }

        // Add TIME flag with numeric value if provided, otherwise use boolean flag
        if (options.timeFlag != null) {
            urlBuilder.addQueryParameter("time", options.timeFlag.value.toString())
        } else if (options.time) {
            urlBuilder.addQueryParameter("time", "1")
        }

        // Add INF flag with numeric value if provided, otherwise use boolean flag
        if (options.infFlag != null) {
            urlBuilder.addQueryParameter("inf", options.infFlag.value.toString())
        } else if (options.inf) {
            urlBuilder.addQueryParameter("inf", "1")
        }

        // Add RISK flag with numeric value if provided, otherwise use boolean flag
        if (options.riskFlag != null) {
            urlBuilder.addQueryParameter("risk", options.riskFlag.value.toString())
        } else if (options.risk) {
            urlBuilder.addQueryParameter("risk", "1")
        }

        // Add PORT flag with numeric value if provided, otherwise use boolean flag
        if (options.portFlag != null) {
            urlBuilder.addQueryParameter("port", options.portFlag.value.toString())
        } else if (options.port) {
            urlBuilder.addQueryParameter("port", "1")
        }

        // Add SEEN flag with numeric value if provided, otherwise use boolean flag
        if (options.seenFlag != null) {
            urlBuilder.addQueryParameter("seen", options.seenFlag.value.toString())
        } else if (options.seen) {
            urlBuilder.addQueryParameter("seen", "1")
        }

        // Add DAYS flag with numeric value if provided, otherwise use boolean flag
        if (options.daysFlag != null) {
            urlBuilder.addQueryParameter("days", options.daysFlag.value.toString())
        } else if (options.days) {
            urlBuilder.addQueryParameter("days", "1")
        }

        // Add tag if provided
        if (options.tag != null) {
            urlBuilder.addQueryParameter("tag", options.tag)
        }

        // Add VER flag with date value if provided
        if (options.verFlag != null) {
            urlBuilder.addQueryParameter("ver", options.verFlag.value)
        }

        // Add SSL parameter
        urlBuilder.addQueryParameter("ssl", if (options.useSSL) "1" else "0")

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

        val urlBuilder = (baseUrl + "dashboard").toHttpUrl().newBuilder()
        urlBuilder.addQueryParameter("key", apiKey)

        // Add tag if provided
        if (options.tag != null) {
            urlBuilder.addQueryParameter("tag", options.tag)
        }

        // Add VER flag with date value if provided
        if (options.verFlag != null) {
            urlBuilder.addQueryParameter("ver", options.verFlag.value)
        }

        // Add SSL parameter
        urlBuilder.addQueryParameter("ssl", if (options.useSSL) "1" else "0")

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
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                // Use the ExceptionHandler to handle API errors
                ExceptionHandler.handleApiError(status, message, jsonObject)
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
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                // Use the ExceptionHandler to handle API errors
                ExceptionHandler.handleApiError(status, message, jsonObject)
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
            if (jsonObject.has("status")) {
                val status = jsonObject.get("status").asString
                val message = if (jsonObject.has("message")) jsonObject.get("message").asString else "Unknown status: $status"

                // Use the ExceptionHandler to handle API errors
                ExceptionHandler.handleApiError(status, message, jsonObject)
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
