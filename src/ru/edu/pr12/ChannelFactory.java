package ru.edu.pr12;

import java.util.EnumMap;
import java.util.Map;

// только выбирает канал по типу, сама ничего не отправляет
public class ChannelFactory {
    private final Map<ChannelType, NotificationChannel> channels = new EnumMap<>(ChannelType.class);

    public ChannelFactory() {
        channels.put(ChannelType.EMAIL, new EmailChannel());
        channels.put(ChannelType.SMS, new SmsChannel());
        channels.put(ChannelType.INTERNAL, new InternalChannel());
    }

    // свои каналы, например заглушки для проверки сервиса
    public ChannelFactory(Map<ChannelType, NotificationChannel> channels) {
        this.channels.putAll(channels);
    }

    public NotificationChannel get(ChannelType t) {
        NotificationChannel channel = channels.get(t);
        if (channel == null) {
            throw new IllegalArgumentException("Нет канала для типа " + t);
        }
        return channel;
    }
}
