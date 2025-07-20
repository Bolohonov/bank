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
- **Миграции базы данных:** Liquibase
- **Контейнеризация:** Helm + Kubernetes
- **Оркестрация контейнеров:** Docker Compose
- **Архитектура:** Микросервисная
- **Авторизация:** OAuth 2.0 (JWT)
- **Service Discovery и Gateway API:** Kubernetes Ingress
- **Externalized/Distributed Config:** Kubernetes ConfigMaps
- **CI/CD:** Jenkins
- **Apache Kafka**: Взаимодействие с микросервисом Notifications, Exchange с использованием платформы Apache Kafka

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
3. Запуск Minikube (если ещё не запущен)
   ```bash
   minikube start
   minikube status
   ```
4. Для Minikube включите Ingress-добавку и увеличте память
   ```bash
   minikube addons enable ingress
   minikube config view vm-driver
   minikube config get memory
   minikube config set memory 16384
   minikube stop
   minikube start
   ```
5. Minikube / локальный кластер не имеет правильной настройки DNS, поэтому ip указываем в /etc/hosts
   ```bash
   minikube ip
   ```
Формат
<minikube-ip> gateway-ingress.bank.local
- Пример
>192.168.49.2 gateway-ingress.bank.local

6. Для локального запуска следует добавить DNS имя для Kafka
127.0.0.1 bolohonov.kafka.ru


7. Активируем Docker-окружение Minikube
   ```bash
   minikube docker-env >addenv.bat
   addenv.bat
   ```
8. Если нет доступа к bitnamicharts, то тянем к себе вручную
   ```bash
   helm pull oci://registry-1.docker.io/bitnamicharts/keycloak --version 24.7.3
   helm pull oci://registry-1.docker.io/bitnamicharts/postgresql --version 14.2.3
   helm pull oci://registry-1.docker.io/bitnamicharts/kafka --version 32.2.13
   ```
9. Сборка и загрузка Docker-образов в Minikube
   ```bash
   docker images
   minikube image ls   
   docker build -t exchange-generator-service:0.0.1-SNAPSHOT ./exchangeGeneratorService
   docker build -t notifications-service:0.0.1-SNAPSHOT ./notificationsService
   docker build -t blocker-service:0.0.1-SNAPSHOT ./blockerService
   docker build -t exchange-service:0.0.1-SNAPSHOT ./exchangeService
   docker build -t account-service:0.0.1-SNAPSHOT ./accountService
   docker build -t cash-service:0.0.1-SNAPSHOT ./cashService   
   docker build -t transfer-service:0.0.1-SNAPSHOT ./transferService
   docker build -t bank-ui:0.0.1-SNAPSHOT ./bankUI   
   ```

10. Проверка ingress
   ```bash
   kubectl get pods -n kube-system
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.1/deploy/static/provider/cloud/deploy.yaml
   kubectl get svc -n ingress-nginx
   ```

11. Добавление внешних репозитории
   ```bash
   helm repo add elastic https://helm.elastic.co
   helm repo add stable https://charts.helm.sh/stable
   helm repo update
   ```
12. Обновление helm чартов
   ```bash
   cd bank
   helm dependency update .
   helm dependency build
   ```
13. Запуск
   ```bash
   helm install bank ./
   ```
14. Доступ к приложению с использованием команды minikube для отображения url приложения
    ```bash 
    minikube service bank-ui --url
    
    >http://127.0.0.1:51899
    >! Because you are using a Docker driver on windows, the terminal needs to be open to run it.
    ```

15. После запуска выполним тест helm
    ```bash 
    helm test bank
    ```
    
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

## CI/CD Jenkins

### Создайте `.env`:

```env
# Путь до локального kubeconfig-файла
KUBECONFIG_PATH=C:\Users\bolohonov\.kube\config.yaml

# Параметры для GHCR
GITHUB_USERNAME=bolohonov
GITHUB_TOKEN=ghp_{токен}
GHCR_TOKEN=ghp_{токен}

# Docker registry (в данном случае GHCR)
DOCKER_REGISTRY=ghcr.io/your-username
GITHUB_REPOSITORY=bolohonov/bank

# Пароль к базе данных PostgreSQL
DB_PASSWORD=postgres
```
> Убедитесь, что GitHub Token имеет права `write:packages`, `read:packages` и `repo`.

### Запустите Jenkins

```bash
cd jenkins
docker compose up -d --build
```

Jenkins будет доступен по адресу: [http://localhost:8080](http://localhost:8080)


## Завершение работы и очистка

Для остановки Jenkins, удаления namespace, а также всех установленных ресурсов, используйте скрипт `nuke-all.sh`.
Он находится в папке `jenkins`:
```bash
cd jenkins
./nuke-all.sh
```

## Локальный запуск без контейнеризации

Добавляем в hosts
```bash
127.0.0.1 account-service
127.0.0.1 bank-ui
127.0.0.1 blocker-service
127.0.0.1 cash-service
127.0.0.1 exchange-generator-service
127.0.0.1 exchange-service
127.0.0.1 notifications-service
127.0.0.1 transfer-service
```