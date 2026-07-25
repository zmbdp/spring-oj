rm D:\GitHub\spring-oj\deploy\test\spring-oj-jar\gateway\oj-gateway.jar
rm D:\GitHub\spring-oj\deploy\test\spring-oj-jar\friend\oj-friend.jar
rm D:\GitHub\spring-oj\deploy\test\spring-oj-jar\jop\oj-jop.jar
rm D:\GitHub\spring-oj\deploy\test\spring-oj-jar\judge\oj-judge.jar
rm D:\GitHub\spring-oj\deploy\test\spring-oj-jar\system\oj-system.jar

copy D:\GitHub\spring-oj\oj-gateway\target\oj-gateway-1.0-SNAPSHOT.jar D:\GitHub\spring-oj\deploy\test\spring-oj-jar\gateway\oj-gateway.jar
copy D:\GitHub\spring-oj\oj-modules\oj-judge\target\oj-judge-1.0-SNAPSHOT.jar D:\GitHub\spring-oj\deploy\test\spring-oj-jar\judge\oj-judge.jar
copy D:\GitHub\spring-oj\oj-modules\oj-friend\target\oj-friend-1.0-SNAPSHOT.jar D:\GitHub\spring-oj\deploy\test\spring-oj-jar\friend\oj-friend.jar
copy D:\GitHub\spring-oj\oj-modules\oj-jop\target\oj-jop-1.0-SNAPSHOT.jar D:\GitHub\spring-oj\deploy\test\spring-oj-jar\jop\oj-jop.jar
copy D:\GitHub\spring-oj\oj-modules\oj-system\target\oj-system-1.0-SNAPSHOT.jar D:\GitHub\spring-oj\deploy\test\spring-oj-jar\system\oj-system.jar
pause