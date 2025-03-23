package io.proxycheck.api.v2.examples

import io.proxycheck.api.v2.ProxyCheckApiAdapter
import io.proxycheck.api.v2.ProxyCheckApiClientAdapter
import io.proxycheck.api.v2.ProxyCheckApiInterface
import io.proxycheck.api.v2.models.ProxyStatus
import io.proxycheck.api.v2.models.ProxyType
import io.proxycheck.api.v2.models.QueryFlag
import io.proxycheck.api.v2.models.ResponseStatus

/**
 * Example usage of the enhanced ProxyCheck.io API client with enums and interfaces.
 */
object EnhancedProxyCheckExample {
    /**
     * Main method to demonstrate the usage of the enhanced ProxyCheck.io API client.
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
            val response = apiClient.checkIp(
                ip = "8.8.8.8",
                flags = emptyList(),
                vpnDetection = true,
                asn = true,
                node = false,
                time = true,
                risk = false,
                port = false,
                seen = false,
                days = false,
                tag = null,
                useSSL = true
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
            val responses = apiClient.checkIps(
                ips = listOf("8.8.8.8", "1.1.1.1"),
                flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME),
                vpnDetection = false,
                asn = false,
                node = false,
                time = false,
                risk = false,
                port = false,
                seen = false,
                days = false,
                tag = null,
                useSSL = true
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
    }
}
