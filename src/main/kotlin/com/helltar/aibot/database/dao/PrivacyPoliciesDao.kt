package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbQuery
import com.helltar.aibot.database.tables.PrivacyPoliciesTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class PrivacyPoliciesDao {

    suspend fun update(text: String) = dbQuery {
        val now = Instant.now(Clock.systemUTC())

        PrivacyPoliciesTable
            .selectAll()
            .singleOrNull()
            ?.let {
                PrivacyPoliciesTable
                    .update {
                        it[policyText] = text
                        it[lastUpdated] = now
                    }
            }
            ?: PrivacyPoliciesTable
                .insert {
                    it[policyText] = text
                    it[lastUpdated] = now
                }
    }

    suspend fun getPolicyText() = dbQuery {
        PrivacyPoliciesTable
            .select(PrivacyPoliciesTable.policyText)
            .singleOrNull()?.get(PrivacyPoliciesTable.policyText) ?: "Privacy Policy"
    }
}

val privacyPoliciesDao = PrivacyPoliciesDao()
