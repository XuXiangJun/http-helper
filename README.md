# http-helper
A simple kotlin http client based on **java.net.HttpURLConnection**

[![](https://jitpack.io/v/XuXiangJun/http-helper.svg)](https://jitpack.io/#XuXiangJun/http-helper)

```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
```
    dependencies {
            implementation 'com.github.XuXiangJun:http-helper:1.2.0'
    }
```

## Sample

### Create http request
```kotlin
val url = "https://github.com/XuXiangJun"
val request = HttpRequest(url)
// Add query parameters
request.addParameter("tab", "repositories")
// Add http header
request.addHeader("Accept-Charset", "utf-8")
// Set http body
request.body = ByteArray(0)
```

### Execute http request
```kotlin
val response = HttpHelper.request(HttpMethod.GET, request)
val code = response.code
val body = response.body
```
