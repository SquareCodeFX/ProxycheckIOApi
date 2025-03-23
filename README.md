# ProxyCheck.io API Client for Java/Kotlin

A Java/Kotlin client library for the [ProxyCheck.io](https://proxycheck.io/) API v2. This library provides a simple and easy-to-use interface for checking if an IP address is a proxy, VPN, or TOR exit node, as well as validating email addresses.

## Features

- Check if an IP address is a proxy, VPN, or TOR exit node
- Check multiple IP addresses in a single request
- Validate email addresses for disposable email providers
- Get dashboard information about your ProxyCheck.io account
- Support for all query flags and parameters
- Proper error handling with custom exceptions
- Rate limit handling
- Fully documented API
- Support for both synchronous and asynchronous API calls using Kotlin Coroutines
- Built-in caching with adjustable cache time

## Installation

### Gradle

```kotlin
dependencies {
    implementation("org.example:proxycheck-api:1.0-SNAPSHOT")
}
```

### Maven

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>proxycheck-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

### Creating a Client

```kotlin
// Create a client with your API key
val client = ProxyCheckApiClient(apiKey = "your-api-key")

// Create a client without an API key (limited to 100 queries per day)
val client = ProxyCheckApiClient()

// Create a client with custom settings
val client = ProxyCheckApiClient(
    apiKey = "your-api-key",
    client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build(),
    baseUrl = "https://proxycheck.io/v2/",
    gson = Gson()
)

// Create a client with caching enabled
val client = ProxyCheckApiClient(
    apiKey = "your-api-key",
    enableCaching = true,
    defaultCacheTime = 5,
    defaultCacheTimeUnit = TimeUnit.MINUTES
)
```

### Caching

The library supports caching of API responses to reduce the number of requests made to the ProxyCheck.io API. Caching is disabled by default, but can be enabled when creating the client.

```kotlin
// Create a client with caching enabled
val client = ProxyCheckApiClient(
    apiKey = "your-api-key",
    enableCaching = true,
    defaultCacheTime = 5,
    defaultCacheTimeUnit = TimeUnit.MINUTES
)
```

You can also specify a custom cache time for individual requests:

```kotlin
// Check an IP address with a custom cache time
val response = client.checkIp(
    ip = "8.8.8.8",
    cacheTime = 10,
    cacheTimeUnit = TimeUnit.MINUTES
)

// Check multiple IP addresses with a custom cache time
val responses = client.checkIps(
    ips = listOf("8.8.8.8", "1.1.1.1"),
    cacheTime = 10,
    cacheTimeUnit = TimeUnit.MINUTES
)

// Get dashboard information with a custom cache time
val dashboard = client.getDashboard(
    cacheTime = 10,
    cacheTimeUnit = TimeUnit.MINUTES
)

// Check an email address with a custom cache time
val emailResponse = client.checkEmail(
    email = "test@example.com",
    cacheTime = 10,
    cacheTimeUnit = TimeUnit.MINUTES
)
```

The same caching functionality is available for asynchronous methods as well.

### Checking a Single IP Address

```kotlin
// Basic check
val response = client.checkIp("8.8.8.8")

// Check with VPN detection
val response = client.checkIp(
    ip = "8.8.8.8",
    vpnDetection = true
)

// Check with multiple flags
val response = client.checkIp(
    ip = "8.8.8.8",
    flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME)
)

// Check with all options
val response = client.checkIp(
    ip = "8.8.8.8",
    flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME),
    vpnDetection = true,
    asn = true,
    node = true,
    time = true,
    risk = true,
    port = true,
    seen = true,
    days = true,
    tag = "my-tag",
    useSSL = true
)

// Access the response data
println("Status: ${response.status}")
println("IP: ${response.ip}")
println("Proxy: ${response.proxyEnum}")
println("Type: ${response.typeEnum}")
println("Risk: ${response.risk}")
println("Country: ${response.country}")
println("ISP: ${response.isp}")
println("ASN: ${response.asn}")
println("Time: ${response.time}")
```

### Checking Multiple IP Addresses

```kotlin
// Basic check
val responses = client.checkIps(listOf("8.8.8.8", "1.1.1.1"))

// Check with VPN detection
val responses = client.checkIps(
    ips = listOf("8.8.8.8", "1.1.1.1"),
    vpnDetection = true
)

// Check with multiple flags
val responses = client.checkIps(
    ips = listOf("8.8.8.8", "1.1.1.1"),
    flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME)
)

// Access the response data
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
}
```

### Checking Email Addresses

The library also provides functionality to check if an email address is from a disposable email provider.

```kotlin
// Basic email check
val emailResponse = client.checkEmail("test@example.com")

// Email check with additional options
val emailResponse = client.checkEmail(
    email = "test@example.com",
    flags = listOf(QueryFlag.MAIL),
    risk = true,
    node = true,
    time = true,
    tag = "my-tag",
    useSSL = true
)

// Access the email response data
println("Status: ${emailResponse.status}")
println("Email: ${emailResponse.email}")
println("Disposable: ${emailResponse.disposable}")
println("Risk: ${emailResponse.risk}")
println("Node: ${emailResponse.node}")
println("Time: ${emailResponse.time}")
```

### Getting Dashboard Information

```kotlin
// Get dashboard information
val dashboard = client.getDashboard()

// Access the dashboard data
println("Status: ${dashboard.status}")
println("Plan: ${dashboard.plan}")
println("Email: ${dashboard.email}")
println("Queries Today: ${dashboard.queriesToday}")
println("Queries Month: ${dashboard.queriesMonth}")
println("Max Queries Day: ${dashboard.maxQueriesDay}")
println("Max Queries Month: ${dashboard.maxQueriesMonth}")
println("Days Until Reset: ${dashboard.daysUntilReset}")
```

### Asynchronous API Usage

The library also provides asynchronous versions of all API methods using Kotlin Coroutines. These methods are marked with the `suspend` keyword and can be called from a coroutine scope.

```kotlin
// Import the required coroutines dependencies
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// Create a client
val client = ProxyCheckApiClient(apiKey = "your-api-key")

// Example 1: Check a single IP address asynchronously
runBlocking {
    try {
        val response = client.checkIpAsync(
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
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

// Example 2: Check multiple IP addresses asynchronously
runBlocking {
    try {
        val responses = client.checkIpsAsync(
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
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

// Example 3: Get dashboard information asynchronously
runBlocking {
    try {
        val dashboard = client.getDashboardAsync()

        println("Status: ${dashboard.status}")
        println("Plan: ${dashboard.plan}")
        println("Email: ${dashboard.email}")
        println("Queries Today: ${dashboard.queriesToday}")
        println("Queries Month: ${dashboard.queriesMonth}")
        println("Max Queries Day: ${dashboard.maxQueriesDay}")
        println("Max Queries Month: ${dashboard.maxQueriesMonth}")
        println("Days Until Reset: ${dashboard.daysUntilReset}")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

// Example 4: Check an email address asynchronously
runBlocking {
    try {
        val emailResponse = client.checkEmailAsync(
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
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
```

### Error Handling

```kotlin
try {
    val response = client.checkIp("8.8.8.8")
    // Process response
} catch (e: ApiKeyException) {
    // Handle API key errors
    println("API key error: ${e.message}")
} catch (e: RateLimitException) {
    // Handle rate limit errors
    println("Rate limit exceeded: ${e.message}")
} catch (e: InvalidRequestException) {
    // Handle invalid request errors
    println("Invalid request: ${e.message}")
} catch (e: ApiErrorException) {
    // Handle API errors
    println("API error: ${e.message}")
} catch (e: NetworkException) {
    // Handle network errors
    println("Network error: ${e.message}")
} catch (e: ProxyCheckException) {
    // Handle all other ProxyCheck.io API errors
    println("ProxyCheck.io API error: ${e.message}")
} catch (e: Exception) {
    // Handle all other errors
    println("Error: ${e.message}")
}
```

## Query Flags

The following query flags are supported:

- `VPN`: Returns VPN status of the IP address
- `ASN`: Returns ASN details of the IP address
- `NODE`: Returns the node that processed the request
- `TIME`: Returns the time it took to process the request
- `RISK`: Returns the risk score of the IP address
- `PORT`: Returns the port used by the proxy
- `SEEN`: Returns the seen date of the proxy
- `DAYS`: Returns the days since the proxy was first detected
- `COUNTRY`: Returns the country of the IP address
- `ISOCODE`: Returns the isocode of the IP address
- `PROXY_TYPE`: Returns the proxy type of the IP address
- `PROVIDER`: Returns the provider of the IP address
- `TOR`: Returns the TOR status of the IP address
- `RESIDENTIAL`: Returns the residential status of the IP address
- `MOBILE`: Returns the mobile status of the IP address
- `HOSTING`: Returns the hosting status of the IP address
- `CITY`: Returns the city of the IP address
- `REGION`: Returns the region/state of the IP address
- `ORGANIZATION`: Returns the organization of the IP address
- `HOSTNAME`: Returns the hostname of the IP address
- `ISP`: Returns the ISP of the IP address
- `MAIL`: Enables email checking for disposable email providers

## Dependencies

This library depends on the following libraries:

- Kotlin Standard Library
- OkHttp (version 4.11.0) for HTTP client functionality
- Gson (version 2.10.1) for JSON parsing
- Kotlin Coroutines (version 1.7.3) for asynchronous programming

## License

This project is licensed under the MIT License - see the LICENSE file for details.
