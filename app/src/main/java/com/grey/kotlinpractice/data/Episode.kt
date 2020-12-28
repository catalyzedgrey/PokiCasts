package com.grey.kotlinpractice.data

import tw.ktrssreader.annotation.*
import java.io.Serializable


@RssTag(name = "channel")
data class Episode(val title: String?, val url: String?,
                   @RssTag(name = "description", order = [OrderType.ITUNES, OrderType.RSS_STANDARD])
                   val description: String?,
                   val pubDate: String?,
                   @RssTag(name = "duration", order = [OrderType.ITUNES, OrderType.RSS_STANDARD])
                   val duration: String?):
    Serializable {}
