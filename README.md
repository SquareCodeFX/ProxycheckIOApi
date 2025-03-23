# ProxyCheck.io API Client for Java/Kotlin

A Java/Kotlin client library for the [ProxyCheck.io](https://proxycheck.io/) API v2. This library provides a simple and easy-to-use interface for checking if an IP address is a proxy, VPN, or TOR exit node, as well as validating email addresses.

[![JitPack](https://jitpack.io/v/SquareCodeFX/ProxycheckIOApi.svg)](https://jitpack.io/#SquareCodeFX/ProxycheckIOApi)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/java-8%2B-blue.svg)](https://www.oracle.com/java/)
[![API](https://img.shields.io/badge/API-ProxyCheck.io%20v2-orange.svg)](https://proxycheck.io/api/)
[![GitHub issues](https://img.shields.io/github/issues/SquareCodeFX/ProxycheckIOApi)](https://github.com/SquareCodeFX/ProxycheckIOApi/issues)
[![GitHub stars](https://img.shields.io/github/stars/SquareCodeFX/ProxycheckIOApi)](https://github.com/SquareCodeFX/ProxycheckIOApi/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/SquareCodeFX/ProxycheckIOApi)](https://github.com/SquareCodeFX/ProxycheckIOApi/network)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/SquareCodeFX/ProxycheckIOApi/graphs/commit-activity)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://makeapullrequest.com)

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Repository](#repository)
- [Dependencies](#dependencies)
- [Usage](#usage)
  - [Creating a Client](#creating-a-client)
  - [Caching](#caching)
  - [Using ProxyCheckOptions](#using-proxycheckoptions)
  - [Checking a Single IP Address](#checking-a-single-ip-address)
  - [Checking Multiple IP Addresses](#checking-multiple-ip-addresses)
  - [Checking Email Addresses](#checking-email-addresses)
  - [Getting Dashboard Information](#getting-dashboard-information)
  - [Asynchronous API Usage](#asynchronous-api-usage)
  - [Error Handling](#error-handling)
- [API Reference](#api-reference)
  - [ProxyCheckApiInterface](#proxycheckapiinterface)
  - [ProxyCheckOptions](#proxycheckoptions)
  - [Response Models](#response-models)
- [API Endpoints](#api-endpoints)
- [Query Flags](#query-flags)
  - [Available Flag Enums](#available-flag-enums)
  - [Using Custom Flag Settings](#using-custom-flag-settings)
- [Exceptions](#exceptions)
- [Rate Limits](#rate-limits)
- [Version Compatibility](#version-compatibility)
- [Tests](#tests)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)
- [Issues and Support](#issues-and-support)

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
    implementation("com.github.SquareCodeFX:ProxycheckIOApi:1.0.0")
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.SquareCodeFX</groupId>
    <artifactId>ProxycheckIOApi</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Repository

This library is available through JitPack. To use it, add the JitPack repository to your build file:

### Gradle

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

## Dependencies

This library depends on the following libraries:

| Dependency | Version | Description |
|------------|---------|-------------|
| Kotlin Standard Library | 1.9.0 | Kotlin standard library |
| OkHttp | 4.11.0 | HTTP client for making API requests |
| Gson | 2.10.1 | JSON parsing library |
| Kotlin Coroutines | 1.7.3 | For asynchronous programming |

For testing, the following dependencies are used:

| Dependency | Version | Description |
|------------|---------|-------------|
| JUnit | 5.10.0 | Testing framework |
| Mockito | 5.4.0 | Mocking framework |
| Mockito Kotlin | 5.0.0 | Kotlin extensions for Mockito |

## Usage

### Creating a Client

There are two ways to create a client:

#### Option 1: Using the Direct Client Implementation

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

#### Option 2: Using the Interface and Adapters (Recommended)

This approach provides better flexibility and testability through dependency injection.

```kotlin
// Create a client using the adapter (implements ProxyCheckApiInterface)
val apiClient: ProxyCheckApiInterface = ProxyCheckApiClientAdapter(apiKey = "your-api-key")

// You can also use the ProxyCheckApiAdapter which delegates to ProxyCheckApi
val apiClient: ProxyCheckApiInterface = ProxyCheckApiAdapter(apiKey = "your-api-key")

// Create a client with custom settings
val apiClient: ProxyCheckApiInterface = ProxyCheckApiClientAdapter(
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
val apiClient: ProxyCheckApiInterface = ProxyCheckApiClientAdapter(
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

### Using ProxyCheckOptions

The library now uses a `ProxyCheckOptions` class to configure API requests. This approach avoids issues with setting both flags and individual boolean parameters.

```kotlin
// Create options with the parameters we need
val options = ProxyCheckOptions(
    vpnDetection = true,
    asn = true,
    time = true,
    useSSL = true
)

// Or use the builder pattern
val options = ProxyCheckOptions.builder()
    .vpnDetection(true)
    .asn(true)
    .time(true)
    .useSSL(true)
    .build()

// You can also set specific flag enums for more control
val options = ProxyCheckOptions.builder()
    .vpnFlag(VpnFlag.ENABLED)
    .asnFlag(AsnFlag.ENABLED)
    .timeFlag(TimeFlag.ENABLED)
    .riskFlag(RiskFlag.ENHANCED)
    .useSSL(true)
    .build()
```

### Checking a Single IP Address

```kotlin
// Basic check
val response = client.checkIp("8.8.8.8")

// Check with options
val options = ProxyCheckOptions(
    vpnDetection = true,
    asn = true,
    time = true
)
val response = client.checkIp(
    ip = "8.8.8.8",
    options = options
)

// Check with multiple flags
val options = ProxyCheckOptions(
    flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME)
)
val response = client.checkIp(
    ip = "8.8.8.8",
    options = options
)

// Check with all options
val options = ProxyCheckOptions(
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
val response = client.checkIp(
    ip = "8.8.8.8",
    options = options
)

// Access the response data
println("Status: ${response.statusEnum} (${response.status.value})")
println("IP: ${response.ip}")
println("Proxy: ${response.proxyEnum} (${response.proxyString})")
println("Type: ${response.typeEnum} (${response.typeString})")
println("Risk: ${response.risk}")
println("Country: ${response.country}")
println("ISP: ${response.isp}")
println("ASN: ${response.asn}")
println("Time: ${response.time}")

// Using enum values for conditional logic
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
```

### Checking Multiple IP Addresses

```kotlin
// Basic check
val responses = client.checkIps(listOf("8.8.8.8", "1.1.1.1"))

// Check with options
val options = ProxyCheckOptions(
    vpnDetection = true,
    asn = true,
    time = true
)
val responses = client.checkIps(
    ips = listOf("8.8.8.8", "1.1.1.1"),
    options = options
)

// Check with multiple flags
val options = ProxyCheckOptions(
    flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME)
)
val responses = client.checkIps(
    ips = listOf("8.8.8.8", "1.1.1.1"),
    options = options
)

// Access the response data
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
}
```

### Checking Email Addresses

The library also provides functionality to check if an email address is from a disposable email provider.

```kotlin
// Basic email check
val emailResponse = client.checkEmail("test@example.com")

// Email check with additional options
val options = ProxyCheckOptions(
    flags = listOf(QueryFlag.MAIL),
    risk = true,
    node = true,
    time = true,
    tag = "my-tag",
    useSSL = true
)
val emailResponse = client.checkEmail(
    email = "test@example.com",
    options = options
)

// Access the email response data
println("Status: ${emailResponse.statusEnum} (${emailResponse.status.value})")
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

## API Reference

### ProxyCheckApiInterface

The `ProxyCheckApiInterface` defines the contract for the API client. It provides the following methods:

#### `checkIp(ip: String, options: ProxyCheckOptions): ProxyCheckResponse`

Checks a single IP address for proxy information.

**Parameters:**
- `ip`: The IP address to check.
- `options`: Optional parameters for the request (see [ProxyCheckOptions](#proxycheckoptions) for details).

**Returns:**
- A `ProxyCheckResponse` object containing the proxy information.

**Example:**
```kotlin
val response = client.checkIp("8.8.8.8")
```

#### `checkIps(ips: List<String>, options: ProxyCheckOptions): Map<String, ProxyCheckResponse>`

Checks multiple IP addresses for proxy information.

**Parameters:**
- `ips`: The list of IP addresses to check.
- `options`: Optional parameters for the request (see [ProxyCheckOptions](#proxycheckoptions) for details).

**Returns:**
- A map of IP addresses to `ProxyCheckResponse` objects.

**Example:**
```kotlin
val responses = client.checkIps(listOf("8.8.8.8", "1.1.1.1"))
```

#### `getDashboard(options: ProxyCheckOptions): DashboardResponse`

Gets the dashboard information for the account.

**Parameters:**
- `options`: Optional parameters for the request (see [ProxyCheckOptions](#proxycheckoptions) for details).

**Returns:**
- A `DashboardResponse` object containing the dashboard information.

**Example:**
```kotlin
val dashboard = client.getDashboard()
```

#### `checkEmail(email: String, options: ProxyCheckOptions): EmailCheckResponse`

Checks if the given email address is from a disposable email provider.

**Parameters:**
- `email`: The email address to check.
- `options`: Optional parameters for the request (see [ProxyCheckOptions](#proxycheckoptions) for details).

**Returns:**
- An `EmailCheckResponse` object containing the response from the API.

**Example:**
```kotlin
val emailResponse = client.checkEmail("test@example.com")
```

### ProxyCheckOptions

The `ProxyCheckOptions` class is used to configure API requests. It provides a flexible way to set various options for the API requests.

#### Constructor Parameters

| Parameter | Type | Description | Default |
|-----------|------|-------------|---------|
| `flags` | `List<QueryFlag>` | List of query flags to include in the request | `emptyList()` |
| `vpnDetection` | `Boolean` | Enable VPN detection | `false` |
| `vpnFlag` | `VpnFlag?` | Specific VPN flag setting (ENABLED, DISABLED) | `null` |
| `asn` | `Boolean` | Include ASN information | `false` |
| `asnFlag` | `AsnFlag?` | Specific ASN flag setting (ENABLED, DISABLED) | `null` |
| `node` | `Boolean` | Include node information | `false` |
| `nodeFlag` | `NodeFlag?` | Specific node flag setting (ENABLED, DISABLED) | `null` |
| `time` | `Boolean` | Include time information | `false` |
| `timeFlag` | `TimeFlag?` | Specific time flag setting (ENABLED, DISABLED) | `null` |
| `inf` | `Boolean` | Include INF information | `false` |
| `infFlag` | `InfFlag?` | Specific INF flag setting (ENABLED, DISABLED) | `null` |
| `risk` | `Boolean` | Include risk information | `false` |
| `riskFlag` | `RiskFlag?` | Specific risk flag setting (DISABLED, ENABLED, ENHANCED) | `null` |
| `port` | `Boolean` | Include port information | `false` |
| `portFlag` | `PortFlag?` | Specific port flag setting (ENABLED, DISABLED) | `null` |
| `seen` | `Boolean` | Include seen information | `false` |
| `seenFlag` | `SeenFlag?` | Specific seen flag setting (ENABLED, DISABLED) | `null` |
| `days` | `Boolean` | Include days information | `false` |
| `daysFlag` | `DaysFlag?` | Specific days flag setting (ENABLED, DISABLED) | `null` |
| `tag` | `String?` | Custom tag for the request | `null` |
| `verFlag` | `VerFlag?` | Specific version flag setting | `null` |
| `useSSL` | `Boolean` | Use SSL for the request | `true` |
| `cacheTime` | `Long?` | Custom cache time for the request | `null` |
| `cacheTimeUnit` | `TimeUnit?` | Custom cache time unit for the request | `null` |

#### Builder Methods

The `ProxyCheckOptions` class provides a builder pattern for easier configuration:

```kotlin
val options = ProxyCheckOptions.builder()
    .vpnDetection(true)
    .asn(true)
    .time(true)
    .useSSL(true)
    .build()
```

Each builder method corresponds to a parameter in the constructor.

### Response Models

The library provides several response models for different API endpoints:

#### ProxyCheckResponse

The `ProxyCheckResponse` class represents the response from the `checkIp` and `checkIps` methods.

**Properties:**
- `status`: The status of the request.
- `statusEnum`: The status as an enum value (`ResponseStatus.SUCCESS`, `ResponseStatus.ERROR`, `ResponseStatus.DENIED`).
- `ip`: The IP address that was checked.
- `proxy`: Whether the IP is a proxy (as a string).
- `proxyEnum`: Whether the IP is a proxy (as an enum value: `ProxyStatus.YES`, `ProxyStatus.NO`, `ProxyStatus.UNKNOWN`).
- `type`: The type of proxy (as a string).
- `typeEnum`: The type of proxy (as an enum value: `ProxyType.VPN`, `ProxyType.TOR`, etc.).
- `risk`: The risk score of the IP.
- `country`: The country of the IP.
- `isp`: The ISP of the IP.
- `asn`: The ASN of the IP.
- `time`: The time it took to process the request.
- And many more properties depending on the query flags used.

#### DashboardResponse

The `DashboardResponse` class represents the response from the `getDashboard` method.

**Properties:**
- `status`: The status of the request.
- `plan`: The plan of the account.
- `email`: The email of the account.
- `queriesToday`: The number of queries used today.
- `queriesMonth`: The number of queries used this month.
- `maxQueriesDay`: The maximum number of queries allowed per day.
- `maxQueriesMonth`: The maximum number of queries allowed per month.
- `daysUntilReset`: The number of days until the query count resets.

#### EmailCheckResponse

The `EmailCheckResponse` class represents the response from the `checkEmail` method.

**Properties:**
- `status`: The status of the request.
- `email`: The email address that was checked.
- `disposable`: Whether the email is from a disposable provider.
- `risk`: The risk score of the email.
- `node`: The node that processed the request.
- `time`: The time it took to process the request.

## API Endpoints

This library provides access to the following ProxyCheck.io API endpoints:

| Endpoint | Description | Method |
|----------|-------------|--------|
| `https://proxycheck.io/v2/{ip}` | Check if an IP address is a proxy | `checkIp()`, `checkIpAsync()` |
| `https://proxycheck.io/v2/` | Check multiple IP addresses | `checkIps()`, `checkIpsAsync()` |
| `https://proxycheck.io/v2/dashboard` | Get account information | `getDashboard()`, `getDashboardAsync()` |
| `https://proxycheck.io/v2/{email}` | Check if an email is from a disposable provider | `checkEmail()`, `checkEmailAsync()` |

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

### Available Flag Enums

The library provides several flag enums for more granular control over API requests:

| Flag Enum | Description | Values |
|-----------|-------------|--------|
| `VpnFlag` | Controls VPN detection | `ENABLED` (1), `DISABLED` (0) |
| `AsnFlag` | Controls ASN information | `ENABLED` (1), `DISABLED` (0) |
| `NodeFlag` | Controls node information | `ENABLED` (1), `DISABLED` (0) |
| `TimeFlag` | Controls time information | `ENABLED` (1), `DISABLED` (0) |
| `InfFlag` | Controls INF information | `ENABLED` (1), `DISABLED` (0) |
| `RiskFlag` | Controls risk information | `DISABLED` (0), `ENABLED` (1), `ENHANCED` (2) |
| `PortFlag` | Controls port information | `ENABLED` (1), `DISABLED` (0) |
| `SeenFlag` | Controls seen information | `ENABLED` (1), `DISABLED` (0) |
| `DaysFlag` | Controls days information | `ENABLED` (1), `DISABLED` (0) |
| `VerFlag` | Controls version information | `V1` (1), `V2` (2) |

### Using Custom Flag Settings

In addition to the basic query flags, you can use specific flag enums to customize your API requests with more granular control. Here are examples of using different flag enums:

#### VPN Flag

The `VpnFlag` enum allows you to enable or disable VPN detection:

```kotlin
// Enable VPN detection
val options = ProxyCheckOptions.builder()
    .vpnFlag(VpnFlag.ENABLED)  // Value: 1
    .build()

// Disable VPN detection
val options = ProxyCheckOptions.builder()
    .vpnFlag(VpnFlag.DISABLED)  // Value: 0
    .build()
```

#### Risk Flag

The `RiskFlag` enum provides different levels of risk information:

```kotlin
// Basic risk information
val options = ProxyCheckOptions.builder()
    .riskFlag(RiskFlag.ENABLED)  // Value: 1
    .build()

// Enhanced risk information
val options = ProxyCheckOptions.builder()
    .riskFlag(RiskFlag.ENHANCED)  // Value: 2
    .build()

// Disable risk information
val options = ProxyCheckOptions.builder()
    .riskFlag(RiskFlag.DISABLED)  // Value: 0
    .build()
```

#### Combining Multiple Flags

You can combine multiple flag enums for more comprehensive results:

```kotlin
// Combine multiple flag enums
val options = ProxyCheckOptions.builder()
    .vpnFlag(VpnFlag.ENABLED)
    .asnFlag(AsnFlag.ENABLED)
    .riskFlag(RiskFlag.ENHANCED)
    .timeFlag(TimeFlag.ENABLED)
    .nodeFlag(NodeFlag.ENABLED)
    .build()

val response = apiClient.checkIp(
    ip = "8.8.8.8",
    options = options
)
```

#### Using Additional Query Flags

You can also use the `flags` property to include multiple query flags:

```kotlin
// Include multiple query flags
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
    .build()

val response = apiClient.checkIp(
    ip = "8.8.8.8",
    options = options
)
```

For a complete example of using different query flags with custom settings, see the [CustomFlagsProxyCheckExample.kt](src/main/kotlin/io/proxycheck/api/v2/examples/CustomFlagsProxyCheckExample.kt) file.

## Exceptions

This library provides a comprehensive set of exceptions to handle various error scenarios:

| Exception | Description |
|-----------|-------------|
| `ProxyCheckException` | Base exception class for all ProxyCheck.io API exceptions |
| `ApiKeyException` | Thrown when the API key is invalid or missing |
| `RateLimitException` | Thrown when the API rate limit is exceeded |
| `InvalidRequestException` | Thrown when the API request is invalid |
| `ApiErrorException` | Thrown when the API returns an error |
| `ApiWarningException` | Thrown when the API returns a warning |
| `ApiDeniedException` | Thrown when the API denies the request |
| `PlanLimitException` | Thrown when the plan limits are exceeded, with additional properties for plan information |
| `NetworkException` | Thrown when there is a network error |

Example of handling exceptions:

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
} catch (e: PlanLimitException) {
    // Handle plan limit errors
    println("Plan limit exceeded: ${e.message}")
    println("Plan: ${e.plan}")
    println("Queries Today: ${e.queriesToday}")
    println("Max Queries Day: ${e.maxQueriesDay}")
} catch (e: ProxyCheckException) {
    // Handle all other ProxyCheck.io API errors
    println("ProxyCheck.io API error: ${e.message}")
}
```

## Rate Limits

ProxyCheck.io imposes rate limits on API requests based on your plan. This library handles rate limit errors by throwing a `RateLimitException` when the rate limit is exceeded.

- Free plan: 100 queries per day
- Paid plans: Various limits based on the plan

When you exceed your plan's query limit, a `PlanLimitException` is thrown with details about your current usage:

- `plan`: Your current plan
- `queriesToday`: Number of queries used today
- `queriesMonth`: Number of queries used this month
- `maxQueriesDay`: Maximum number of queries allowed per day
- `maxQueriesMonth`: Maximum number of queries allowed per month
- `daysUntilReset`: Number of days until the query count resets

To avoid rate limit errors, you can:

1. Use the built-in caching functionality to reduce the number of API requests
2. Implement your own caching mechanism
3. Upgrade to a higher plan with higher rate limits

## Version Compatibility

This library is compatible with:

- Java 8 or higher
- Kotlin 1.7 or higher
- Android API level 21 (Android 5.0 Lollipop) or higher

The library targets ProxyCheck.io API v2 and is built with:

- Kotlin 1.9.0
- OkHttp 4.11.0
- Gson 2.10.1
- Kotlin Coroutines 1.7.3

## Tests

The project includes unit tests for the `ProxyCheckApiClient` class, which cover:

- Successful IP checks
- Successful multiple IP checks
- Successful email checks
- Successful dashboard information retrieval
- Error handling for various exceptions (API key errors, rate limit errors)
- Testing different flag options and parameters

Additional tests that could be added:

1. Integration tests with the actual ProxyCheck.io API
   - Test with real IP addresses
   - Test with real email addresses
   - Test rate limiting

2. More comprehensive mock tests
   - Test additional response formats
   - Test more error scenarios

## Contributing

Contributions are welcome! Please see the [CONTRIBUTING.md](CONTRIBUTING.md) file for guidelines on how to contribute to this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Changelog

All notable changes to this project are documented in the [CHANGELOG.md](CHANGELOG.md) file.

## Issues and Support

If you encounter any issues or have questions about using this library, please:

1. Check the [existing issues](https://github.com/SquareCodeFX/ProxycheckIOApi/issues) to see if your problem has already been reported
2. Open a [new issue](https://github.com/SquareCodeFX/ProxycheckIOApi/issues/new) if your problem hasn't been reported yet
3. Provide as much information as possible, including:
   - Steps to reproduce the issue
   - Expected behavior
   - Actual behavior
   - Code samples
   - Error messages
   - Your environment (Java/Kotlin version, OS, etc.)

For feature requests, please open an issue with the "enhancement" label.
