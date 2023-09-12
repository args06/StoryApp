package com.example.storyapp.utils

import com.example.storyapp.data.local.entity.StoryEntity

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..50) {
            val story = StoryEntity(
                i.toString(), "name + $i", "desc $i", "photo $i", "createAt $i", 121.0, 124.0
            )
            items.add(story)
        }
        return items
    }
}