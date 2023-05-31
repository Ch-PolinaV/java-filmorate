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
SELECT u.name
FROM users u
LEFT JOIN friends f ON u.user_id = f.friend_id
WHERE f.user_id IN (1, 3)
GROUP BY u.name
HAVING count(friend_id) = 2;
```