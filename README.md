# webchat-example
Super barebones play webchat

## To Run locally

start redis in docker on default port (6379) 

```
$ docker pull redis
$ docker run -p 6379:6379 redis
```

open `application.conf` and change `redis.host` to `localhost`.

run the play app using sbt or intellij or whatever method you prefer.  

open browser windows to localhost:9000, enter some names, chat to yourself and pretend you have friends.
