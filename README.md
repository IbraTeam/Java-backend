Инструкция эксплуатации:
  1. Для начала сбилдить сам проект, переходите в консоли в корень проекта и прописываете ```mvn clean package```
  2. Для запуска compose ```docker-compose up -d```  
  3. Подключение к postgres ```docker exec -it java-backend_postgres_1 psql -U postgres teamwork```
  4. Изменение роли пользователя на админа ```UPDATE users SET role = 'ADMIN' WHERE id = 'id пользователя';```  
  5. Запросы прокидывать на: ```http://localhost:8080```
