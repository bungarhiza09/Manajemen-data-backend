package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class GuruListResponse(
    val guru: List<GuruResponse>,
    val limit: Int,
    val offset: Long,
    val hasMore: Boolean
)