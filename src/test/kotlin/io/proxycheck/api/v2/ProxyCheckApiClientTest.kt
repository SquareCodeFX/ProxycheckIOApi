package io.proxycheck.api.v2

import io.proxycheck.api.v2.client.ProxyCheckApiClient
import io.proxycheck.api.v2.exceptions.ApiKeyException
import io.proxycheck.api.v2.exceptions.RateLimitException
import io.proxycheck.api.v2.models.ProxyCheckOptions
import io.proxycheck.api.v2.models.enum.ProxyStatus
import io.proxycheck.api.v2.models.enum.ProxyType
import io.proxycheck.api.v2.models.enum.ResponseStatus
import io.proxycheck.api.v2.models.flag.*
import io.proxycheck.api.v2.models.response.ProxyCheckResponse
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
    fun `checkIp should include TAG flag with custom string value in the request`() {
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
            .tagFlag(TagFlag.of("my-custom-tag")) // Custom tag value
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

    @Test
    fun `getDashboard should include detection export data when EXPORT_DETECTIONS flag is used`() {
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
                "days_until_reset": 15,
                "detections": {
                    "1.2.3.4": {
                        "ip": "1.2.3.4",
                        "date": "2023-01-01",
                        "proxy_type": "VPN",
                        "risk": 80,
                        "country": "US",
                        "isocode": "US",
                        "asn": "AS12345",
                        "provider": "Example Provider",
                        "info": {
                            "vpn": true,
                            "tor": false
                        }
                    },
                    "5.6.7.8": {
                        "ip": "5.6.7.8",
                        "date": "2023-01-02",
                        "proxy_type": "TOR",
                        "risk": 90,
                        "country": "DE",
                        "isocode": "DE",
                        "asn": "AS67890",
                        "provider": "Another Provider",
                        "info": {
                            "vpn": false,
                            "tor": true
                        }
                    }
                }
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .flags(listOf(QueryFlag.EXPORT_DETECTIONS))
            .build()
        val response = apiClient.getDashboard(options)

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.statusEnum)
        assertNotNull(response.detections)
        assertEquals(2, response.detections?.size)

        val detection1 = response.detections?.get("1.2.3.4")
        assertNotNull(detection1)
        assertEquals("1.2.3.4", detection1?.ip)
        assertEquals("2023-01-01", detection1?.date)
        assertEquals("VPN", detection1?.proxyType)
        assertEquals(80, detection1?.risk)
        assertEquals("US", detection1?.country)
        assertEquals("US", detection1?.isoCode)
        assertEquals("AS12345", detection1?.asn)
        assertEquals("Example Provider", detection1?.provider)
        assertNotNull(detection1?.info)

        val detection2 = response.detections?.get("5.6.7.8")
        assertNotNull(detection2)
        assertEquals("5.6.7.8", detection2?.ip)
        assertEquals("2023-01-02", detection2?.date)
        assertEquals("TOR", detection2?.proxyType)
        assertEquals(90, detection2?.risk)
        assertEquals("DE", detection2?.country)
        assertEquals("DE", detection2?.isoCode)
        assertEquals("AS67890", detection2?.asn)
        assertEquals("Another Provider", detection2?.provider)
        assertNotNull(detection2?.info)
    }

    @Test
    fun `getDashboard should include usage export data when EXPORT_USAGE flag is used`() {
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
                "days_until_reset": 15,
                "usage": {
                    "daily": {
                        "2023-01-01": {
                            "queries": 100,
                            "detections": 20,
                            "date": "2023-01-01"
                        },
                        "2023-01-02": {
                            "queries": 150,
                            "detections": 30,
                            "date": "2023-01-02"
                        }
                    },
                    "monthly": {
                        "2023-01": {
                            "queries": 3000,
                            "detections": 600,
                            "month": "01",
                            "year": "2023"
                        },
                        "2022-12": {
                            "queries": 2800,
                            "detections": 550,
                            "month": "12",
                            "year": "2022"
                        }
                    },
                    "total": {
                        "queries": 10000,
                        "detections": 2000
                    }
                }
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .flags(listOf(QueryFlag.EXPORT_USAGE))
            .build()
        val response = apiClient.getDashboard(options)

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.statusEnum)
        assertNotNull(response.usage)

        // Check daily usage
        assertNotNull(response.usage?.dailyUsage)
        assertEquals(2, response.usage?.dailyUsage?.size)

        val day1 = response.usage?.dailyUsage?.get("2023-01-01")
        assertNotNull(day1)
        assertEquals(100, day1?.queries)
        assertEquals(20, day1?.detections)
        assertEquals("2023-01-01", day1?.date)

        val day2 = response.usage?.dailyUsage?.get("2023-01-02")
        assertNotNull(day2)
        assertEquals(150, day2?.queries)
        assertEquals(30, day2?.detections)
        assertEquals("2023-01-02", day2?.date)

        // Check monthly usage
        assertNotNull(response.usage?.monthlyUsage)
        assertEquals(2, response.usage?.monthlyUsage?.size)

        val month1 = response.usage?.monthlyUsage?.get("2023-01")
        assertNotNull(month1)
        assertEquals(3000, month1?.queries)
        assertEquals(600, month1?.detections)
        assertEquals("01", month1?.month)
        assertEquals("2023", month1?.year)

        val month2 = response.usage?.monthlyUsage?.get("2022-12")
        assertNotNull(month2)
        assertEquals(2800, month2?.queries)
        assertEquals(550, month2?.detections)
        assertEquals("12", month2?.month)
        assertEquals("2022", month2?.year)

        // Check total usage
        assertNotNull(response.usage?.totalUsage)
        assertEquals(10000, response.usage?.totalUsage?.queries)
        assertEquals(2000, response.usage?.totalUsage?.detections)
    }

    @Test
    fun `getDashboard should include custom lists data when CUSTOM_LISTS flag is used`() {
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
                "days_until_reset": 15,
                "lists": [
                    {
                        "id": "list1",
                        "name": "Blocked IPs",
                        "description": "List of blocked IP addresses",
                        "type": "ip",
                        "created_at": "2023-01-01T00:00:00Z",
                        "updated_at": "2023-01-02T00:00:00Z",
                        "entries": [
                            {
                                "id": "entry1",
                                "value": "1.2.3.4",
                                "note": "Malicious activity",
                                "added_at": "2023-01-01T12:00:00Z"
                            },
                            {
                                "id": "entry2",
                                "value": "5.6.7.8",
                                "note": "Spam",
                                "added_at": "2023-01-02T12:00:00Z"
                            }
                        ]
                    },
                    {
                        "id": "list2",
                        "name": "Allowed IPs",
                        "description": "List of allowed IP addresses",
                        "type": "ip",
                        "created_at": "2023-01-03T00:00:00Z",
                        "updated_at": "2023-01-04T00:00:00Z",
                        "entries": [
                            {
                                "id": "entry3",
                                "value": "9.10.11.12",
                                "note": "Trusted client",
                                "added_at": "2023-01-03T12:00:00Z"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .flags(listOf(QueryFlag.CUSTOM_LISTS))
            .build()
        val response = apiClient.getDashboard(options)

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.statusEnum)
        assertNotNull(response.customLists)
        assertEquals(2, response.customLists?.size)

        val list1 = response.customLists?.get(0)
        assertNotNull(list1)
        assertEquals("list1", list1?.id)
        assertEquals("Blocked IPs", list1?.name)
        assertEquals("List of blocked IP addresses", list1?.description)
        assertEquals("ip", list1?.type)
        assertEquals("2023-01-01T00:00:00Z", list1?.createdAt)
        assertEquals("2023-01-02T00:00:00Z", list1?.updatedAt)
        assertNotNull(list1?.entries)
        assertEquals(2, list1?.entries?.size)

        val entry1 = list1?.entries?.get(0)
        assertNotNull(entry1)
        assertEquals("entry1", entry1?.id)
        assertEquals("1.2.3.4", entry1?.value)
        assertEquals("Malicious activity", entry1?.note)
        assertEquals("2023-01-01T12:00:00Z", entry1?.addedAt)

        val list2 = response.customLists?.get(1)
        assertNotNull(list2)
        assertEquals("list2", list2?.id)
        assertEquals("Allowed IPs", list2?.name)
        assertEquals(1, list2?.entries?.size)
    }

    @Test
    fun `getDashboard should include CORS status when CORS flag is used`() {
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
                "days_until_reset": 15,
                "cors": {
                    "enabled": true,
                    "allowed_origins": ["https://example.com", "https://test.com"],
                    "allowed_methods": ["GET", "POST", "OPTIONS"],
                    "allowed_headers": ["Content-Type", "Authorization"],
                    "allow_credentials": true,
                    "max_age": 3600
                }
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .flags(listOf(QueryFlag.CORS))
            .build()
        val response = apiClient.getDashboard(options)

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.statusEnum)
        assertNotNull(response.corsStatus)

        assertEquals(true, response.corsStatus?.enabled)
        assertNotNull(response.corsStatus?.allowedOrigins)
        assertEquals(2, response.corsStatus?.allowedOrigins?.size)
        assertEquals("https://example.com", response.corsStatus?.allowedOrigins?.get(0))
        assertEquals("https://test.com", response.corsStatus?.allowedOrigins?.get(1))

        assertNotNull(response.corsStatus?.allowedMethods)
        assertEquals(3, response.corsStatus?.allowedMethods?.size)
        assertEquals("GET", response.corsStatus?.allowedMethods?.get(0))
        assertEquals("POST", response.corsStatus?.allowedMethods?.get(1))
        assertEquals("OPTIONS", response.corsStatus?.allowedMethods?.get(2))

        assertNotNull(response.corsStatus?.allowedHeaders)
        assertEquals(2, response.corsStatus?.allowedHeaders?.size)
        assertEquals("Content-Type", response.corsStatus?.allowedHeaders?.get(0))
        assertEquals("Authorization", response.corsStatus?.allowedHeaders?.get(1))

        assertEquals(true, response.corsStatus?.allowCredentials)
        assertEquals(3600, response.corsStatus?.maxAge)
    }

    @Test
    fun `getDashboard should include tag export data when EXPORT_TAGS flag is used`() {
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
                "days_until_reset": 15,
                "tags": {
                    "my-custom-tag": {
                        "name": "my-custom-tag",
                        "count": 150,
                        "first_used": "2023-01-01",
                        "last_used": "2023-01-15",
                        "info": {
                            "detections": 30,
                            "queries": 150
                        }
                    },
                    "another-tag": {
                        "name": "another-tag",
                        "count": 75,
                        "first_used": "2023-01-05",
                        "last_used": "2023-01-10",
                        "info": {
                            "detections": 15,
                            "queries": 75
                        }
                    }
                }
            }
        """.trimIndent()

        whenever(mockResponseBody.string()).thenReturn(jsonResponse)

        // Act
        val options = ProxyCheckOptions.builder()
            .flags(listOf(QueryFlag.EXPORT_TAGS))
            .build()
        val response = apiClient.getDashboard(options)

        // Assert
        assertNotNull(response)
        assertEquals(ResponseStatus.SUCCESS, response.statusEnum)
        assertNotNull(response.tags)
        assertEquals(2, response.tags?.size)

        val tag1 = response.tags?.get("my-custom-tag")
        assertNotNull(tag1)
        assertEquals("my-custom-tag", tag1?.name)
        assertEquals(150, tag1?.count)
        assertEquals("2023-01-01", tag1?.firstUsed)
        assertEquals("2023-01-15", tag1?.lastUsed)
        assertNotNull(tag1?.info)

        val tag2 = response.tags?.get("another-tag")
        assertNotNull(tag2)
        assertEquals("another-tag", tag2?.name)
        assertEquals(75, tag2?.count)
        assertEquals("2023-01-05", tag2?.firstUsed)
        assertEquals("2023-01-10", tag2?.lastUsed)
        assertNotNull(tag2?.info)
    }
}
