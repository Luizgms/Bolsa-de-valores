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

    private final static String QUEUE_NAME = "BROKER";
    private static final String EXCHANGE_NAME = "topic_logs";
    private static final int NUM_THREADS = 1;
    private static ConnectionFactory factory = new ConnectionFactory();

    public static void main(String[] args) {
        threadRecv.start();
    }

    static Thread threadRecv = new Thread(new Runnable() {
        public void run() {
            try {
                System.out.println("Inicializando a bolsa...\n");
                initFactory();
                Channel channel = createChannel();
                consumeQueue(channel);
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    });

    private static void initFactory() throws IOException, TimeoutException {
        factory.setHost(Env.getHost());
        factory.setUsername(Env.getUservhost());
        factory.setPassword(Env.getPassword());
        factory.setVirtualHost(Env.getUservhost());
    }

    private static Channel createChannel() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        return channel;
    }

    private static void consumeQueue(Channel channel) throws IOException, TimeoutException {
        String[] BINDING_KEYS = { "compra.*", "venda.*"};

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        for (String bindingKey : BINDING_KEYS) {
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, bindingKey);
        }

        System.out.println(" [*] Bolsa de Valores Waiting for messages in queue BROKER");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("\n [x] Bolsa de Valores Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }



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

                System.out.println(" [*] Bolsa de Valores Waiting for messages in channel BROKER");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

}
