package org.delcom.helpers

object JWTConstants {
    const val NAME     = "auth-jwt"
    const val REALM    = "delcom-realm"
    const val ISSUER   = "delcom-app"
    const val AUDIENCE = "delcom-user"
}

// Nama claim role di dalam JWT payload
object RoleClaims {
    const val ROLE_CLAIM = "role"   // isi: "admin" atau "siswa"
    const val USER_ID    = "userId" // isi: id user yang login
}

// Nilai role yang dipakai di sistem
object Roles {
    const val ADMIN = "admin"
    const val SISWA = "siswa"
}
