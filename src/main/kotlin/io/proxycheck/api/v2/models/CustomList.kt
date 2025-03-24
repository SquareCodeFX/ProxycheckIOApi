package io.proxycheck.api.v2.models

import com.google.gson.annotations.SerializedName

/**
 * Represents a custom list from the ProxyCheck.io Dashboard API.
 */
data class CustomList(
    /**
     * The ID of the custom list.
     */
    val id: String? = null,
    
    /**
     * The name of the custom list.
     */
    val name: String? = null,
    
    /**
     * The description of the custom list.
     */
    val description: String? = null,
    
    /**
     * The type of the custom list.
     */
    val type: String? = null,
    
    /**
     * The date when the custom list was created.
     */
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    /**
     * The date when the custom list was last updated.
     */
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    /**
     * The entries in the custom list.
     */
    val entries: List<CustomListEntry>? = null
)

/**
 * Represents an entry in a custom list.
 */
data class CustomListEntry(
    /**
     * The ID of the entry.
     */
    val id: String? = null,
    
    /**
     * The value of the entry.
     */
    val value: String? = null,
    
    /**
     * The note for the entry.
     */
    val note: String? = null,
    
    /**
     * The date when the entry was added.
     */
    @SerializedName("added_at")
    val addedAt: String? = null
)
