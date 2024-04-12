import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Bolsa {

    private final static String QUEUE_RECV = "BROKER";
    private final static String QUEUE_SEND = "BOLSADEVALORES";
    private final static String[] BINDING_KEYS = {"compra.*","venda.*"};
    private static final String EXCHANGE_NAME = "topic_logs";
    Channel channel;
    ConnectionFactory factory = new ConnectionFactory();

    Bolsa() throws IOException, TimeoutException {
        createRecvChannel();
        consumeQueue();
    }

    private void createRecvChannel() throws IOException, TimeoutException {
        Map<String, String> env = System.getenv();
        factory.setHost(env.get("HOST"));
        factory.setUsername(env.get("USERVHOST"));
        factory.setPassword(env.get("PASSWORD"));
        factory.setVirtualHost(env.get("USERVHOST"));
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_RECV, false, false, false, null);
    }

    private void consumeQueue() throws IOException, TimeoutException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        for (String bindingKey : BINDING_KEYS) {
            channel.queueBind(QUEUE_RECV, EXCHANGE_NAME, bindingKey);
        }

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(QUEUE_RECV, true, deliverCallback, consumerTag -> { });
    }

    public void armazena(String tipo, String ativo, int quant, double val, char[] corretora) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_SEND, false, false, false, null);

            String[] body = {tipo,ativo,""+quant,""+val,corretora.toString()};
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            channel.basicPublish(EXCHANGE_NAME, tipo+"."+ativo, null, getMessage(body).getBytes());
            System.out.println(" [x] Sent '" + tipo+"."+ativo + "':'" + body + "'");
        }
    }

    private static String getMessage(String[] strings) {
        if (strings.length < 2)
            return "Hello World!";
        return joinStrings(strings, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0) return "";
        if (length < startIndex) return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
    
}
