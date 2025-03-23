package io.proxycheck.api.v2

import com.google.gson.Gson
import io.proxycheck.api.v2.models.DashboardResponse
import io.proxycheck.api.v2.models.EmailCheckResponse
import io.proxycheck.api.v2.models.ProxyCheckResponse
import io.proxycheck.api.v2.models.QueryFlag
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
        flags: List<QueryFlag>,
        vpnDetection: Boolean,
        asn: Boolean,
        node: Boolean,
        time: Boolean,
        risk: Boolean,
        port: Boolean,
        seen: Boolean,
        days: Boolean,
        tag: String?,
        useSSL: Boolean
    ): ProxyCheckResponse {
        return api.checkIp(
            ip, flags, vpnDetection, asn, node, time, risk, port, seen, days, tag, useSSL
        )
    }

    /**
     * Checks multiple IP addresses for proxy information.
     * Delegates to ProxyCheckApi.checkIps.
     */
    override fun checkIps(
        ips: List<String>,
        flags: List<QueryFlag>,
        vpnDetection: Boolean,
        asn: Boolean,
        node: Boolean,
        time: Boolean,
        risk: Boolean,
        port: Boolean,
        seen: Boolean,
        days: Boolean,
        tag: String?,
        useSSL: Boolean
    ): Map<String, ProxyCheckResponse> {
        return api.checkIps(
            ips, flags, vpnDetection, asn, node, time, risk, port, seen, days, tag, useSSL
        )
    }

    /**
     * Gets the dashboard information for the account.
     * Delegates to ProxyCheckApi.getDashboard.
     */
    override fun getDashboard(): DashboardResponse {
        return api.getDashboard()
    }

    /**
     * Checks if the given email address is from a disposable email provider.
     * This is implemented directly in the adapter since ProxyCheckApi doesn't support email checking.
     */
    override fun checkEmail(
        email: String,
        flags: List<QueryFlag>,
        node: Boolean,
        time: Boolean,
        risk: Boolean,
        tag: String?,
        useSSL: Boolean
    ): EmailCheckResponse {
        // Create a new ProxyCheckApiClient to handle the email check
        val apiClient = ProxyCheckApiClient(
            apiKey = apiKey,
            client = client,
            baseUrl = baseUrl,
            gson = gson
        )

        return apiClient.checkEmail(
            email, flags, node, time, risk, tag, useSSL
        )
    }
}
