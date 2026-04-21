package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
	    exclude = {
	        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
	        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
	    }
	)
public class ToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolApplication.class, args);
    }
}