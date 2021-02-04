package com.bykhkalo.redditapiclient.utils

import java.sql.Timestamp

class TimeUtils {

    companion object{
        private const val SECOND_MILLIS: Long = 1000
        private const val MINUTE_MILLIS: Long = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS: Long = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS: Long = 24 * HOUR_MILLIS

        fun getTimeAgo(time: Int): String {

            var formattedTime: Int = time

            if (formattedTime < 1000000000000L) {
                formattedTime *= 1000
            }

            val now: Int = Timestamp(System.currentTimeMillis()).time.toInt()

            if (formattedTime > now || formattedTime <= 0) {
                throw Exception("Invalid timestamp value!")
            }

            val diff = now - formattedTime
            return if (diff < MINUTE_MILLIS) {
                "just now"
            } else if (diff < 2 * MINUTE_MILLIS) {
                "a minute ago"
            } else if (diff < 50 * MINUTE_MILLIS) {
                (diff / MINUTE_MILLIS).toString() + " minutes ago"
            } else if (diff < 90 * MINUTE_MILLIS) {
                "an hour ago"
            } else if (diff < 24 * HOUR_MILLIS) {
                (diff / HOUR_MILLIS).toString() + " hours ago"
            } else if (diff < 48 * HOUR_MILLIS) {
                "yesterday"
            } else {
                (diff / DAY_MILLIS).toString() + " days ago"
            }
        }
    }




}