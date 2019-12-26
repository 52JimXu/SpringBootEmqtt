package top.jimxu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: 徐万金·JimXu    GitHub:52JimXu
 * @Description:
 * @Date: Create in 16:43 2019/12/24
 */
@SpringBootApplication
@ComponentScan("top.jimxu")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
