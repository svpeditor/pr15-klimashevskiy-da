package ru.edu.pr12;

// вариант 4: push-уведомление на устройство, текст не длиннее 25 символов
public class PushChannel implements NotificationChannel {
    public static final int MAX_LENGTH = 25;

    // адресат - токен устройства без пробелов
    public boolean supports(NotificationRequest r) {
        String to = r.recipient();
        if (to == null || to.isBlank() || to.contains(" ")) {
            return false;
        }
        return r.message() != null && r.message().length() <= MAX_LENGTH;
    }

    public NotificationResult send(NotificationRequest r) {
        return new NotificationResult(true, "push отправлен на устройство " + r.recipient());
    }
}
