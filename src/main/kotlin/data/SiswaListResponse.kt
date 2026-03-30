package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class SiswaListResponse(
    val posts: List<SiswaResponse>,
    val limit: Int,
    val offset: Long,
    val hasMore: Boolean
)