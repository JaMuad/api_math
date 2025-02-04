# Math API

A Spring Boot API that classifies numbers and provides interesting mathematical properties and fun facts.

## Example Requests

| Request URL                                      | Response                                                                 |
|--------------------------------------------------|--------------------------------------------------------------------------|
| `/api/classify-number?number=371`                | `{ "number": 371, "is_prime": false, "is_perfect": false, ... }`         |
| `/api/classify-number?number=28`                 | `{ "number": 28, "is_prime": false, "is_perfect": true, ... }`           |
| `/api/classify-number?number=abc`                | `{ "number": "abc", "error": true }`                                     |

## Project Structure

```math_api/
├── src/
│ ├── main/
│ │ ├── java/com/muadmaalim/math_api/
│ │ │ ├── ApiResponse.java
│ │ │ ├── CorsConfig.java
│ │ │ ├── ErrorResponse.java
│ │ │ ├── NumberController.java
│ │ │ ├── NumberService.java
│ │ │ └── MathApiApplication.java
│ │ └── resources/
│ │ ├── application.properties
│ │ └── static/
│ │ └── templates/
│ └── test/
│ └── java/com/muadmaalim/math_api/
│ └── MathApiApplicationTests.java
├── pom.xml
└── README.md
```


## How to Set Up the Project

1. **Download and Install IntelliJ IDEA CE**:
   - Install the latest version of IntelliJ IDEA CE (Community Edition).

