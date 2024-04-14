import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bolsa {

    private final static String QUEUE_RECV = "BROKER";
    private final static String[] BINDING_KEYS = { "compra.*", "venda.*" };
    private static final String EXCHANGE_NAME = "topic_logs";
    private static final int NUM_THREADS = 1;
    ConnectionFactory factory = new ConnectionFactory();

    private ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    public void start() throws IOException, TimeoutException {
        executorService.submit(() -> {
            try {
                initFactory();
            } catch (IOException | TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try (Connection connection = factory.newConnection();
                Channel channel = createChannel();) {
                
                consumeQueue(channel);

                System.out.println(" [*] Waiting for messages in channel BROKER");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    private void initFactory() throws IOException, TimeoutException {
        factory.setHost(Env.getHost());
        factory.setUsername(Env.getUservhost());
        factory.setPassword(Env.getPassword());
        factory.setVirtualHost(Env.getUservhost());
    }

    private Channel createChannel() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_RECV, false, false, false, null);
        return channel;
    }

    private void consumeQueue(Channel channel) throws IOException, TimeoutException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        for (String bindingKey : BINDING_KEYS) {
            channel.queueBind(QUEUE_RECV, EXCHANGE_NAME, bindingKey);
        }

        // Define the DeliverCallback within the method's scope
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //TO-DO: adicionar link para método do livro
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(QUEUE_RECV, true, deliverCallback, consumerTag -> {
        });
    }

}
