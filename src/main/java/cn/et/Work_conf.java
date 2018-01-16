package cn.et;
import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Work_conf {
	/**  
     * 获取消息队列名称  
     */  
    private final static String QUEUE_NAME = "WORK_QUEU";  
    /**  
     * 异步接收  
     * @throws Exception   
     */
    public static void main(String[] args) throws Exception{  
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("192.168.74.128");  
        Connection connection = factory.newConnection();  
        Channel channel = connection.createChannel();  
        //消费者也需要定义队列 有可能消费者先于生产者启动   
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);   
        //定义回调抓取消息  
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {
            	System.out.println(new String(body,"UTF-8"));
            }  
        };  
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }  
      
}
