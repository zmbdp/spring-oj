rm C:\web\spring-oj\deploy\test\spring-oj-jar\gateway\oj-gateway.jar
rm C:\web\spring-oj\deploy\test\spring-oj-jar\friend\oj-friend.jar
rm C:\web\spring-oj\deploy\test\spring-oj-jar\jop\oj-jop.jar
rm C:\web\spring-oj\deploy\test\spring-oj-jar\judge\oj-judge.jar
rm C:\web\spring-oj\deploy\test\spring-oj-jar\system\oj-system.jar

copy C:\web\spring-oj\oj-gateway\target\oj-gateway-1.0-SNAPSHOT.jar C:\web\spring-oj\deploy\test\spring-oj-jar\gateway\oj-gateway.jar
copy C:\web\spring-oj\oj-modules\oj-judge\target\oj-judge-1.0-SNAPSHOT.jar C:\web\spring-oj\deploy\test\spring-oj-jar\judge\oj-judge.jar
copy C:\web\spring-oj\oj-modules\oj-friend\target\oj-friend-1.0-SNAPSHOT.jar C:\web\spring-oj\deploy\test\spring-oj-jar\friend\oj-friend.jar
copy C:\web\spring-oj\oj-modules\oj-jop\target\oj-jop-1.0-SNAPSHOT.jar C:\web\spring-oj\deploy\test\spring-oj-jar\jop\oj-jop.jar
copy C:\web\spring-oj\oj-modules\oj-system\target\oj-system-1.0-SNAPSHOT.jar C:\web\spring-oj\deploy\test\spring-oj-jar\system\oj-system.jar
pause