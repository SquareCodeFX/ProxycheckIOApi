package io.proxycheck.api.v2.models

import com.google.gson.annotations.SerializedName

/**
 * Represents tag data exported from the ProxyCheck.io Dashboard API.
 */
data class TagData(
    /**
     * The tag name.
     */
    val name: String? = null,
    
    /**
     * The number of times this tag has been used.
     */
    val count: Int? = null,
    
    /**
     * The date when the tag was first used.
     */
    @SerializedName("first_used")
    val firstUsed: String? = null,
    
    /**
     * The date when the tag was last used.
     */
    @SerializedName("last_used")
    val lastUsed: String? = null,
    
    /**
     * Additional information about the tag.
     */
    val info: Map<String, Any>? = null
)
