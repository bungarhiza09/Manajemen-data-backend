package org.delcom.module

import org.delcom.repositories.GuruRepository
import org.delcom.repositories.SiswaRepository
import org.delcom.service.GuruService
import org.delcom.service.SiswaService
import org.koin.dsl.module

val appModule = module {

    // ===== SISWA =====
    single {
        SiswaRepository()
    }

    single {
        SiswaService(get())
    }

    // ===== GURU =====
    single {
        GuruRepository()
    }

    single {
        GuruService(get())
    }
}