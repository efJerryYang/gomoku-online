# Debugging

1. Cross Origin Resource Sharing (CORS)

   1. Problem: On accessing 'http://localhost:8080/api/pick' from 'http://localhost:8081', the system displays the error message: `Access to XMLHttpRequest at 'http://localhost:8080/api/pick' from origin 'http://localhost:8081' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.`
   2. Solution: Add two files, `CorsConfiguration.java` and `SecurityConfiguration.java`. For more information on this issue and the solution, refer to [this](https://stackoverflow.com/questions/40286549/spring-boot-security-cors).

2. `java.lang.NoClassDefFoundError: javax/xml/bind/DatatypeConverter`

   1. Problem: When executing the `compact()` method of `JwtBuilder`, the error occurs.
   2. Solution: Add the `jaxb-api` dependency in the `pom.xml`. See [this](https://stackoverflow.com/questions/49683488/how-to-fix-exception-in-thread-main-java-lang-noclassdeffounderror-javax-xml) for more details.

3. Host the frontend app to server's port.
   1. The realization didn't come to me until I remembered the Python script I wrote before, which required me to use `0.0.0.0` while starting the server to make it accessible to the public network. I tried it and found it working. The Network address in Vite was no longer in an unset state. However, it only had the internal network IP, and the public network couldn't access it. The communication between the frontend and backend was still not possible due to cross-origin request issues
   2. It seems like the previous CORS configuration had some problems. But, I do not want to make any changes to it, and I'm just being lazy. In case you're interested, you can refer to [this](https://blog.51cto.com/u_15080000/2593677?articleABtest=0).
