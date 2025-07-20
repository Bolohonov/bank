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
- - **CI/CD:** Jenkins
- **Apache Kafka**: Взаимодействие с микросервисом Notifications, Exchange с использованием платформы Apache Kafka
- **Трейсинг запросов**: Система распределённых трассировок Zipkin
- **Cистема сбора и анализа метрик**: система сбора и анализа метрик Prometheus
- **Платформа для визуализации, мониторинга и анализа данных**: Grafana
- **ELK-стек**: Logstash - логирование сообщений в Apache Kafka, Elasticsearch, Использование системы визуализации данных Kibana

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

```bash
git clone https://github.com/bolohonov/bank.git 
```

2. Соберите приложение с помощью Gradle:
   ```bash
   gradle build
   ```
4. Запуск Minikube (если ещё не запущен)
   ```bash
   minikube start
   minikube status
   ```
5. Для Minikube включите Ingress-добавку и увеличте размер памяти
   ```bash
   minikube addons enable ingress
   minikube config view vm-driver
   minikube config get memory
   minikube config set memory 16384
   minikube stop
   minikube start
   ```
6. Minikube / локальный кластер не имеет правильной настройки DNS, поэтому ip указываем в /etc/hosts
   ```bash
   minikube ip
   ```
Формат
<minikube-ip> gateway-ingress.bank.local
- Пример
>192.168.49.2 gateway-ingress.bank.local

Для локального запуска следует добавить DNS имя для Kafka
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
   helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
   helm repo add grafana https://grafana.github.io/helm-charts
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
    minikube service bank-bank-ui-application --url
    
    >http://127.0.0.1:51899
    >! Because you are using a Docker driver on windows, the terminal needs to be open to run it.
    ```

15. После запуска выполним тест helm
    ```bash 
    helm test bank
    ```
# Проброс порта Zipkin
    ```
    kubectl port-forward svc/bank-zipkin 9411:9411
    ```

# Проброс порта Prometheus
    ```
    kubectl port-forward svc/bank-kube-prometheus-prometheus 9090
    ```

# Проброс порта Grafana
    ```
    kubectl port-forward svc/bank-grafana 3000
    ```
Дашборды необходимо импортировать вручную через UI Grafana. Выберите источник данных Prometheus и загрузите JSON-файлы из папки

[Bank_metrics.json](grafana%2Fprovisioning%2Fdashboards%2FBank_metrics.json)
[JVM_metrics_4701_rev10.json](grafana%2Fprovisioning%2Fdashboards%2FJVM_metrics_4701_rev10.json)
[HTTP_metrics_21308_rev1.json](grafana%2Fprovisioning%2Fdashboards%2FHTTP_metrics_21308_rev1.json)

# Kibana
Доступ к приложению с использованием команды minikube для отображения url приложения

    ```bash 
    minikube service bank-kibana --url
    
    >http://127.0.0.1:51899
    >! Because you are using a Docker driver on windows, the terminal needs to be open to run it.
    ```

# Полезные команды для работы с Kubernetes и Helm

## Получение ReplicaSet
```bash
kubectl get replicaset -l app=exchange-generator-service
```

## Вход в контейнер и вывод секрета БД

### Подключение к Pod'у:
```bash
kubectl exec -it bank-db-0 -- bash
```

### Получение пароля из Secret:
```bash
kubectl get secret bank-db -o jsonpath='{.data}'
# Пример вывода: {"postgres-password":"VTdhSXVVN3EyZw=="}
echo "VTdhSXVVN3EyZw==" | base64 --decode
# Расшифрованный пароль: U7aIuU7q2g
```

## Пересоздание PVC при изменении настроек СУБД

### Удаление PVC:
```bash
kubectl get pvc
kubectl delete pvc data-bank-db-0
```

### После удаления — установите проект заново:
```bash
helm install bank ./
```

## Проброс портов

### PostgreSQL:
```bash
kubectl port-forward bank-db-0 5432:5432
```

### Keycloak:
```bash
kubectl port-forward bank-keycloak-0 8080:8080
```

## Получение внешнего IP Ingress

```bash
kubectl get ingress
# Если IP = 127.0.0.1, выполните:
minikube addons enable ingress

kubectl get svc -A | grep ingress
# Пример вывода:
# ingress-nginx   ingress-nginx-controller             LoadBalancer   10.109.152.214   127.0.0.1     80:30813/TCP,443:32321/TCP   10d
> **IP:** `127.0.0.1` или другой указанный.
```

## Обновление/перезапуск приложения через Helm

### Удаление и повторная установка:
```bash
helm uninstall bank
kubectl delete pvc data-bank-postgresql-0
kubectl delete pvc data-bank-kafka-broker-0
kubectl delete pvc data-bank-kafka-controller-0
helm install bank ./
```

### Обновление:
```bash
helm upgrade bank ./
```

### Проверка конфигурации без применения:
```bash
helm install bank ./ --dry-run >1.yaml
```

## Установка curl внутри контейнера и тестирование

### Вход в Pod:
```bash
kubectl exec -it bank-exchange-generator-service-548c6798b7-lllcx -- sh
```

### Установка и использование curl:
```bash
apk add curl
curl -v http://bank-exchange-service.default.svc.cluster.local:80/exchange/rates
curl -v http://gateway-ingress.bank.local/exchangeService/exchange/rates
```

## Проброс сервиса и проверка health-check

```bash
kubectl port-forward svc/bank-exchange-service 8080:80
curl http://localhost:8080/actuator/health
```

## Информационные команды

### Текущие ресурсы:
```bash
kubectl get ingress
kubectl get svc
kubectl get pods
```

### Детализация:
```bash
kubectl describe pod bank-exchange-service-xxx
kubectl describe service bank-exchange-service
kubectl get endpoints bank-exchange-service
kubectl get events -n default
```

## Elasticsearch
### Проверка шаблона после запуска
http://localhost:9200/_index_template/logs_template?pretty

### Проверка созданных индексов в Elasticsearch
http://localhost:9200/_cat/indices?v

### Безопасность

- В проекте реализована система аутентификации и авторизации с использованием Spring Security.
- В качестве сервера авторизации OAuth 2.0 можно использовать Keycloak, любой другой, который можно установить локально, или самописный сервер авторизации OAuth 2.0 с использованием проекта Spring Security OAuth.

- Авторизационный сервер KeyCloak http://localhost:8282/ для обмена с платежным сервисом
  Данные для авторизации
    - KEYCLOAK_ADMIN: admin
    - KEYCLOAK_ADMIN_PASSWORD: adminpassword
    - Настройка клиента KeyCloak: realm-config.json

## CI/CD Jenkins

### 5. Создайте `.env` файл в папке [jenkins](jenkins)
Создайте файл `.env`:

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
DB_PASSWORD=12345
```
> Убедитесь, что GitHub Token имеет права `write:packages`, `read:packages` и `repo`.

### 6. Запустите Jenkins

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
``````