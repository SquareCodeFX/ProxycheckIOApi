package io.proxycheck.api.v2.models

import com.google.gson.annotations.SerializedName

/**
 * Represents the response from the ProxyCheck.io API.
 */
data class ProxyCheckResponse(
    /**
     * The status of the request as a string.
     * @see statusEnum for the enum representation.
     */
    @SerializedName("status")
    private val statusString: String,

    /**
     * The IP address that was queried.
     */
    val ip: String? = null,

    /**
     * The proxy status of the IP address as a string.
     * @see proxyEnum for the enum representation.
     */
    @SerializedName("proxy") val proxyString: String? = null,

    /**
     * The type of proxy if the IP is a proxy as a string.
     * @see typeEnum for the enum representation.
     */
    @SerializedName("type") val typeString: String? = null,

    /**
     * The risk score of the IP address (0-100).
     */
    val risk: Int? = null,

    /**
     * The country code of the IP address.
     */
    @SerializedName("isocode")
    val isoCode: String? = null,

    /**
     * The country of the IP address.
     */
    val country: String? = null,

    /**
     * The region/state of the IP address.
     */
    val region: String? = null,

    /**
     * The city of the IP address.
     */
    val city: String? = null,

    /**
     * The ISP of the IP address.
     */
    val isp: String? = null,

    /**
     * The ASN of the IP address.
     */
    val asn: Int? = null,

    /**
     * The organization of the IP address.
     */
    val organization: String? = null,

    /**
     * The hostname of the IP address.
     */
    val hostname: String? = null,

    /**
     * The provider of the IP address.
     */
    val provider: String? = null,

    /**
     * The VPN status of the IP address.
     */
    val vpn: Boolean? = null,

    /**
     * The TOR status of the IP address.
     */
    val tor: Boolean? = null,

    /**
     * The time it took to process the request in milliseconds.
     */
    override val time: String? = null,

    /**
     * The message from the API if there was an error.
     */
    override val message: String? = null,

    /**
     * The block status of the IP address.
     */
    val block: Int? = null,

    /**
     * The node that processed the request.
     */
    override val node: String? = null
) : ApiResponse {
    /**
     * The status of the request as an enum.
     */
    val statusEnum: ResponseStatus
        get() = ResponseStatus.fromString(statusString) ?: ResponseStatus.ERROR

    /**
     * The proxy status of the IP address as an enum.
     */
    val proxyEnum: ProxyStatus
        get() = ProxyStatus.fromString(proxyString)

    /**
     * The type of proxy if the IP is a proxy as an enum.
     */
    val typeEnum: ProxyType
        get() = ProxyType.fromString(typeString)

    /**
     * The status of the request.
     * Implementation of ApiResponse interface.
     */
    override val status: ResponseStatus
        get() = statusEnum
}
