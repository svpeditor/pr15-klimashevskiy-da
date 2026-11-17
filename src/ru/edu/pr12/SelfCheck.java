package ru.edu.pr12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SelfCheck {
    private static final NotificationService SERVICE = new NotificationService(new ChannelFactory());
    private static int passed = 0;
    private static int count = 0;

    public static void main(String[] args) {
        checkEmail();
        checkSms();
        checkInternalAndFactory();
        checkServiceWithFake();
        checkVariant();
        System.out.println("Unit: " + passed + "/" + count + " passed");
        // свой файл: java -cp out ru.edu.pr12.SelfCheck data/my.csv
        runFile(args.length > 0 ? args[0] : "data/sample.csv");
    }

    private static void checkEmail() {
        check("email ok", sent(ChannelType.EMAIL, "user@example.com"));
        check("email short", sent(ChannelType.EMAIL, "a@b.c"));
        check("email without @", !sent(ChannelType.EMAIL, "user.example.com"));
        check("email two @", !sent(ChannelType.EMAIL, "user@@example.com"));
        check("email empty name", !sent(ChannelType.EMAIL, "@example.com"));
        check("email no dot", !sent(ChannelType.EMAIL, "user@localhost"));
        check("email dot at end", !sent(ChannelType.EMAIL, "user@example."));
    }

    private static void checkSms() {
        check("sms ok", sent(ChannelType.SMS, "+79990000000"));
        check("sms 8 instead of +7", !sent(ChannelType.SMS, "89990000000"));
        check("sms 9 digits", !sent(ChannelType.SMS, "+7999000000"));
        check("sms 11 digits", !sent(ChannelType.SMS, "+799900000000"));
        check("sms letters", !sent(ChannelType.SMS, "+7999000000a"));
    }

    private static void checkInternalAndFactory() {
        check("internal ok", sent(ChannelType.INTERNAL, "u-100"));
        check("internal blank", !sent(ChannelType.INTERNAL, " "));
        ChannelFactory f = new ChannelFactory();
        check("factory classes", f.get(ChannelType.EMAIL) instanceof EmailChannel && f.get(ChannelType.SMS) instanceof SmsChannel);
        check("factory null", !tryFactory(f, null));
        check("empty message", !SERVICE.notify(new NotificationRequest(ChannelType.EMAIL, "user@example.com", " ")).success());
        check("null request", !tryNotify(null));
    }

    // сервис проверяется с каналом-заглушкой, без настоящей отправки
    private static void checkServiceWithFake() {
        FakeChannel fake = new FakeChannel();
        NotificationService s = new NotificationService(new ChannelFactory(Map.of(ChannelType.EMAIL, fake)));
        NotificationResult r = s.notify(new NotificationRequest(ChannelType.EMAIL, "ok", "Meeting moved"));
        check("fake send called", r.success() && fake.sent == 1);
        s.notify(new NotificationRequest(ChannelType.EMAIL, "bad", "Meeting moved"));
        check("fake not called on bad recipient", fake.sent == 1);
        check("fake factory without sms", !tryFactory(new ChannelFactory(Map.of(ChannelType.EMAIL, fake)), ChannelType.SMS));
    }

    private static void checkVariant() {
        // вариант 4: канал PUSH, текст до 25 символов
        check("variant max 25", PushChannel.MAX_LENGTH == 25);
        check("variant factory push", new ChannelFactory().get(ChannelType.PUSH) instanceof PushChannel);
        check("variant push 25 chars", SERVICE.notify(new NotificationRequest(ChannelType.PUSH, "dev-42", "x".repeat(25))).success());
        check("variant push 26 chars", !SERVICE.notify(new NotificationRequest(ChannelType.PUSH, "dev-42", "x".repeat(26))).success());
        check("variant push space", !sent(ChannelType.PUSH, "dev 42"));
        check("variant push blank", !sent(ChannelType.PUSH, ""));
        // старые каналы работают как раньше
        check("variant email unchanged", sent(ChannelType.EMAIL, "user@example.com"));
    }

    // ---------- вспомогательные методы ----------

    private static class FakeChannel implements NotificationChannel {
        int sent = 0;

        public boolean supports(NotificationRequest r) {
            return r.recipient().equals("ok");
        }

        public NotificationResult send(NotificationRequest r) {
            sent++;
            return new NotificationResult(true, "fake");
        }
    }

    private static boolean sent(ChannelType type, String recipient) {
        return SERVICE.notify(new NotificationRequest(type, recipient, "Task updated")).success();
    }

    private static boolean tryFactory(ChannelFactory f, ChannelType t) {
        try {
            f.get(t);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean tryNotify(NotificationRequest r) {
        try {
            SERVICE.notify(r);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static void check(String name, boolean ok) {
        count++;
        if (ok) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            System.out.println("FAIL " + name);
        }
    }

    // ---------- уведомления из файла type;recipient;message ----------

    private static void runFile(String fileName) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать " + fileName);
            return;
        }
        System.out.println();
        System.out.println("Уведомления из файла " + fileName + ":");
        for (int i = 1; i < lines.size(); i++) {   // первая строка - заголовок
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            String[] p = line.split(";", -1);
            if (p.length != 3) {
                System.out.println(line + " -> ошибка: нужно 3 значения через ;");
                continue;
            }
            ChannelType type;
            try {
                type = ChannelType.valueOf(p[0].strip());
            } catch (IllegalArgumentException e) {
                System.out.println(line + " -> ошибка: неизвестный канал " + p[0]);
                continue;
            }
            NotificationResult r = SERVICE.notify(new NotificationRequest(type, p[1].strip(), p[2].strip()));
            System.out.println(line + " -> " + (r.success() ? "отправлено: " : "не отправлено: ") + r.details());
        }
    }
}
