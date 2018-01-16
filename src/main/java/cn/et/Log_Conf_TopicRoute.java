package cn.et;
import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Log_Conf_TopicRoute {
	/**  
     * 获取消息队列名称  
     */  
	private static String EXCHANGE_NAME = "student";  
    /**  
     * 异步接收  
     * @throws Exception   
     */
    public static void main(String[] args) throws Exception{  
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("192.168.74.128");  
        Connection connection = factory.newConnection();  
        final Channel channel = connection.createChannel();  
      //消费者也需要定义队列 有可能消费者先于生产者启动   
        channel.exchangeDeclare(EXCHANGE_NAME, "topic",true);  
        channel.basicQos(1);  
        //产生一个随机的队列 该队列用于从交换器获取消息  
        String queueName = channel.queueDeclare().getQueue();  
        //参数设置了多个routingkey 就可以绑定到多个 如果在当前交换机这些routingkey上发消息 都可以接受  
        channel.queueBind(queueName, EXCHANGE_NAME, "1701.*.girl");  
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");  
        //定义回调抓取消息  
        Consumer consumer = new DefaultConsumer(channel) {  
            @Override  
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {  
                String message = new String(body, "UTF-8");  
                //如果是错误消息 将他写入到文件系统中  
                System.out.println(message);  
                //参数2 true表示确认该队列所有消息  false只确认当前消息 每个消息都有一个消息标记  
                channel.basicAck(envelope.getDeliveryTag(), false);  
                  
            }  
        };  
        //参数2 表示手动确认  
        channel.basicConsume(queueName, false, consumer);    
    }  
      
}
