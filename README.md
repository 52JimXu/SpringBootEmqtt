# Emqtt的使用
今天我来说说emqtt吧，因为公司要用这技术，我也就得学习一下咯，我将用SpringBoot来发消息，JavaScript来接收消息，当然JavaScript不是我写的，网上找的一个

首先，要有一个emqtt的服务吧，来嘛，我提供一个win10-64位下载地址:[emqtt](https://v7.jimxu.top/other/emqttd.zip)
其他的版本自己找咯！！！

不废话，直接上代码，首先是pom.xml
```xml
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-integration</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.integration</groupId>
            <artifactId>spring-integration-stream</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.integration</groupId>
            <artifactId>spring-integration-mqtt</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.4</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

前三个是必须要用到的，第四个是lombok，不清楚的可以去百度一下，他可以简化实体类的代码，其他的没什么说的
然后来看看application.properties
```properties
#MQTT配置信息
#MQTT-用户名
spring.mqtt.username=admin
#MQTT-密码
spring.mqtt.password=password
#MQTT-服务器连接地址，如果有多个，用逗号隔开，如：tcp://127.0.0.1:61613，tcp://192.168.0.102:61613
spring.mqtt.host-url=tcp://127.0.0.1:1883
#MQTT-连接服务器默认客户端ID
spring.mqtt.client-id=mqttId
#MQTT-默认的消息推送主题，实际可在调用接口时指定
spring.mqtt.default-topic=topic

server.port=8081
```
然后把mqtt配置注入到MqttProperties.java里面
```java
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
```
然后就是mqtt的配置类了
```java
@Configuration
public class MqttConfiguration {

    @Autowired
    private MqttProperties mqttProperties;

    @Bean
    public MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setUserName(mqttProperties.getUsername());
        mqttConnectOptions.setPassword(mqttProperties.getPassword().toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{mqttProperties.getHostUrl()});
        mqttConnectOptions.setKeepAliveInterval(2);
        return mqttConnectOptions;
    }
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =  new MqttPahoMessageHandler(mqttProperties.getClientId(), mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(mqttProperties.getDefaultTopic());
        return messageHandler;
    }
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

}
```
emqtt发送消息类
```java
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttTestServer {
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC)String topic);
}
```
好了，可以开始使用了，方便一点，我们用controller测试吧
```java
@RestController
@RequestMapping("/mqtt")
public class MqttController {
    @Autowired
    private MqttTestServer mqttTestServer;

    @RequestMapping("/test")
    public String sendmsg(String msg) {
    	//这个1是topic，接收数据会用到
        mqttTestServer.sendToMqtt(msg+LocalDateTime.now(), "1");
        return "发送成功，内容是:"+msg;
    }
}
```
好了，启动emqtt,在emqtt的bin目录下打开cmd,输入emqttd start
![](https://v7.jimxu.top/images/1577328075172.jpg)
访问127.0.0.1:18083,用户名admn，密码public，会发现没有一个连接
![](https://v7.jimxu.top/images/1577328103045.png)

那么我们启动application.java
```java
@SpringBootApplication
@ComponentScan("top.jimxu")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
```
访问localhost:8081/mqtt/test?msg=你想发送的内容
会看到
![](https://v7.jimxu.top/images/1577328151136.png)
然后再看emqtt控制台的连接，发现就多了一个
![](https://v7.jimxu.top/images/1577328166095.png)
然后我们用js来获取数据，我在网上找的一个，看代码
```html
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8" />
    <title></title>
    <!-- <script src=“https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.js” type=“text/javascript”> </script> -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.min.js" type="text/javascript"></script>

    <script>
        var hostname = '127.0.0.1', //'192.168.1.2',
            port = 8083,
            clientId = 'client-mao2080',
            timeout = 5,
            keepAlive = 100,
            cleanSession = false,
            ssl = false,
             userName = 'admin',  
             password = 'public',  
             //这里这个topic要与java那里设置的topic一样才可以拿到数据哦
            topic = '1';
        client = new Paho.MQTT.Client(hostname, port, clientId);
        //建立客户端实例  
        var options = {
            invocationContext: {
                host: hostname,
                port: port,
                path: client.path,
                clientId: clientId
            },
            timeout: timeout,
            keepAliveInterval: keepAlive,
            cleanSession: cleanSession,
            useSSL: ssl,
            // userName: userName,  
            // password: password,  
            onSuccess: onConnect,
            onFailure: function (e) {
                console.log(e);
                s = "{time:" + new Date().Format("yyyy-MM-dd hh:mm:ss") + ", onFailure()}";
                console.log(s);
            }
        };
        client.connect(options);
        //连接服务器并注册连接成功处理事件  
        function onConnect() {
            console.log("onConnected");
            s = "{time:" + new Date().Format("yyyy-MM-dd hh:mm:ss") + ", onConnected()}";
            console.log(s);
            client.subscribe(topic);
        }

        client.onConnectionLost = onConnectionLost;

        //注册连接断开处理事件  
        client.onMessageArrived = onMessageArrived;

        //注册消息接收处理事件  
        function onConnectionLost(responseObject) {
            console.log(responseObject);
            s = "{time:" + new Date().Format("yyyy-MM-dd hh:mm:ss") + ", onConnectionLost()}";
            console.log(s);
            if (responseObject.errorCode !== 0) {
                console.log("onConnectionLost:" + responseObject.errorMessage);
                console.log("连接已断开");
            }
        }

        function onMessageArrived(message) {
            s = "{time:" + new Date().Format("yyyy-MM-dd hh:mm:ss") + ", onMessageArrived()}";
            console.log(s);
            console.log("收到消息:" + message.payloadString);
        }

        function send() {
            var s = document.getElementById("msg").value;
            if (s) {
                s = "{time:" + new Date().Format("yyyy-MM-dd hh:mm:ss") + ", content:" + (s) + ", from: web console}";
                message = new Paho.MQTT.Message(s);
                message.destinationName = topic;
                client.send(message);
                document.getElementById("msg").value = "";
            }
        }

        var count = 0;

        function start() {
            window.tester = window.setInterval(function () {
                if (client.isConnected) {
                    var s = "{time:" + new Date().Format("yyyy-MM-dd hh:mm:ss") + ", content:" + (count++) +
                        ", from: web console}";
                    message = new Paho.MQTT.Message(s);
                    message.destinationName = topic;
                    client.send(message);
                }
            }, 1000);
        }

        function stop() {
            window.clearInterval(window.tester);
        }

        Date.prototype.Format = function (fmt) { //author: meizz 
            var o = {
                "M+": this.getMonth() + 1, //月份 
                "d+": this.getDate(), //日 
                "h+": this.getHours(), //小时 
                "m+": this.getMinutes(), //分 
                "s+": this.getSeconds(), //秒 
                "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
                "S": this.getMilliseconds() //毫秒 
            };
            if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[
                    k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
        }
    </script>
</head>

<body>
    <input type="text" id="msg" />
    <input type="button" value="Send" onclick="send()" />
    <input type="button" value="Start" onclick="start()" />
    <input type="button" value="Stop" onclick="stop()" />
</body>

</html>
```
打开html，打开console，刷新localhost:8081/mqtt/test?msg=你想发送的内容
![](https://v7.jimxu.top/images/1577328196756.gif)

测试完成，其他功能你们自己琢磨。。
