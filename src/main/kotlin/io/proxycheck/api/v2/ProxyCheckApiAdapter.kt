package io.proxycheck.api.v2

import com.google.gson.Gson
import io.proxycheck.api.v2.models.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Adapter class that implements ProxyCheckApiInterface and delegates to ProxyCheckApi.
 * This allows for interface-based programming while maintaining backward compatibility.
 */
class ProxyCheckApiAdapter(
    private val apiKey: String? = null,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build(),
    private val baseUrl: String = "https://proxycheck.io/v2/",
    private val gson: Gson = Gson()
) : ProxyCheckApiInterface {

    private val api = ProxyCheckApi(apiKey, client, baseUrl, gson)

    /**
     * Checks a single IP address for proxy information.
     * Delegates to ProxyCheckApi.checkIp.
     */
    override fun checkIp(
        ip: String,
        options: ProxyCheckOptions
    ): ProxyCheckResponse {
        return api.checkIp(ip, options)
    }

    /**
     * Checks multiple IP addresses for proxy information.
     * Delegates to ProxyCheckApi.checkIps.
     */
    override fun checkIps(
        ips: List<String>,
        options: ProxyCheckOptions
    ): Map<String, ProxyCheckResponse> {
        return api.checkIps(ips, options)
    }

    /**
     * Gets the dashboard information for the account.
     * Delegates to ProxyCheckApi.getDashboard.
     */
    override fun getDashboard(
        options: ProxyCheckOptions
    ): DashboardResponse {
        return api.getDashboard(options)
    }

    /**
     * Checks if the given email address is from a disposable email provider.
     * This is implemented directly in the adapter since ProxyCheckApi doesn't support email checking.
     */
    override fun checkEmail(
        email: String,
        options: ProxyCheckOptions
    ): EmailCheckResponse {
        // Create a new ProxyCheckApiClient to handle the email check
        val apiClient = ProxyCheckApiClient(
            apiKey = apiKey,
            client = client,
            baseUrl = baseUrl,
            gson = gson
        )

        return apiClient.checkEmail(email, options)
    }
}
