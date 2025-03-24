package io.proxycheck.api.v2.models

import io.proxycheck.api.v2.models.flag.*
import java.util.concurrent.TimeUnit

/**
 * Options for ProxyCheck API requests.
 * This class encapsulates all the optional parameters that can be used with the ProxyCheck.io API.
 *
 * @property flags The query flags to include in the request.
 * @property vpnDetection Whether to enable VPN detection (boolean flag).
 * @property vpnFlag The VPN detection level, overrides vpnDetection if provided.
 * @property asn Whether to include ASN data in the response (boolean flag).
 * @property asnFlag The ASN data level, overrides asn if provided.
 * @property node Whether to include the node that processed the request in the response.
 * @property nodeFlag The node flag, overrides node if provided.
 * @property time Whether to include the time it took to process the request in the response.
 * @property timeFlag The time flag, overrides time if provided.
 * @property inf Whether to include additional information.
 * @property infFlag The inf flag, overrides inf if provided.
 * @property risk Whether to include the risk score in the response.
 * @property riskFlag The risk flag, overrides risk if provided.
 * @property port Whether to include the port used by the proxy in the response.
 * @property portFlag The port flag, overrides port if provided.
 * @property seen Whether to include the seen date of the proxy in the response.
 * @property seenFlag The seen flag, overrides seen if provided.
 * @property days Whether to include the days since the proxy was first detected in the response.
 * @property daysFlag The days flag, overrides days if provided.
 * @property tag A custom tag to identify the request.
 * @property tagFlag The tag flag, provides a custom string for query tagging.
 * @property verFlag The version flag.
 * @property useSSL Whether to use SSL for the request.
 * @property cacheTime The time to cache the response, or null to use the default.
 * @property cacheTimeUnit The time unit for cacheTime, or null to use the default.
 */
data class ProxyCheckOptions(
    val flags: List<QueryFlag> = emptyList(),
    val vpnDetection: Boolean = false,
    val vpnFlag: VpnFlag? = null,
    val asn: Boolean = false,
    val asnFlag: AsnFlag? = null,
    val node: Boolean = false,
    val nodeFlag: NodeFlag? = null,
    val time: Boolean = false,
    val timeFlag: TimeFlag? = null,
    val inf: Boolean = false,
    val infFlag: InfFlag? = null,
    val risk: Boolean = false,
    val riskFlag: RiskFlag? = null,
    val port: Boolean = false,
    val portFlag: PortFlag? = null,
    val seen: Boolean = false,
    val seenFlag: SeenFlag? = null,
    val days: Boolean = false,
    val daysFlag: DaysFlag? = null,
    val tag: String? = null,
    val tagFlag: TagFlag? = null,
    val verFlag: VerFlag? = null,
    val useSSL: Boolean = true,
    val cacheTime: Long? = null,
    val cacheTimeUnit: TimeUnit? = null
) {
    /**
     * Builder class for creating ProxyCheckOptions instances.
     */
    class Builder {
        private var flags: List<QueryFlag> = emptyList()
        private var vpnDetection: Boolean = false
        private var vpnFlag: VpnFlag? = null
        private var asn: Boolean = false
        private var asnFlag: AsnFlag? = null
        private var node: Boolean = false
        private var nodeFlag: NodeFlag? = null
        private var time: Boolean = false
        private var timeFlag: TimeFlag? = null
        private var inf: Boolean = false
        private var infFlag: InfFlag? = null
        private var risk: Boolean = false
        private var riskFlag: RiskFlag? = null
        private var port: Boolean = false
        private var portFlag: PortFlag? = null
        private var seen: Boolean = false
        private var seenFlag: SeenFlag? = null
        private var days: Boolean = false
        private var daysFlag: DaysFlag? = null
        private var tag: String? = null
        private var tagFlag: TagFlag? = null
        private var verFlag: VerFlag? = null
        private var useSSL: Boolean = true
        private var cacheTime: Long? = null
        private var cacheTimeUnit: TimeUnit? = null

        /**
         * Set the query flags.
         */
        fun flags(flags: List<QueryFlag>) = apply { this.flags = flags }

        /**
         * Enable VPN detection.
         */
        fun vpnDetection(enabled: Boolean = true) = apply { this.vpnDetection = enabled }

        /**
         * Set the VPN flag.
         */
        fun vpnFlag(flag: VpnFlag) = apply { this.vpnFlag = flag }

        /**
         * Enable ASN data.
         */
        fun asn(enabled: Boolean = true) = apply { this.asn = enabled }

        /**
         * Set the ASN flag.
         */
        fun asnFlag(flag: AsnFlag) = apply { this.asnFlag = flag }

        /**
         * Enable node information.
         */
        fun node(enabled: Boolean = true) = apply { this.node = enabled }

        /**
         * Set the node flag.
         */
        fun nodeFlag(flag: NodeFlag) = apply { this.nodeFlag = flag }

        /**
         * Enable time information.
         */
        fun time(enabled: Boolean = true) = apply { this.time = enabled }

        /**
         * Set the time flag.
         */
        fun timeFlag(flag: TimeFlag) = apply { this.timeFlag = flag }

        /**
         * Enable additional information.
         */
        fun inf(enabled: Boolean = true) = apply { this.inf = enabled }

        /**
         * Set the inf flag.
         */
        fun infFlag(flag: InfFlag) = apply { this.infFlag = flag }

        /**
         * Enable risk score.
         */
        fun risk(enabled: Boolean = true) = apply { this.risk = enabled }

        /**
         * Set the risk flag.
         */
        fun riskFlag(flag: RiskFlag) = apply { this.riskFlag = flag }

        /**
         * Enable port information.
         */
        fun port(enabled: Boolean = true) = apply { this.port = enabled }

        /**
         * Set the port flag.
         */
        fun portFlag(flag: PortFlag) = apply { this.portFlag = flag }

        /**
         * Enable seen date information.
         */
        fun seen(enabled: Boolean = true) = apply { this.seen = enabled }

        /**
         * Set the seen flag.
         */
        fun seenFlag(flag: SeenFlag) = apply { this.seenFlag = flag }

        /**
         * Enable days information.
         */
        fun days(enabled: Boolean = true) = apply { this.days = enabled }

        /**
         * Set the days flag.
         */
        fun daysFlag(flag: DaysFlag) = apply { this.daysFlag = flag }

        /**
         * Set a custom tag.
         */
        fun tag(tag: String) = apply { this.tag = tag }

        /**
         * Set the tag flag.
         */
        fun tagFlag(flag: TagFlag) = apply { this.tagFlag = flag }

        /**
         * Set the version flag.
         */
        fun verFlag(flag: VerFlag) = apply { this.verFlag = flag }

        /**
         * Set whether to use SSL.
         */
        fun useSSL(useSSL: Boolean) = apply { this.useSSL = useSSL }

        /**
         * Set the cache time.
         */
        fun cacheTime(time: Long, unit: TimeUnit) = apply {
            this.cacheTime = time
            this.cacheTimeUnit = unit
        }

        /**
         * Build the ProxyCheckOptions instance.
         */
        fun build() = ProxyCheckOptions(
            flags, vpnDetection, vpnFlag, asn, asnFlag, node, nodeFlag, time, timeFlag,
            inf, infFlag, risk, riskFlag, port, portFlag, seen, seenFlag, days, daysFlag,
            tag, tagFlag, verFlag, useSSL, cacheTime, cacheTimeUnit
        )
    }

    companion object {
        /**
         * Create a new builder for ProxyCheckOptions.
         */
        fun builder() = Builder()
    }
}
