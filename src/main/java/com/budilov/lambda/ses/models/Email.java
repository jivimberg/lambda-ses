package com.budilov.lambda.ses.models;

public class Email {
    private String from;
    private String subject;
    private String messageKey;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String message) {
        this.messageKey = message;
    }

    @Override
    public String toString() {
        return "Email{" +
                "from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", messageKey='" + messageKey + '\'' +
                '}';
    }
}
