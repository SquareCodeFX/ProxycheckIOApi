package io.proxycheck.api.v2.models

import com.google.gson.annotations.SerializedName

/**
 * Represents detection data exported from the ProxyCheck.io Dashboard API.
 */
data class DetectionData(
    /**
     * The IP address of the detection.
     */
    val ip: String? = null,
    
    /**
     * The date when the detection was made.
     */
    val date: String? = null,
    
    /**
     * The proxy type of the detection.
     */
    @SerializedName("proxy_type")
    val proxyType: String? = null,
    
    /**
     * The risk score of the detection.
     */
    val risk: Int? = null,
    
    /**
     * The country of the detection.
     */
    val country: String? = null,
    
    /**
     * The ISO code of the country.
     */
    @SerializedName("isocode")
    val isoCode: String? = null,
    
    /**
     * The ASN of the detection.
     */
    val asn: String? = null,
    
    /**
     * The provider of the detection.
     */
    val provider: String? = null,
    
    /**
     * Additional information about the detection.
     */
    val info: Map<String, Any>? = null
)
