package io.proxycheck.api.v2.models.response

import com.google.gson.annotations.SerializedName
import io.proxycheck.api.v2.models.enum.ResponseStatus
import io.proxycheck.api.v2.models.response.ApiResponse

/**
 * Represents the response from the ProxyCheck.io Email Check API.
 */
data class EmailCheckResponse(
    /**
     * The status of the request as a string.
     * @see statusEnum for the enum representation.
     */
    @SerializedName("status")
    private val statusString: String,

    /**
     * The email address that was queried.
     */
    val email: String? = null,

    /**
     * Whether the email is from a disposable email provider.
     */
    val disposable: Boolean? = null,

    /**
     * The risk score of the email address (0-100).
     */
    val risk: Int? = null,

    /**
     * The message from the API if there was an error.
     */
    override val message: String? = null,

    /**
     * The node that processed the request.
     */
    override val node: String? = null,

    /**
     * The time it took to process the request in milliseconds.
     */
    override val time: String? = null
) : ApiResponse {
    /**
     * The status of the request as an enum.
     */
    val statusEnum: ResponseStatus
        get() = ResponseStatus.fromString(statusString) ?: ResponseStatus.ERROR

    /**
     * The status of the request.
     * Implementation of ApiResponse interface.
     */
    override val status: ResponseStatus
        get() = statusEnum
}
