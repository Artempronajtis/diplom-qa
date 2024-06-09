# Процедура запуска автотестов
## ПО для запуска автотестов
* IntelliJ IDEA Community Edition 2024.1.1
* Java Development Kit (JDK) Eclipse temurin-11.0.21
* Docker Desktop
## Шаги
### Шаг 1: Открытие проекта
1. Откройте скопированный проект в IntelliJ IDEA.
### Шаг 2: Запуск Docker контейнеров
1. В терминале IntelliJ IDEA выполните команду для запуска контейнеров с MySQL, PostgreSQL и эмулятором банковского сервиса:

    docker-compose up
### Шаг 3: Запуск приложения
1. Для тестирования запросов в БД MySQL запустите приложение aqa-shop.jar:

    java -jar ./artifacts/aqa-shop.jar
2. Для тестирования запросов в БД PostgreSQL запустите приложение с указанными параметрами:

    java -jar "C:\Users\Hi-tech\IdeaProjects\diplom-qa\artifacts\aqa-shop.jar" -Dspring.datasource.url=jdbc:postgresql://localhost:5432/db -Dspring.datasource.username=app -Dspring.datasource.password=pass
### Шаг 4: Запуск автотестов
1. Для запуска автотестов с проверкой БД MySQL выполните команду:

    ./gradlew test
2. Для запуска автотестов с проверкой БД PostgreSQL выполните команду:

    ./gradlew test -PdbUrl=jdbc:postgresql://localhost:5432/db -PdbUsername=app -PdbPassword=pass
### Шаг 5: Получение отчета по результатам автотестов
1. Создать отчёт Allure и открыть в браузере с помощью команды в терминале:

    ./gradlew allureServe
### Шаг 6: Завершения работы
1. Для завершения работы allureServe выполнить команду:

    Ctrl+C
2. Для остановки работы контейнеров выполнить команду:

    docker-compose down