2. **Create the Spring Boot Project**:
   - Go to [start.spring.io](https://start.spring.io/).
   - Configure the project:
     - **Project**: Maven
     - **Language**: Java
     - **Spring Boot**: 3.4.2
     - **Group**: `com.muadmaalim`
     - **Artifact**: `math_api`
     - **Description**: Demo Project for Spring Boot
     - **Package Name**: `com.yourDomainName.math_api`
     - **Packaging**: Jar
     - **Java**: 17
     - **Dependencies**: Spring Web, Spring Boot DevTools, Lombok
   - Click **Generate** to download the project.

3. **Open the Project in IntelliJ IDEA**:
   - Extract the downloaded ZIP file.
   - Open IntelliJ IDEA and select **Open**.
   - Navigate to the extracted folder and open it.

4. **Create the Required Java Files**:
   - Inside `src/main/java/com/muadmaalim/math_api`, create the following files:
     - `ApiResponse.java`
     - `CorsConfig.java`
     - `ErrorResponse.java`
     - `NumberController.java`
     - `NumberService.java`
     - `MathApiApplication`


`ApiResponse.java`code: 
```
package com.muadmaalim.math_api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private Integer number;
    private Boolean is_prime;
    private Boolean is_perfect;
    private String[] properties;
    private Integer digit_sum;
    private String fun_fact;
}
```

`CorsConfig.java` code:
```
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET");
    }
}
```

`ErrorResponse.java` code:
```
package com.muadmaalim.math_api;

import lombok.Getter;
import lombok.Setter;

// Error Response DTO (for 400 Bad Request)
@Getter
@Setter
public class ErrorResponse {
    private String number;
    private Boolean error = true;
}
```

`NumberController.java` code:
```
package com.muadmaalim.math_api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // This maps the base path for all endpoints in this controller
public class NumberController {

    private final NumberService numberService;

    public NumberController(NumberService numberService) {
        this.numberService = numberService;
    }

    @GetMapping("/classify-number") // This maps the specific endpoint
    public ResponseEntity<?> classifyNumber(@RequestParam String number) {
        try {
            int num = Integer.parseInt(number);
            ApiResponse response = new ApiResponse();
            response.setNumber(num);
            response.setIs_prime(numberService.isPrime(num));
            response.setIs_perfect(numberService.isPerfect(num));
            response.setProperties(numberService.getProperties(num));
            response.setDigit_sum(String.valueOf(num).chars().map(Character::getNumericValue).sum());
            response.setFun_fact(numberService.getFunFact(num));
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            ErrorResponse error = new ErrorResponse();
            error.setNumber(number);
            return ResponseEntity.badRequest().body(error);
        }
    }
}
```

`NumberService.java` code:
```
package com.muadmaalim.math_api;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class NumberService {

    public boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public boolean isPerfect(int number) {
        if (number <= 1) return false;
        int sum = 1;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                sum += i;
                int otherDivisor = number / i;
                if (otherDivisor != i) sum += otherDivisor;
            }
        }
        return sum == number;
    }

    public boolean isArmstrong(int number) {
        int original = number, digits = String.valueOf(number).length(), sum = 0;
        while (number > 0) {
            int digit = number % 10;
            sum += Math.pow(digit, digits);
            number /= 10;
        }
        return sum == original;
    }

    public String getFunFact(int number) {
        try {
            String url = "http://numbersapi.com/" + number + "/math?json";
            return new RestTemplate().getForObject(url, JsonNode.class).get("text").asText();
        } catch (Exception e) {
            return "No fun fact available.";
        }
    }

    public String[] getProperties(int number) {
        List<String> properties = new ArrayList<>();
        if (isArmstrong(number)) {
            properties.add("armstrong");
        }
        properties.add(getParity(number));
        return properties.toArray(new String[0]);
    }

    private String getParity(int number) {
        return (number % 2 == 0) ? "even" : "odd";
    }
}
```
`MathApiApplication` code:
```
package com.muadmaalim.math_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MathApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MathApiApplication.class, args);
    }
}
```



5. **Fix Lombok Error**:
   - Open `pom.xml` and add the Lombok dependency:
     ```xml
     <dependency>
         <groupId>org.projectlombok</groupId>
         <artifactId>lombok</artifactId>
         <version>1.18.30</version>
         <scope>provided</scope>
     </dependency>
     ```
   - Reload Maven dependencies (`View` > `Tool Windows` > `Maven` > Reload).
   - Install the Lombok plugin in IntelliJ IDEA (`File` > `Settings` > `Plugins`).
   - Enable annotation processing (`File` > `Settings` > `Build, Execution, Deployment` > `Compiler` > `Annotation Processors`).

6. **Run the Application**:
   - Open `MathApiApplication.java` and click the green **Run** button.
   - The application will start on `http://localhost:8080`.

7. **Test the API**:
   - Use Postman to send a GET request to:
     ```
     http://localhost:8080/api/classify-number?number=371
     ```
   - Example response:
     ```
     json
     {
       "number": 371,
       "is_prime": false,
       "is_perfect": false,
       "properties": ["armstrong", "odd"],
       "digit_sum": 11,
       "fun_fact": "371 is an Armstrong number because 3^3 + 7^3 + 1^3 = 371"
     }
     ```

8. **Deploy to AWS EC2**:

1. Launch an EC2 Instance  
- Go to the AWS EC2 console.  
- Click **Launch Instance**.  
- **Choose an Amazon Machine Image (AMI):**  
  - Ubuntu 22.04 LTS or Amazon Linux 2023.  
- **Choose an Instance Type:**  
  - `t2.micro` (Free tier) or `t3.small` (better performance).  
- **Configure Security Group:**  
  - Allow **HTTP (80)**, **HTTPS (443)**, and **SSH (22)**.  
- **Set Authentication:**  
  - Use a **key pair** for SSH access (download the `.pem` file).  
- Click **Launch**.

2. Connect to EC2 via SSH  
```ssh -i your-key.pem ubuntu@your-ec2-public-ip```
For Windows, use PuTTY with the `.pem` file.

3. Update EC2
```
sudo apt update && sudo apt upgrade -y
```
4. Install OpenJDK 17
```
sudo apt install openjdk-17-jdk -y
```
verify the installation `java -version`

Set Java 17 as default: `sudo update-alternatives --config java
`

Verify again: `java -version`

5 Set JAVA_HOME
```
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' | sudo tee -a /etc/environment
echo 'export PATH=$JAVA_HOME/bin:$PATH' | sudo tee -a /etc/environment
source /etc/environment
 ```

Verify: `echo $JAVA_HOME`

Expected output : `/usr/lib/jvm/java-17-openjdk-amd64
`

6 Check if Maven is also installed well and has no error: `mvn -version`

7 Upload Your Porject to EC2
There are multiple ways to upload your project.
If using GitHub, push your project to a repository, then clone it on EC2:
```
git clone <repository-url>
```

8 Build and Run Your Project
```
cd ~/api_math/math_api
mvn clean package
mvn spring-boot:run
```

9 Test Your API
```
curl http://your-ec2-public-ip:8080/api/number?=371
```


9. **Configure Your Domain in Route 53**:

If your domain is example.com, follow these steps:

1 Add an A Record in Route 53
- Go to AWS `Route 53`.
- Select your domain `(example.com)`.
- Click `Create` Record.
  
Set the following:
- Record name: LEAVE IT BLANK
- Record type: A - IPv4 address
- Value: `Your EC2 public IP`
- TTL: `300` (5 minutes)
- ClicK `Create` Record.
Wait a few minutes for the DNS changes to propagate.

Test the Domain : 
```
curl http://example.com:8080/api/number?=371
```
10. **Remove `:8080` Using NGINX Reverse Proxy **:

2 Install Nginx
```
sudo apt update
sudo apt install nginx -y
```

3 Edit Nginx Configuration
```
sudo nano /etc/nginx/sites-available/default
```
Replace everything in the file with: 
```
server {
    listen 80;
    server_name example.com;

    location / {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

4 Restart Nginx 
```
sudo systemctl restart nginx
```

Test Final result : `curl http://example.com/api/number?=371
`

