# Банковское приложение с использованием микросервисной архитектуры

## Описание проекта

Проект представляет приложение "Банк", реализованное с использованием Spring Boot и паттернов микросервисной архитектуры. Реализована функциональность для работы с банковскими счетами, включая регистрацию пользователей, управление счетами, переводы между счетами с конвертацией валют.

### Функциональность
- Регистрация пользователей.
- Добавление счетов в различных валютах.
- Пополнение и снятие виртуальных денег.
- Переводы между счетами одного пользователя или разных пользователей с конвертацией валют.
- Отслеживание курсов валют.
- Уведомления о выполненных операциях.

## Технологии

- **Язык программирования:** Java 21
- **Фреймворк:** Spring Boot
- **База данных:** PostgreSQL
- **Контейнеризация:** Docker
- **Оркестрация контейнеров:** Kubernetes

## Структура микросервисов

1. **Bank UI** — Веб-интерфейс приложения.
2. **Account** — Сервис управления аккаунтами и счетами.
3. **Cash** — Сервис пополнения и снятия денег.
4. **Transfer** — Сервис перевода денег между счетами.
5. **Exchange** — Сервис конвертации валют.
6. **Exchange Generator** — Сервис генерации курсов валют.
7. **Blocker** — Сервис блокировки подозрительных операций.
8. **Notifications** — Сервис уведомлений.

## Запуск приложения

### Предварительные требования
- Установленный Docker и Docker Compose.
- PostgreSQL (если не используется Docker).
- JDK 21 (если собирается проект локально).

## Установка и запуск приложения

1. Клонирование репозитория

2. Соберите приложение с помощью Gradle:
   ```bash
   gradle build
   ```
4. Запуск Minikube (если ещё не запущен)
   ```bash
   minikube start
   minikube status
   ```
5. Активируем Docker-окружение Minikube
   ```bash
   minikube docker-env >addenv.bat
   addenv.bat
   ```
3. Если отсутствует доступ, то необходимо подгрузить
   helm pull oci://registry-1.docker.io/bitnamicharts/keycloak --version 24.7.3
   helm pull oci://registry-1.docker.io/bitnamicharts/postgresql --version 14.2.3

4. Сборка и загрузка Docker-образов в Minikube
   ```bash
   docker build -t exchange-generator-service:0.0.1-SNAPSHOT ./exchangeGeneratorService
   docker build -t notifications-service:0.0.1-SNAPSHOT ./notificationsService
   ```

3) Проверка ingress
   ```bash
   kubectl get pods -n kube-system
   ```
4) Ingress
   ```bash
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.1/deploy/static/provider/cloud/deploy.yaml
   ```
5) Проверка ingress
   ```bash
   kubectl get svc -n ingress-nginx
   ```
6) Обновление helm чартов
   ```bash
   cd bank
   helm dependency update .
    ```
Запуск
helm install bank ./


Шаг 5. Проверьте, что сервис запущен и доступен внутри кластера
kubectl get svc


kubectl get pods

Кол-во реплик
kubectl get replicaset -l app=exchange-generator-service

kubectl exec -it bank-db-0 -- bash
Вывод ключа пароля к БД
kubectl get secret bank-db -o jsonpath='{.data}'
>'{"postgres-password":"VTdhSXVVN3EyZw=="}'
>echo "VTdhSXVVN3EyZw==" | base64 --decode
U7aIuU7q2g

СУБД настройки не применяются без пересоздания PVC
uninstall проекта найдите и удалите PVC:
kubectl get pvc
Затем удалите и затем install заново
kubectl delete pvc data-bank-db-0

kubectl port-forward bank-db-0 5432:5432

kubectl port-forward bank-keycloak-0 8080:8080


6. Запуск сервисов Docker Compose:
   ```bash
   docker-compose up --build -d
   ```
Приложение будет доступно по адресу: http://localhost:8080


## Тестирование

Приложение покрыто юнит- и интеграционными тестами с использованием JUnit 5, TestContext Framework и Spring Boot Test. Для запуска тестов выполните:

   ```bash
   gradle test
   ```

### Безопасность

- В проекте реализована система аутентификации и авторизации с использованием Spring Security.
- В качестве сервера авторизации OAuth 2.0 можно использовать Keycloak с использованием Spring Security OAuth.

- Сервер авторизации KeyCloak http://localhost:8282/ для обмена с платежным сервисом
  Данные для авторизации
    - KEYCLOAK_ADMIN: admin
    - KEYCLOAK_ADMIN_PASSWORD: adminpassword
    - Настройка клиента KeyCloak: realm-config.json
