package io.proxycheck.api.v2.models

import com.google.gson.annotations.SerializedName

/**
 * Represents the CORS status from the ProxyCheck.io Dashboard API.
 */
data class CorsStatus(
    /**
     * Whether CORS is enabled.
     */
    val enabled: Boolean? = null,
    
    /**
     * The allowed origins for CORS.
     */
    @SerializedName("allowed_origins")
    val allowedOrigins: List<String>? = null,
    
    /**
     * The allowed methods for CORS.
     */
    @SerializedName("allowed_methods")
    val allowedMethods: List<String>? = null,
    
    /**
     * The allowed headers for CORS.
     */
    @SerializedName("allowed_headers")
    val allowedHeaders: List<String>? = null,
    
    /**
     * Whether credentials are allowed for CORS.
     */
    @SerializedName("allow_credentials")
    val allowCredentials: Boolean? = null,
    
    /**
     * The maximum age for CORS preflight requests.
     */
    @SerializedName("max_age")
    val maxAge: Int? = null
)
