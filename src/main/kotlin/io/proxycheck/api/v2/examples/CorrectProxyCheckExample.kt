package io.proxycheck.api.v2.examples

import io.proxycheck.api.v2.client.ProxyCheckApiAdapter
import io.proxycheck.api.v2.client.ProxyCheckApiClientAdapter
import io.proxycheck.api.v2.client.ProxyCheckApiInterface
import io.proxycheck.api.v2.models.ProxyCheckOptions
import io.proxycheck.api.v2.models.enum.ProxyStatus
import io.proxycheck.api.v2.models.enum.ProxyType
import io.proxycheck.api.v2.models.enum.ResponseStatus
import io.proxycheck.api.v2.models.flag.QueryFlag

/**
 * Example usage of the ProxyCheck.io API client with the correct approach.
 * This example demonstrates how to use the ProxyCheckOptions class to avoid issues
 * with setting both flags and individual boolean parameters.
 */
object CorrectProxyCheckExample {
    /**
     * Main method to demonstrate the usage of the ProxyCheck.io API client.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Create a new API client using the adapter (implements ProxyCheckApiInterface)
        // Replace "YOUR_API_KEY" with your actual API key
        val apiClient: ProxyCheckApiInterface = ProxyCheckApiClientAdapter(apiKey = "YOUR_API_KEY")

        // You can also use the ProxyCheckApiAdapter which delegates to ProxyCheckApi
        // val apiClient: ProxyCheckApiInterface = ProxyCheckApiAdapter(apiKey = "YOUR_API_KEY")

        // Example 1: Check a single IP address
        println("Example 1: Check a single IP address")
        try {
            // Create options with the parameters we need
            val options = ProxyCheckOptions(
                vpnDetection = true,
                asn = true,
                time = true,
                useSSL = true
            )

            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                options = options
            )

            // Using the enum properties
            println("Status: ${response.statusEnum} (${response.status.value})")
            println("IP: ${response.ip}")
            println("Proxy: ${response.proxyEnum} (${response.proxyString})")
            println("Type: ${response.typeEnum} (${response.typeString})")
            println("Risk: ${response.risk}")
            println("Country: ${response.country}")
            println("ISP: ${response.isp}")
            println("ASN: ${response.asn}")
            println("Time: ${response.time}")

            // Example of using the enum values for conditional logic
            when (response.statusEnum) {
                ResponseStatus.SUCCESS -> println("Request was successful")
                ResponseStatus.ERROR -> println("Request encountered an error")
                ResponseStatus.DENIED -> println("Request was denied")
            }

            when (response.proxyEnum) {
                ProxyStatus.YES -> println("This IP is a proxy")
                ProxyStatus.NO -> println("This IP is not a proxy")
                ProxyStatus.UNKNOWN -> println("Proxy status is unknown")
            }

            when (response.typeEnum) {
                ProxyType.VPN -> println("This is a VPN")
                ProxyType.TOR -> println("This is a TOR node")
                ProxyType.PUBLIC -> println("This is a public proxy")
                ProxyType.RESIDENTIAL -> println("This is a residential proxy")
                ProxyType.WEB -> println("This is a web proxy")
                ProxyType.HOSTING -> println("This is a hosting provider")
                ProxyType.UNKNOWN -> println("Proxy type is unknown")
            }

            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 2: Check multiple IP addresses
        println("Example 2: Check multiple IP addresses")
        try {
            // Create options with only the flags we need
            // This avoids setting both flags and individual boolean parameters
            val options = ProxyCheckOptions(
                flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME),
                useSSL = true
            )

            val responses = apiClient.checkIps(
                ips = listOf("8.8.8.8", "1.1.1.1"),
                options = options
            )

            for ((ip, response) in responses) {
                println("IP: $ip")
                println("Status: ${response.statusEnum} (${response.status.value})")
                println("Proxy: ${response.proxyEnum} (${response.proxyString})")
                println("Type: ${response.typeEnum} (${response.typeString})")
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

            println("Status: ${dashboard.statusEnum} (${dashboard.status.value})")
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
            // Create options with the parameters we need
            val options = ProxyCheckOptions(
                flags = listOf(QueryFlag.MAIL),
                risk = true,
                node = true,
                time = true
            )

            val emailResponse = apiClient.checkEmail(
                email = "test@example.com",
                options = options
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
