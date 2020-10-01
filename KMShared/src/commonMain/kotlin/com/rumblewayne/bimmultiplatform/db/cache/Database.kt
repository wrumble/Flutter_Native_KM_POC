package com.rumblewayne.bimmultiplatform.db.cache

import BGColor

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    var colorListener: ((String) -> Unit)? = null
    private val database = KMSharedDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.kMSharedDatabaseQueries

    fun fetchColor(): BGColor? {
        println("WTF fetchColor")
        return dbQuery
                .getColors()
                .executeAsList()
                .map { BGColor(name = it.name) }
                .firstOrNull()
    }

    fun cacheBackgroundColor(color: String) {
        println("WTF cacheBackgroundColor: $color")
        val bgColor = BGColor(name = color)
        clearDatabase()
        dbQuery.saveColor(bgColor.name, bgColor.hex)
        colorListener?.invoke(bgColor.hex)
    }

    private fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.getColors()
        }
    }
}