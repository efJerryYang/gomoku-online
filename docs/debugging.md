# Debugging

1. Cross Origin Resource Sharing (CORS)
    1. Problem: `Access to XMLHttpRequest at 'http://localhost:8080/api/pick' from origin 'http://localhost:8081' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.`
    2. Solution: add `CorsConfiguration.java` and `SecurityConfiguration.java`. See [this](https://stackoverflow.com/questions/40286549/spring-boot-security-cors) for more details.

2. `java.lang.NoClassDefFoundError: javax/xml/bind/DatatypeConverter`
   1. Problem: when executing `compact()` method of `JwtBuilder`, the error occurs.
   2. Solution: add `jaxb-api` dependency in `pom.xml`. See [this](https://stackoverflow.com/questions/49683488/how-to-fix-exception-in-thread-main-java-lang-noclassdeffounderror-javax-xml) for more details.

