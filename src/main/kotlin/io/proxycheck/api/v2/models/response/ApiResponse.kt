package io.proxycheck.api.v2.models.response

import io.proxycheck.api.v2.models.enum.ResponseStatus

/**
 * Interface representing a common structure for all API responses.
 */
interface ApiResponse {
    /**
     * The status of the request.
     */
    val status: ResponseStatus

    /**
     * The message from the API if there was an error.
     */
    val message: String?

    /**
     * The node that processed the request.
     */
    val node: String?

    /**
     * The time it took to process the request in milliseconds.
     */
    val time: String?
}
