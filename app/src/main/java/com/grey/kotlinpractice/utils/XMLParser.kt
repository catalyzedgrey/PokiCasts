package com.grey.kotlinpractice.utils

import android.util.Xml
import com.grey.kotlinpractice.data.Episode
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*

class XMLParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(`in`: InputStream): ArrayList<*> {
        return try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(`in`, null)
            parser.nextTag()
            readFeed(parser)
        } finally {
            `in`.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): ArrayList<*> {
        var entries: ArrayList<*> = ArrayList<Any?>()
        parser.require(XmlPullParser.START_TAG, ns, "rss")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            // Starts by looking for the entry tag
            if (name == "channel") {
                entries = readItem(parser)
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItem(parser: XmlPullParser): ArrayList<Episode> {
        parser.require(XmlPullParser.START_TAG, ns, "channel")
        val epArrayList: ArrayList<Episode> = ArrayList<Episode>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "item") {
                epArrayList.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return epArrayList
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): Episode {
        parser.require(XmlPullParser.START_TAG, ns, "item")
        var title: String? = null
        var description: String? = null
        var url: String? = null
        var length: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "title") {
                title = readTitle(parser)
            } else if (name == "description") {
                description = readDescription(parser)
            } else if (name == "itunes:duration") {
                length = readDuration(parser) //readlength(parser);
            } else if (name == "enclosure") {
                url = readUrl(parser)
                //length = readlength(parser);
                parser.nextTag()
            } else {
                skip(parser)
            }
        }
        return Episode(title!!, description!!, url!!, length!!)
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "title")
        return title
    }

    // Processes description tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDescription(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "description")
        val description = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "description")
        return description
    }

    // Processes description tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDuration(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:duration")
        val duration = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "itunes:duration")
        return duration
    }

    // Processes description tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readUrl(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "enclosure")
        //url="https://traffic.megaphone.fm/STA7192102916.mp3" length="226363350" type="audio/mpeg"
        return parser.getAttributeValue(null, "url")
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readlength(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "enclosure")
        //url="https://traffic.megaphone.fm/STA7192102916.mp3" length="226363350" type="audio/mpeg"
        return parser.getAttributeValue(null, "length")
    }

    // Processes description tags in the feed.
    //    private String readLength(XmlPullParser parser) throws IOException, XmlPullParserException {
    //        parser.require(XmlPullParser.START_TAG, ns, "enclosure");
    //        String description = readText(parser);
    //        parser.require(XmlPullParser.END_TAG, ns, "description");
    //        return description;
    //    }
    // For the tags title and description, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    //    private String readTaglessText(XmlPullParser parser) throws IOException, XmlPullParserException {
    //        String result = "";
    //        String test = parser.getAttributeValue(null, "url");
    //        //result = parser.getText();
    //        parser.nextTag();
    //
    //        return result;
    //    }
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    companion object {
        private val ns: String? = null
    }
}
