package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object PrivacyPoliciesTable : Table() {

    val policyText = text("policy_text")
    val lastUpdated = timestamp("last_updated")
}