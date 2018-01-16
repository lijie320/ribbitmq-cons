package cn.et;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
@RestController
public class Ribbitmq_conf {
	public static byte[] getByte(Object obj) throws ClassNotFoundException, IOException{
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bas);
		os.writeObject(obj);
		return bas.toByteArray();
	}
	
	public static Object getObject(byte[] bos) throws ClassNotFoundException, IOException{
		ByteArrayInputStream bas = new ByteArrayInputStream(bos);
		ObjectInputStream os = new ObjectInputStream(bas);
		return os.readObject();
	}
	/**  
     * 获取消息队列名称  
     */  
    private final static String QUEUE_NAME = "MAIL_QUEU";  
    /**  
     * 异步接收  
     * @throws Exception   
     */
    @Autowired
   	private JavaMailSender jms;
    @GetMapping("/send")
    public String asyncRec() throws Exception{  
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("192.168.74.128");  
        Connection connection = factory.newConnection();  
        Channel channel = connection.createChannel();  
        //消费者也需要定义队列 有可能消费者先于生产者启动   
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");  
        //定义回调抓取消息  
        Consumer consumer = new DefaultConsumer(channel) {
        	
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {
				try {
					Map map = (Map)Ribbitmq_conf.getObject(body);
					System.out.println(map.get("content"));
					SimpleMailMessage sm = new SimpleMailMessage();
					sm.setFrom("m17666292862@163.com");
					sm.setTo(map.get("mailTo").toString());
					sm.setText(map.get("content").toString());
					sm.setSubject(map.get("subject").toString());
					jms.send(sm);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
            }  
        };  
        channel.basicConsume(QUEUE_NAME, true, consumer);
        return "成功";
    }  
      
}
