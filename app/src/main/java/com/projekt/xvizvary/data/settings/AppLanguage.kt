package com.projekt.xvizvary.data.settings

enum class AppLanguage(val languageTag: String) {
    EN("en"),
    CS("cs");

    companion object {
        fun fromLanguageTag(tag: String?): AppLanguage {
            return entries.firstOrNull { it.languageTag.equals(tag, ignoreCase = true) } ?: EN
        }
    }
}

