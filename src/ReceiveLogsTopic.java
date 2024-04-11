import java.util.Map;

import com.rabbitmq.client.*;

public class ReceiveLogsTopic {

    private static final String EXCHANGE_NAME = "BOLSADEVALORES";
    private static final String QUEUE_NAME = "BROKER";

    public static void main(String[] argv) throws Exception {
        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.get("HOST"));
        factory.setUsername(env.get("USERVHOST"));
        factory.setPassword(env.get("PASSWORD"));
        factory.setVirtualHost(env.get("USERVHOST"));
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        if (argv.length < 1) {
            System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
            System.exit(1);
        }

        for (String bindingKey : argv) {
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, bindingKey);
        }

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}