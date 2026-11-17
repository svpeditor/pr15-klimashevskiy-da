package ru.edu.pr12;

public class EmailChannel implements NotificationChannel {
    // один @, непустое имя, в домене есть точка не на краю
    public boolean supports(NotificationRequest r) {
        String to = r.recipient();
        if (to == null) {
            return false;
        }
        int at = to.indexOf('@');
        if (at <= 0 || at != to.lastIndexOf('@')) {
            return false;
        }
        String domain = to.substring(at + 1);
        int dot = domain.indexOf('.');
        return dot > 0 && dot < domain.length() - 1;
    }

    public NotificationResult send(NotificationRequest r) {
        return new NotificationResult(true, "email отправлен на " + r.recipient());
    }
}
