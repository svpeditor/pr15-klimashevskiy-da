# Практическая работа №15. Базовые паттерны проектирования на прикладной задаче

Климашевский Даниил Александрович, группа ЭФБО-07-25, вариант 4.

Модуль уведомлений: каналы EMAIL, SMS, INTERNAL и PUSH реализуют интерфейс NotificationChannel и сами проверяют адресата, ChannelFactory выбирает канал, NotificationService получает фабрику через конструктор. По варианту 4 добавлен канал PUSH с текстом не длиннее 25 символов.

## Сборка и запуск

Нужна Java 21 или новее.

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr12.SelfCheck
```

Или скриптом: `./run_selfcheck.sh` (macOS/Linux), `run_selfcheck.bat` (Windows).

Сначала выполняются проверки, потом обрабатывается `data/sample.csv` (формат `type;recipient;message`, первая строка - заголовок).
Свой файл: `java -cp out ru.edu.pr12.SelfCheck data/boundaries.csv`.

Пакет называется `ru.edu.pr12`, как в исходном проекте.

## Что где лежит

- `src/ru/edu/pr12/NotificationChannel.java` - общий интерфейс канала
- `src/ru/edu/pr12/EmailChannel.java` (и SmsChannel.java, InternalChannel.java, PushChannel.java) - каналы
- `src/ru/edu/pr12/ChannelFactory.java` - выбор канала через EnumMap
- `src/ru/edu/pr12/NotificationService.java` - отправка через выбранный канал
- `src/ru/edu/pr12/NotificationRequest.java` (и NotificationResult.java, ChannelType.java) - запрос, результат, типы
- `src/ru/edu/pr12/SelfCheck.java` - проверки, заглушка канала, уведомления из файла
- `data/sample.csv` - открытый набор из задания, `data/boundaries.csv` - мои граничные значения
- `diagrams/` - activity-диаграмма и диаграмма классов (dot и png)
- `DECISIONS.md` - вариант, два решения, граничные проверки, ответы на вопросы задания
- `REPORT.docx` - отчет
