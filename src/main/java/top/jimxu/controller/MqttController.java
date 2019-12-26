package top.jimxu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.jimxu.util.MqttTestServer;

import java.time.LocalDateTime;


/**
 * @Author: 徐万金·JimXu    GitHub:52JimXu
 * @Description:
 * @Date: Create in 8:57 2019/12/25
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {
    @Autowired
    private MqttTestServer mqttTestServer;

    @RequestMapping("/test")
    public String sendmsg(String msg) {
        mqttTestServer.sendToMqtt(msg+LocalDateTime.now(), "1");
        return "发送成功，内容是:"+msg;
    }
}
