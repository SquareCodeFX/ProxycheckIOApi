package io.proxycheck.api.v2.examples

import io.proxycheck.api.v2.ProxyCheckApiClient
import io.proxycheck.api.v2.models.QueryFlag
import kotlinx.coroutines.runBlocking

/**
 * Example usage of the ProxyCheck.io API client.
 */
object ProxyCheckExample {
    /**
     * Main method to demonstrate the usage of the ProxyCheck.io API client.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Create a new API client
        // Replace "YOUR_API_KEY" with your actual API key
        val apiClient = ProxyCheckApiClient(apiKey = "YOUR_API_KEY")

        // Synchronous Examples
        println("=== Synchronous Examples ===")

        // Example 1: Check a single IP address
        println("Example 1: Check a single IP address")
        try {
            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                vpnDetection = true,
                asn = true,
                time = true
            )

            println("Status: ${response.status}")
            println("IP: ${response.ip}")
            println("Proxy: ${response.proxyEnum}")
            println("Type: ${response.typeEnum}")
            println("Risk: ${response.risk}")
            println("Country: ${response.country}")
            println("ISP: ${response.isp}")
            println("ASN: ${response.asn}")
            println("Time: ${response.time}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 2: Check multiple IP addresses
        println("Example 2: Check multiple IP addresses")
        try {
            val responses = apiClient.checkIps(
                ips = listOf("8.8.8.8", "1.1.1.1"),
                flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME)
            )

            for ((ip, response) in responses) {
                println("IP: $ip")
                println("Status: ${response.status}")
                println("Proxy: ${response.proxyEnum}")
                println("Type: ${response.typeEnum}")
                println("Risk: ${response.risk}")
                println("Country: ${response.country}")
                println("ISP: ${response.isp}")
                println("ASN: ${response.asn}")
                println("Time: ${response.time}")
                println()
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 3: Get dashboard information
        println("Example 3: Get dashboard information")
        try {
            val dashboard = apiClient.getDashboard()

            println("Status: ${dashboard.status}")
            println("Plan: ${dashboard.plan}")
            println("Email: ${dashboard.email}")
            println("Queries Today: ${dashboard.queriesToday}")
            println("Queries Month: ${dashboard.queriesMonth}")
            println("Max Queries Day: ${dashboard.maxQueriesDay}")
            println("Max Queries Month: ${dashboard.maxQueriesMonth}")
            println("Days Until Reset: ${dashboard.daysUntilReset}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 4: Check an email address
        println("Example 4: Check an email address")
        try {
            val emailResponse = apiClient.checkEmail(
                email = "test@example.com",
                risk = true,
                node = true,
                time = true
            )

            println("Status: ${emailResponse.status}")
            println("Email: ${emailResponse.email}")
            println("Disposable: ${emailResponse.disposable}")
            println("Risk: ${emailResponse.risk}")
            println("Node: ${emailResponse.node}")
            println("Time: ${emailResponse.time}")
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Asynchronous Examples
        println("=== Asynchronous Examples ===")

        // We need to use runBlocking to call suspend functions from a non-suspend context
        runBlocking {
            // Example 5: Check a single IP address asynchronously
            println("Example 5: Check a single IP address asynchronously")
            try {
                val response = apiClient.checkIpAsync(
                    ip = "8.8.8.8",
                    vpnDetection = true,
                    asn = true,
                    time = true
                )

                println("Status: ${response.status}")
                println("IP: ${response.ip}")
                println("Proxy: ${response.proxyEnum}")
                println("Type: ${response.typeEnum}")
                println("Risk: ${response.risk}")
                println("Country: ${response.country}")
                println("ISP: ${response.isp}")
                println("ASN: ${response.asn}")
                println("Time: ${response.time}")
                println()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }

            // Example 6: Check multiple IP addresses asynchronously
            println("Example 6: Check multiple IP addresses asynchronously")
            try {
                val responses = apiClient.checkIpsAsync(
                    ips = listOf("8.8.8.8", "1.1.1.1"),
                    flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME)
                )

                for ((ip, response) in responses) {
                    println("IP: $ip")
                    println("Status: ${response.status}")
                    println("Proxy: ${response.proxyEnum}")
                    println("Type: ${response.typeEnum}")
                    println("Risk: ${response.risk}")
                    println("Country: ${response.country}")
                    println("ISP: ${response.isp}")
                    println("ASN: ${response.asn}")
                    println("Time: ${response.time}")
                    println()
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }

            // Example 7: Get dashboard information asynchronously
            println("Example 7: Get dashboard information asynchronously")
            try {
                val dashboard = apiClient.getDashboardAsync()

                println("Status: ${dashboard.status}")
                println("Plan: ${dashboard.plan}")
                println("Email: ${dashboard.email}")
                println("Queries Today: ${dashboard.queriesToday}")
                println("Queries Month: ${dashboard.queriesMonth}")
                println("Max Queries Day: ${dashboard.maxQueriesDay}")
                println("Max Queries Month: ${dashboard.maxQueriesMonth}")
                println("Days Until Reset: ${dashboard.daysUntilReset}")
                println()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }

            // Example 8: Check an email address asynchronously
            println("Example 8: Check an email address asynchronously")
            try {
                val emailResponse = apiClient.checkEmailAsync(
                    email = "test@example.com",
                    risk = true,
                    node = true,
                    time = true
                )

                println("Status: ${emailResponse.status}")
                println("Email: ${emailResponse.email}")
                println("Disposable: ${emailResponse.disposable}")
                println("Risk: ${emailResponse.risk}")
                println("Node: ${emailResponse.node}")
                println("Time: ${emailResponse.time}")
                println()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}
