# DZ3_Velichko


В этом задании программа работает с MySQL:

- **ADD** — создаёт животное и сохраняет его в базу;
- **LIST** — читает полный список из базы;
- **EDIT** — изменяет тип, имя, возраст, вес и цвет выбранного животного;
- **FILTER** — выводит животных выбранного типа;
- **DELETE** — удаляет животное по id;
- **EXIT** — завершает программу.

Таблица `animals` создаётся самой программой при запуске. Отдельно выполнять SQL-скрипт не нужно.


## Подготовка MySQL

Пароль ниже — только для локальной базы этого ДЗ. В `src/main/resources/db.properties` указаны те же значения: пользователь `otus`, пароль `otus`, база `animals_db`.

Подключиться к MySQL под администратором и выполнить:

```sql
CREATE USER IF NOT EXISTS 'otus'@'127.0.0.1' IDENTIFIED BY 'otus';
CREATE USER IF NOT EXISTS 'otus'@'localhost' IDENTIFIED BY 'otus';
GRANT ALL PRIVILEGES ON *.* TO 'otus'@'127.0.0.1';
GRANT ALL PRIVILEGES ON *.* TO 'otus'@'localhost';
FLUSH PRIVILEGES;
```

