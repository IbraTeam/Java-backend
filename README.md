Инструкция эксплуатации:
  1. В самом начале нужно поднять бдшки, ```docker-compose up -d postgres redis```
  2. Затем сбилдить сам проект, переходите в консоли в корень проекта и прописываете ```mvn clean package```
  3. Для запуска compose ```docker-compose up -d```  
  4. Подключение к postgres ```docker exec -it root_postgres_1 psql -U postgres teamwork```
  5. Изменение роли пользователя на админа ```UPDATE users SET role = 'ADMIN' WHERE id = 'id пользователя';```  
  6. Запросы прокидывать на: ```http://95.163.229.215:8080```
