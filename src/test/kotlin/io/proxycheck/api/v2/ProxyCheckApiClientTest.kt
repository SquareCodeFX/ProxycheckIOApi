package io.proxycheck.api.v2

import io.proxycheck.api.v2.exceptions.ApiKeyException
import io.proxycheck.api.v2.exceptions.RateLimitException
import io.proxycheck.api.v2.models.ProxyCheckResponse
import io.proxycheck.api.v2.models.QueryFlag
import io.proxycheck.api.v2.models.ResponseStatus
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
        val response = apiClient.checkIp(
            ip = "8.8.8.8",
            flags = listOf(QueryFlag.VPN, QueryFlag.ASN, QueryFlag.TIME)
        )

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.status)
        assertEquals("8.8.8.8", response.ip)
        assertEquals("no", response.proxyString)
        assertEquals(15169, response.asn)
        assertEquals(0.01, response.time)
    }
}
