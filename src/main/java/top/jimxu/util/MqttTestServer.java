package top.jimxu.util;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @Author: 徐万金·JimXu    GitHub:52JimXu
 * @Description:
 * @Date: Create in 8:56 2019/12/25
 */
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttTestServer {
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC)String topic);
}