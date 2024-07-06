package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.PrivacyPoliciesTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class PrivacyPoliciesDAO {

    suspend fun update(text: String) = dbQuery {
        val existingPolicy = PrivacyPoliciesTable.selectAll().singleOrNull()

        if (existingPolicy == null)
            PrivacyPoliciesTable.insert {
                it[policyText] = text
                it[lastUpdated] = Instant.now(Clock.systemUTC())
            }
        else
            PrivacyPoliciesTable.update {
                it[policyText] = text
                it[lastUpdated] = Instant.now(Clock.systemUTC())
            }
    }

    suspend fun getPolicyText() = dbQuery {
        PrivacyPoliciesTable.selectAll().singleOrNull()?.get(PrivacyPoliciesTable.policyText) ?: "Privacy Policy"
    }
}