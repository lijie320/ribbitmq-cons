package cn.et;
import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Log_Conf {
	/**  
     * 获取消息队列名称  
     */  
	private static String EXCHANGE_NAME = "amq-log";  
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
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout",true);  
       // channel.basicQos(1);  
        //产生一个随机的队列 该队列用于从交换器获取消息  
        String queueName = channel.queueDeclare().getQueue();  
        //将队列和某个交换机丙丁 就可以正式获取消息了 routingkey和交换器的一样都设置成空  
        channel.queueBind(queueName, EXCHANGE_NAME, "");  
          
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");  
        //定义回调抓取消息  
        Consumer consumer = new DefaultConsumer(channel) {  
            @Override  
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {  
                String message = new String(body, "UTF-8");  
                System.out.println(message);  
                //参数2 true表示确认该队列所有消息  false只确认当前消息 每个消息都有一个消息标记  
                //channel.basicAck(envelope.getDeliveryTag(), false);  
            }  
        };  
        //参数2 表示手动确认  
        channel.basicConsume(queueName, true, consumer);  
    }  
      
}
