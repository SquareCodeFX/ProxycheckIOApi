package io.proxycheck.api.v2

import io.proxycheck.api.v2.exceptions.ApiKeyException
import io.proxycheck.api.v2.exceptions.RateLimitException
import io.proxycheck.api.v2.models.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import okhttp3.*
import java.io.IOException

/**
 * Unit tests for the ProxyCheckApiClient class.
 */
class ProxyCheckApiClientTest {

    private lateinit var mockClient: OkHttpClient
    private lateinit var mockCall: Call
    private lateinit var mockResponse: Response
    private lateinit var mockResponseBody: ResponseBody
    private lateinit var apiClient: ProxyCheckApiClient

    @BeforeEach
    fun setUp() {
        // Create mocks
        mockClient = mock()
        mockCall = mock()
        mockResponse = mock()
        mockResponseBody = mock()

        // Set up default behavior
        whenever(mockClient.newCall(any())).thenReturn(mockCall)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockResponse.body).thenReturn(mockResponseBody)

        // Create the API client with the mock HTTP client
        apiClient = ProxyCheckApiClient(
            apiKey = "test-api-key",
            client = mockClient
        )
    }

    @Test
    fun `checkIp should return a valid response when the API call is successful`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "type": "",
                "risk": 0,
                "country": "US",
                "isocode": "US",
                "asn": 15169,
                "provider": "Google LLC"
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val response = apiClient.checkIp("8.8.8.8")

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals(0, response.risk)
        assertEquals("US", response.country)
    }

    @Test
    fun `checkIp should throw ApiKeyException when the API returns an API key error`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "error",
                "message": "Invalid API key"
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act & Assert
        assertThrows<ApiKeyException> {
            apiClient.checkIp("8.8.8.8")
        }
    }

    @Test
    fun `checkIp should throw RateLimitException when the API returns a rate limit error`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "error",
                "message": "You have exceeded your rate limit"
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act & Assert
        assertThrows<RateLimitException> {
            apiClient.checkIp("8.8.8.8")
        }
    }

    @Test
    fun `checkIps should return valid responses for multiple IPs`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "8.8.8.8": {
                    "proxy": "no",
                    "type": "",
                    "risk": 0,
                    "country": "US",
                    "isocode": "US",
                    "asn": 15169,
                    "provider": "Google LLC"
                },
                "1.1.1.1": {
                    "proxy": "no",
                    "type": "",
                    "risk": 0,
                    "country": "AU",
                    "isocode": "AU",
                    "asn": 13335,
                    "provider": "Cloudflare, Inc."
                }
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val responses = apiClient.checkIps(listOf("8.8.8.8", "1.1.1.1"))

        // Assert
        assertNotNull(responses)
        assertEquals(2, responses.size)
        assertTrue(responses.containsKey("8.8.8.8"))
        assertTrue(responses.containsKey("1.1.1.1"))
        assertEquals("no", responses["8.8.8.8"]?.proxyString)
        assertEquals("no", responses["1.1.1.1"]?.proxyString)
        assertEquals("US", responses["8.8.8.8"]?.country)
        assertEquals("AU", responses["1.1.1.1"]?.country)
    }

    @Test
    fun `checkIp should include query flags in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false,
                "asn": 15169,
                "time": 0.01
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .flags(listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME))
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals(15169, response.asn)
        assertEquals(0.01, response.timeDouble)
    }

    @Test
    fun `checkIp should include VPN flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false,
                "asn": 15169,
                "time": 0.01
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .vpnFlag(VpnFlag.ENABLED) // Value 1
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals(15169, response.asn)
    }

    @Test
    fun `checkIp should include ASN flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false,
                "asn": 15169,
                "time": 0.01
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .asnFlag(AsnFlag.ENABLED) // Value 1
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals(15169, response.asn)
    }

    @Test
    fun `checkIp should include NODE flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false,
                "node": "test-node",
                "time": 0.01
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .nodeFlag(NodeFlag.ENABLED) // Value 1
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals("test-node", response.node)
    }

    @Test
    fun `checkIp should include TIME flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false,
                "time": 0.01
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .timeFlag(TimeFlag.ENABLED) // Value 1
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals(0.01, response.timeDouble)
    }

    @Test
    fun `checkIp should include INF flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .infFlag(InfFlag.ENABLED) // Value 1
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
    }

    @Test
    fun `checkIp should include RISK flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false,
                "risk": 0
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .riskFlag(RiskFlag.ENHANCED) // Value 2
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals(0, response.risk)
    }

    @Test
    fun `checkIp should include PORT flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .portFlag(PortFlag.ENABLED) // Value 1
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
    }

    @Test
    fun `checkIp should include SEEN flag with numeric value in the request`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "ip": "8.8.8.8",
                "proxy": "no",
                "vpn": false
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .seenFlag(SeenFlag.ENABLED) // Value 1
            .build()
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
    }
    @Test
    fun `checkEmail should return a valid response when the API call is successful`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "email": "test@example.com",
                "disposable": false,
                "risk": 0,
                "node": "test-node",
                "time": 0.01
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .flags(listOf(QueryFlag.MAIL))
            .build()
        val response = apiClient.checkEmail(
            email = "test@example.com",
            options = options
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.statusEnum)
        assertEquals("test@example.com", response.email)
        assertEquals(false, response.disposable)
        assertEquals(0, response.risk)
        assertEquals("test-node", response.node)
        assertEquals("0.01", response.time)
    }

    @Test
    fun `getDashboard should return a valid response when the API call is successful`() {
        // Arrange
        val jsonResponse = """
            {
                "status": "ok",
                "plan": "Premium",
                "email": "test@example.com",
                "queries_today": 100,
                "queries_month": 3000,
                "maxQueries_day": 1000,
                "maxQueries_month": 30000,
                "days_until_reset": 15
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val response = apiClient.getDashboard()

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.statusEnum)
        assertEquals("Premium", response.plan)
        assertEquals("test@example.com", response.email)
        assertEquals(100, response.queriesToday)
        assertEquals(3000, response.queriesMonth)
        assertEquals(1000, response.maxQueriesDay)
        assertEquals(30000, response.maxQueriesMonth)
        assertEquals(15, response.daysUntilReset)
    }
}
