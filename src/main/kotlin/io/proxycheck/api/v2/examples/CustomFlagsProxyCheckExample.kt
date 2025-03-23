package io.proxycheck.api.v2.examples

import io.proxycheck.api.v2.ProxyCheckApiAdapter
import io.proxycheck.api.v2.ProxyCheckApiClientAdapter
import io.proxycheck.api.v2.ProxyCheckApiInterface
import io.proxycheck.api.v2.models.*

/**
 * Example usage of the ProxyCheck.io API client with different query flags and custom settings.
 * This example demonstrates how to use various flag enums to customize API requests.
 */
object CustomFlagsProxyCheckExample {
    /**
     * Main method to demonstrate the usage of the ProxyCheck.io API client with custom flags.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Create a new API client using the adapter (implements ProxyCheckApiInterface)
        // Replace "YOUR_API_KEY" with your actual API key
        val apiClient: ProxyCheckApiInterface = ProxyCheckApiClientAdapter(apiKey = "YOUR_API_KEY")

        // Example 1: Using VPN flag with different settings
        println("Example 1: Using VPN flag with different settings")
        try {
            // Create options with VPN flag set to ENABLED
            val options = ProxyCheckOptions.builder()
                .vpnFlag(VpnFlag.ENABLED)
                .useSSL(true)
                .build()

            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                options = options
            )

            println("Status: ${response.statusEnum} (${response.status.value})")
            println("IP: ${response.ip}")
            println("Proxy: ${response.proxyEnum} (${response.proxyString})")
            println("VPN: ${response.vpn}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 2: Using ASN flag with different settings
        println("Example 2: Using ASN flag with different settings")
        try {
            // Create options with ASN flag set to ENABLED
            val options = ProxyCheckOptions.builder()
                .asnFlag(AsnFlag.ENABLED)
                .useSSL(true)
                .build()

            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                options = options
            )

            println("Status: ${response.statusEnum} (${response.status.value})")
            println("IP: ${response.ip}")
            println("ASN: ${response.asn}")
            println("Provider: ${response.provider}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 3: Using RISK flag with different settings
        println("Example 3: Using RISK flag with different settings")
        try {
            // Create options with RISK flag set to ENHANCED
            val options = ProxyCheckOptions.builder()
                .riskFlag(RiskFlag.ENHANCED)
                .useSSL(true)
                .build()

            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                options = options
            )

            println("Status: ${response.statusEnum} (${response.status.value})")
            println("IP: ${response.ip}")
            println("Risk: ${response.risk}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 4: Using TIME flag with different settings
        println("Example 4: Using TIME flag with different settings")
        try {
            // Create options with TIME flag set to ENABLED
            val options = ProxyCheckOptions.builder()
                .timeFlag(TimeFlag.ENABLED)
                .useSSL(true)
                .build()

            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                options = options
            )

            println("Status: ${response.statusEnum} (${response.status.value})")
            println("IP: ${response.ip}")
            println("Time: ${response.time}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 5: Combining multiple flags with different settings
        println("Example 5: Combining multiple flags with different settings")
        try {
            // Create options with multiple flags
            val options = ProxyCheckOptions.builder()
                .vpnFlag(VpnFlag.ENABLED)
                .asnFlag(AsnFlag.ENABLED)
                .riskFlag(RiskFlag.ENHANCED)
                .timeFlag(TimeFlag.ENABLED)
                .nodeFlag(NodeFlag.ENABLED)
                .useSSL(true)
                .build()

            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                options = options
            )

            println("Status: ${response.statusEnum} (${response.status.value})")
            println("IP: ${response.ip}")
            println("Proxy: ${response.proxyEnum} (${response.proxyString})")
            println("VPN: ${response.vpn}")
            println("ASN: ${response.asn}")
            println("Provider: ${response.provider}")
            println("Risk: ${response.risk}")
            println("Time: ${response.time}")
            println("Node: ${response.node}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 6: Using additional query flags
        println("Example 6: Using additional query flags")
        try {
            // Create options with additional query flags
            val options = ProxyCheckOptions.builder()
                .flags(listOf(
                    QueryFlag.VPN,
                    QueryFlag.ASN,
                    QueryFlag.COUNTRY,
                    QueryFlag.ISOCODE,
                    QueryFlag.PROXY_TYPE,
                    QueryFlag.PROVIDER,
                    QueryFlag.CITY,
                    QueryFlag.REGION,
                    QueryFlag.ORGANIZATION,
                    QueryFlag.HOSTNAME,
                    QueryFlag.ISP
                ))
                .useSSL(true)
                .build()

            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                options = options
            )

            println("Status: ${response.statusEnum} (${response.status.value})")
            println("IP: ${response.ip}")
            println("Proxy: ${response.proxyEnum} (${response.proxyString})")
            println("VPN: ${response.vpn}")
            println("ASN: ${response.asn}")
            println("Country: ${response.country}")
            println("ISO Code: ${response.isoCode}")
            println("Provider: ${response.provider}")
            println("City: ${response.city}")
            println("Region: ${response.region}")
            println("Organization: ${response.organization}")
            println("Hostname: ${response.hostname}")
            println("ISP: ${response.isp}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}
