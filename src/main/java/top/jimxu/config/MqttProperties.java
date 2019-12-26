package top.jimxu.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
   * @Author: 徐万金·JimXu    GitHub:52JimXu
   * @Description:
   * @Date: Create in 2019/12/24
*/
@Component
@ToString
@Data
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperties {

    private String username;

    private String password;

    private String hostUrl;

    private String clientId;

    private String defaultTopic;

}
