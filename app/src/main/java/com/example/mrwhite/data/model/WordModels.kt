package com.example.mrwhite.data.model

enum class WordCategory(val displayName: String) {
    ANY("Any Category"),
    FOOD("Food"),
    ANIMALS("Animals"),
    COUNTRIES("Countries"),
    CITIES("Cities"),
    PLACES("Places"),
    NATURE("Nature"),
    OBJECTS("Objects"),
    VEHICLES("Vehicles"),
    TRAVEL("Travel"),
    SPORTS("Sports"),
    TECHNOLOGY("Technology"),
    ENTERTAINMENT("Entertainment"),
    CLOTHING("Clothing"),
    HOUSEHOLD("Household"),
    PROFESSIONS("Professions"),
    ACTIVITIES("Activities"),
    SCHOOL("School"),
    SCIENCE("Science"),
    WEATHER("Weather"),
    GEOGRAPHY("Geography"),
    MUSIC("Music"),
    MOVIES("Movies"),
    EVERYDAY_LIFE("Everyday Life"),
    MIXED("Mixed")
}

data class WordPairData(
    val category: WordCategory,
    val civilianWord: String,
    val undercoverWord: String,
    val tags: Set<String> = emptySet()
) {
    val pairKey: String
        get() = "${civilianWord.lowercase()}|${undercoverWord.lowercase()}"
}
