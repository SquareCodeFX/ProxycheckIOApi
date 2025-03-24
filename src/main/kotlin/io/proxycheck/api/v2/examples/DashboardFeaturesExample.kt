package io.proxycheck.api.v2.examples

import io.proxycheck.api.v2.client.ProxyCheckApiClientAdapter
import io.proxycheck.api.v2.client.ProxyCheckApiInterface
import io.proxycheck.api.v2.models.ProxyCheckOptions
import io.proxycheck.api.v2.models.flag.QueryFlag

/**
 * Example usage of the ProxyCheck.io API client with dashboard features.
 * This example demonstrates how to use the dashboard API to access:
 * - Detection exporting
 * - Tag exporting
 * - Usage exporting
 * - Custom lists
 * - CORS configuration
 */
object DashboardFeaturesExample {
    /**
     * Main method to demonstrate the usage of the ProxyCheck.io API client with dashboard features.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Create a new API client using the adapter (implements ProxyCheckApiInterface)
        // Replace "YOUR_API_KEY" with your actual API key
        val apiClient: ProxyCheckApiInterface = ProxyCheckApiClientAdapter(apiKey = "YOUR_API_KEY")

        // Example 1: Get dashboard with detection export data
        println("Example 1: Get dashboard with detection export data")
        try {
            val options = ProxyCheckOptions.builder()
                .flags(listOf(QueryFlag.EXPORT_DETECTIONS))
                .build()
            val dashboardWithDetections = apiClient.getDashboard(options)

            // Access detection export data
            if (dashboardWithDetections.detections != null) {
                println("Detection export data:")
                for ((ip, detection) in dashboardWithDetections.detections!!) {
                    println("IP: ${detection.ip}")
                    println("Date: ${detection.date}")
                    println("Proxy Type: ${detection.proxyType}")
                    println("Risk: ${detection.risk}")
                    println("Country: ${detection.country}")
                    println("ISO Code: ${detection.isoCode}")
                    println("ASN: ${detection.asn}")
                    println("Provider: ${detection.provider}")
                    println()
                }
            } else {
                println("No detection export data available")
            }
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 2: Get dashboard with tag export data
        println("Example 2: Get dashboard with tag export data")
        try {
            val options = ProxyCheckOptions.builder()
                .flags(listOf(QueryFlag.EXPORT_TAGS))
                .build()
            val dashboardWithTags = apiClient.getDashboard(options)

            // Access tag export data
            if (dashboardWithTags.tags != null) {
                println("Tag export data:")
                for ((tagName, tag) in dashboardWithTags.tags!!) {
                    println("Tag Name: ${tag.name}")
                    println("Count: ${tag.count}")
                    println("First Used: ${tag.firstUsed}")
                    println("Last Used: ${tag.lastUsed}")

                    // Access additional tag info
                    if (tag.info != null) {
                        println("Detections: ${tag.info?.get("detections")}")
                        println("Queries: ${tag.info?.get("queries")}")
                    }
                    println()
                }
            } else {
                println("No tag export data available")
            }
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 3: Get dashboard with usage export data
        println("Example 3: Get dashboard with usage export data")
        try {
            val options = ProxyCheckOptions.builder()
                .flags(listOf(QueryFlag.EXPORT_USAGE))
                .build()
            val dashboardWithUsage = apiClient.getDashboard(options)

            // Access usage export data
            if (dashboardWithUsage.usage != null) {
                println("Usage export data:")
                
                // Access daily usage
                val dailyUsage = dashboardWithUsage.usage?.dailyUsage
                if (dailyUsage != null && dailyUsage.isNotEmpty()) {
                    println("Daily Usage:")
                    for ((date, usage) in dailyUsage) {
                        println("Date: $date")
                        println("Queries: ${usage.queries}")
                        println("Detections: ${usage.detections}")
                        println()
                    }
                }

                // Access monthly usage
                val monthlyUsage = dashboardWithUsage.usage?.monthlyUsage
                if (monthlyUsage != null && monthlyUsage.isNotEmpty()) {
                    println("Monthly Usage:")
                    for ((month, usage) in monthlyUsage) {
                        println("Month: $month")
                        println("Queries: ${usage.queries}")
                        println("Detections: ${usage.detections}")
                        println()
                    }
                }

                // Access total usage
                val totalUsage = dashboardWithUsage.usage?.totalUsage
                if (totalUsage != null) {
                    println("Total Usage:")
                    println("Total Queries: ${totalUsage.queries}")
                    println("Total Detections: ${totalUsage.detections}")
                    println()
                }
            } else {
                println("No usage export data available")
            }
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 4: Get dashboard with custom lists
        println("Example 4: Get dashboard with custom lists")
        try {
            val options = ProxyCheckOptions.builder()
                .flags(listOf(QueryFlag.CUSTOM_LISTS))
                .build()
            val dashboardWithLists = apiClient.getDashboard(options)

            // Access custom lists
            if (dashboardWithLists.customLists != null && dashboardWithLists.customLists!!.isNotEmpty()) {
                println("Custom lists:")
                for (list in dashboardWithLists.customLists!!) {
                    println("List ID: ${list.id}")
                    println("List Name: ${list.name}")
                    println("List Description: ${list.description}")
                    println("List Type: ${list.type}")
                    println("Created At: ${list.createdAt}")
                    println("Updated At: ${list.updatedAt}")

                    // Access list entries
                    if (list.entries != null && list.entries!!.isNotEmpty()) {
                        println("Entries:")
                        for (entry in list.entries!!) {
                            println("  Entry ID: ${entry.id}")
                            println("  Entry Value: ${entry.value}")
                            println("  Entry Note: ${entry.note}")
                            println("  Added At: ${entry.addedAt}")
                            println()
                        }
                    } else {
                        println("No entries in this list")
                    }
                    println()
                }
            } else {
                println("No custom lists available")
            }
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        // Example 5: Get dashboard with CORS configuration
        println("Example 5: Get dashboard with CORS configuration")
        try {
            val options = ProxyCheckOptions.builder()
                .flags(listOf(QueryFlag.CORS))
                .build()
            val dashboardWithCors = apiClient.getDashboard(options)

            // Access CORS configuration
            if (dashboardWithCors.corsStatus != null) {
                println("CORS configuration:")
                println("CORS Enabled: ${dashboardWithCors.corsStatus?.enabled}")

                // Access allowed origins
                val allowedOrigins = dashboardWithCors.corsStatus?.allowedOrigins
                if (allowedOrigins != null && allowedOrigins.isNotEmpty()) {
                    println("Allowed Origins:")
                    for (origin in allowedOrigins) {
                        println("- $origin")
                    }
                }

                // Access allowed methods
                val allowedMethods = dashboardWithCors.corsStatus?.allowedMethods
                if (allowedMethods != null && allowedMethods.isNotEmpty()) {
                    println("Allowed Methods:")
                    for (method in allowedMethods) {
                        println("- $method")
                    }
                }

                // Access allowed headers
                val allowedHeaders = dashboardWithCors.corsStatus?.allowedHeaders
                if (allowedHeaders != null && allowedHeaders.isNotEmpty()) {
                    println("Allowed Headers:")
                    for (header in allowedHeaders) {
                        println("- $header")
                    }
                }

                println("Allow Credentials: ${dashboardWithCors.corsStatus?.allowCredentials}")
                println("Max Age: ${dashboardWithCors.corsStatus?.maxAge}")
            } else {
                println("No CORS configuration available")
            }
            println()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}
