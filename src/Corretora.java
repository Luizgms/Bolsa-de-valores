import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Corretora {
    char[] nome;

    private final static String QUEUE_NAME = "BROKER";
    private final static String[] BINDING_KEYS = { "compra.*", "venda.*" };
    private static final String EXCHANGE_NAME = "topic_logs";
    ConnectionFactory factory = new ConnectionFactory();
    
    public char[] getNome() {
        return nome;
    }

    public void setNome(char[] nome) {
        this.nome = nome;
    }

    public Corretora(char[] nome) throws IOException, TimeoutException {
        this.nome = nome;
        Channel channel = createChannel();
        consumeQueue(channel);
    }

    public void compra(String ativo, int quant, double val, char[] corretora) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            String[] body = { "compra", ativo, "" + quant, "" + val, corretora.toString() };
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            channel.basicPublish(EXCHANGE_NAME, "compra." + ativo, null, getMessage(body).getBytes());
            System.out.println(" [x] Sent '" + "compra." + ativo + "':'" + body + "'");
        }
    }

    public void venda(String ativo, int quant, double val, char[] corretora) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            String[] body = { "venda", ativo, "" + quant, "" + val, corretora.toString() };
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            channel.basicPublish(EXCHANGE_NAME, "venda." + ativo, null, getMessage(body).getBytes());
            System.out.println(" [x] Sent '" + "venda." + ativo + "':'" + body + "'");
        }
    }

    public void acompanhar(String ativo, char[] corretora) throws IOException, TimeoutException {
        final String[] BINDING_KEYS = { "compra." + ativo, "venda." + ativo };
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            for (String bindingKey : BINDING_KEYS) {
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, bindingKey);
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        }
    }

    private Channel createChannel() throws IOException, TimeoutException {
        factory.setHost(Env.getHost());
        factory.setUsername(Env.getUservhost());
        factory.setPassword(Env.getPassword());
        factory.setVirtualHost(Env.getUservhost());
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        return channel;
    }

    private void consumeQueue(Channel channel) throws IOException, TimeoutException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        for (String bindingKey : BINDING_KEYS) {
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, bindingKey);
        }

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    private static String getMessage(String[] strings) {
        if (strings.length < 2)
            return "Hello World!";
        return joinStrings(strings, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0)
            return "";
        if (length < startIndex)
            return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
    
}
