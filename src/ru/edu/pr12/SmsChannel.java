package ru.edu.pr12;

public class SmsChannel implements NotificationChannel {
    // номер в формате +7 и 10 цифр
    public boolean supports(NotificationRequest r) {
        return r.recipient() != null && r.recipient().matches("\\+7\\d{10}");
    }

    public NotificationResult send(NotificationRequest r) {
        return new NotificationResult(true, "sms отправлено на " + r.recipient());
    }
}
