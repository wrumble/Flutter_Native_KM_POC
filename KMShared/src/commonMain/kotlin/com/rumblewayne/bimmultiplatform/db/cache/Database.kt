package com.rumblewayne.bimmultiplatform.db.cache

import BGColor
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.map

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = KMSharedDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.kMSharedDatabaseQueries

    val backgroundColorFlow: CommonFlow<BGColor> =
            dbQuery.getColorWithId(BGColor.id)
                    .asFlow()
                    .mapToOneNotNull()
                    .map { BGColor(it.name) }
                    .asCommonFlow()


    fun cacheBackgroundColor(color: String) {
        val bgColor = BGColor(color)
        dbQuery.saveOrUpdateColor(BGColor.id, bgColor.name, bgColor.hex)
    }
}