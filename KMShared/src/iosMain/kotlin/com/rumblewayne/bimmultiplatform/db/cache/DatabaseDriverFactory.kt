package com.rumblewayne.bimmultiplatform.db.cache

import com.rumblewayne.bimmultiplatform.db.cache.KMSharedDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KMSharedDatabase.Schema, "colors.db")
    }
}