package simbot.cycle;

import love.forte.simboot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSimbot
@SpringBootApplication
public class SpringBootWebJavaApplication {

    /**
     * 正常流程启动Spring.
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebJavaApplication.class, args);
    }

}
