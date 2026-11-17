package ru.edu.pr12;

// стратегия отправки: каждый канал сам проверяет своего адресата
public interface NotificationChannel {
    boolean supports(NotificationRequest r);

    NotificationResult send(NotificationRequest r);
}
