package ru.edu.pr12;

public class NotificationService {
    private final ChannelFactory factory;

    // фабрика передается снаружи, сервис не создает каналы сам
    public NotificationService(ChannelFactory f) {
        factory = f;
    }

    public NotificationResult notify(NotificationRequest r) {
        if (r == null || r.type() == null) {
            throw new IllegalArgumentException("Не указан запрос или тип канала");
        }
        if (r.message() == null || r.message().isBlank()) {
            return new NotificationResult(false, "пустой текст сообщения");
        }
        NotificationChannel c = factory.get(r.type());
        if (!c.supports(r)) {
            return new NotificationResult(false, "канал " + r.type() + " отклонил запрос, адресат " + r.recipient());
        }
        return c.send(r);
    }
}
