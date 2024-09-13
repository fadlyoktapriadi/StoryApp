package com.example.storyapp

import com.example.storyapp.data.response.ListStoryItem

object DataDummy {

    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "2024-04-21T06:30:31.213Z",
                "Fadly Oktapriadi",
                "Tes Deskripsi",
                11.532102,
                "story-$i",
                -12.40012
            )
            items.add(quote)
        }
        return items
    }
}