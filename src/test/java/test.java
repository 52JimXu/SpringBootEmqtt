import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.jimxu.Application;
import top.jimxu.config.MqttConfiguration;
import top.jimxu.config.MqttProperties;

/**
 * @Author: 徐万金·JimXu    GitHub:52JimXu
 * @Description:
 * @Date: Create in 17:54 2019/12/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class test {
    @Autowired
    MqttProperties mqttProperties;
    @Autowired
    MqttConfiguration mqttConfiguration;
    @Test
    public void test(){
        System.err.println(mqttProperties.getHostUrl());
    }
}
