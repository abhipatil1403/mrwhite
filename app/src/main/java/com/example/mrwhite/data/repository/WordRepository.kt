package com.example.mrwhite.data.repository

class WordRepository {
    private val wordPairs = listOf(
        Pair("Apple", "Orange"),
        Pair("Car", "Bus"),
        Pair("Dog", "Cat"),
        Pair("Sun", "Moon"),
        Pair("Guitar", "Piano")
    )

    fun getRandomWordPair(): Pair<String, String> {
        return wordPairs.random()
    }
}
