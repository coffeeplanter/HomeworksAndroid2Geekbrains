package ru.geekbrains.android2.lesson26;

import java.util.Calendar;

/**
 * Класс для хранения информации о сообщении
 */

class SmsMessage {

    private String from;
    private Calendar date;
    private String body;

    SmsMessage() {
        this.date = Calendar.getInstance();
    }

    String getFrom() {
        return from;
    }

    void setFrom(String from) {
        this.from = from;
    }

    Calendar getDate() {
        return date;
    }

    void setDate(Calendar date) {
        this.date = date;
    }

    void setDate(long milliSeconds) {
        date.setTimeInMillis(milliSeconds);
    }

    String getBody() {
        return body;
    }

    void setBody(String body) {
        this.body = body;
    }
}
