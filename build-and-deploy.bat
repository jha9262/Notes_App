@echo off
echo Building React app...
npm run build

echo Copying React build to Spring Boot...
xcopy /E /Y build\* src\main\resources\static\

echo Building Spring Boot...
mvn clean package

echo Deployment ready! Run: java -jar target\notes-app-1.0.0.jar