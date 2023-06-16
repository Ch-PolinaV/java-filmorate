# Filmorate

Бэкенд для сервиса, который будет возвращать топ фильмов,
рекомендованных к просмотру.

## Возможности приложения:

* создание и обновление пользователей;
* добавление и удаление пользователей из списка друзей;
* добавление и обновление фильмов;
* добавление и удаление лайка фильму;
* получение наиболее популярных фильмов по количеству лайков

## Схема базы данных
![Схема БД](QuickDBD-FreeDiagram.png)

## Примеры запросов:
* Получить список всех пользователей
```sql
SELECT *
FROM user;
```
* Получить названия всех фильмов
```sql
SELECT name
FROM film;
```
* Получить топ-10 фильмов 
```sql
SELECT f.name
FROM film f
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
GROUP BY f.name
ORDER BY COUNT(fl.user_id) DESC
LIMIT 10;
```
* Получить список общих друзей для пользователей: id=1 и id=3
```sql
SELECT USER_ID , EMAIL, LOGIN, NAME, BIRTHDAY
FROM FRIENDS f1
JOIN FRIENDS f2 ON f2.FIRST_USER_ID = 1 AND f2.SECOND_USER_ID = f1.SECOND_USER_ID
JOIN USERS u ON u.USER_ID = f1.SECOND_USER_ID
WHERE f1.FIRST_USER_ID = 2;
```