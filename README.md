## Rate limiter 

Rate limiter demo based on Spring Boot.


### End point

**Health check**

`GET /diagnostic/status/heartbeat` - returns `OK` if the app is running

**ip based rate limiting**

`GET /ip-based` 
- Returns `OK` 
- Return `429` when the IP is reaching the rate limit.

> The IP check is based on the the `X-Forwarded-For` value in the header first, if it is not present in the header 
> then uses the remote address of the request.


`GET /user-based` 
- Returns `OK` 
- Return `429` when the user is reaching the rate limit.

> The user check is now based on the `userId` field in the request's cookie.
>
> Now the valid user name should start with `airtasker`, for example `airtasker1` 
>
> Invalid user handler has not implemented yet.


### How to run it in Docker

##### Start the process

```
./auto/start
```

> It will use the environment variabled set in `docker-compose` file.
##### Run test 
```
./auto/test
```

### How to run it in local

##### 1. Environment variables

```
export IP_BASED_LIMIT_PER_HOUR=100
export USER_BASED_LIMIT_PER_HOUR=100
```

##### 2. Start the process

```
./gradlew bootRun
```

##### Run test in local
```
./gradlew check
```

### How to build Docker image
```
./auto/build
```

In order to run the application from image, it will need to pass in `IP_BASED_LIMIT_PER_HOUR` and `USER_BASED_LIMIT_PER_HOUR
as environment variables.


### Note
The core implementation of the rate limit is based on the lazy refill rate limiter algorithm. The main code is in `LazyRefillRateLimiter`.
If we want to implement a new RateLimit logic, we can extract the common methods like `allow`, `waitTime` to an interface.

The main implementation is using the spring's interceptor, based on the request path, it will find a matching interceptor for the rate limit check.

There are two interceptors, `IPBasedRateLimiterInterceptor` and `UserBasedRateLimitInterceptor` currently in the code, so basically we can add more 
interceptors based on the requirement.

Inside of the interceptor, each has a `RateLimiter`, inside of `RateLimiter`, it contains a hashmap between the ip/user with the LazyRefillRateLimiter,
and priority queue for the rate limiters, and once start, it will clean up the long living unused ratelimiter in the queue and hashmap.

