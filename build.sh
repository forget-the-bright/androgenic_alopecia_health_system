   git pull --rebase
   docker rm -f hair-loss
   docker rmi hair-loss:latest
   mvn clean package  -Dfile.encoding=UTF-8 -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -U -Dmaven.test.skip=true
   docker build -t hair-loss .
   docker run -d -p 801:80 --name hair-loss hair-loss
   docker rmi kskswanghao/hair-loss:latest
   docker tag hair-loss kskswanghao/hair-loss:latest
   docker push kskswanghao/hair-loss:latest