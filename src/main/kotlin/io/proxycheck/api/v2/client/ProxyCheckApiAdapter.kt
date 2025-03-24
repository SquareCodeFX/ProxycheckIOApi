package io.proxycheck.api.v2.client

import com.google.gson.Gson
import io.proxycheck.api.v2.models.ProxyCheckOptions
import io.proxycheck.api.v2.models.response.DashboardResponse
import io.proxycheck.api.v2.models.response.EmailCheckResponse
import io.proxycheck.api.v2.models.response.ProxyCheckResponse
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Adapter class that implements ProxyCheckApiInterface and delegates to ProxyCheckApiClient.
 * This allows for interface-based programming while maintaining backward compatibility.
 */
class ProxyCheckApiAdapter(
    apiKey: String? = null,
    client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build(),
    baseUrl: String = "https://proxycheck.io/v2/",
    gson: Gson = Gson()
) : ProxyCheckApiInterface {

    private val apiClient = ProxyCheckApiClient(apiKey, client, baseUrl, gson)

    /**
     * Checks a single IP address for proxy information.
     * Delegates to ProxyCheckApiClient.checkIp.
     */
    override fun checkIp(
        ip: String,
        options: ProxyCheckOptions
    ): ProxyCheckResponse {
        return apiClient.checkIp(ip, options)
    }

    /**
     * Checks multiple IP addresses for proxy information.
     * Delegates to ProxyCheckApiClient.checkIps.
     */
    override fun checkIps(
        ips: List<String>,
        options: ProxyCheckOptions
    ): Map<String, ProxyCheckResponse> {
        return apiClient.checkIps(ips, options)
    }

    /**
     * Gets the dashboard information for the account.
     * Delegates to ProxyCheckApiClient.getDashboard.
     */
    override fun getDashboard(
        options: ProxyCheckOptions
    ): DashboardResponse {
        return apiClient.getDashboard(options)
    }

    /**
     * Checks if the given email address is from a disposable email provider.
     * Delegates to ProxyCheckApiClient.checkEmail.
     */
    override fun checkEmail(
        email: String,
        options: ProxyCheckOptions
    ): EmailCheckResponse {
        return apiClient.checkEmail(email, options)
    }
}
