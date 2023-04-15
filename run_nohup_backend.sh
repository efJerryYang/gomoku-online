cd gomoku-online-backend/

mvn install
mvn clean package

cd target/
nohup java -jar gomoku-online-*.jar &

