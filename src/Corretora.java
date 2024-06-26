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
    ConnectionFactory factory = new ConnectionFactory();
    Channel channel = createChannel();
    
    public char[] getNome() {
        return nome;
    }

    public void setNome(char[] nome) {
        this.nome = nome;
    }

    public Corretora(char[] nome) throws IOException, TimeoutException {
        this.nome = nome;
    }

    public void compra(String ativo, int quant, double val, char[] corretora) throws IOException, TimeoutException {
        Thread threadAcompanhar = new Thread(new Runnable() {
            public void run() {
                try {
                    acompanhar(ativo, corretora);
                    try (Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel()) {

                        String[] body = { "compra", ativo, "" + quant, "" + val, String.valueOf(corretora) };
                        channel.exchangeDeclare("exchange_broker_bolsa", BuiltinExchangeType.TOPIC);

                        channel.basicPublish("exchange_broker_bolsa", "compra." + ativo, null, getMessage(body).getBytes());
                        System.out.println("\n [x] Sent '" + "compra." + ativo + "':'" + getMessage(body) + "'");
                    }
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });

        threadAcompanhar.start();
    }

    public void venda(String ativo, int quant, double val, char[] corretora) throws IOException, TimeoutException {
        Thread threadAcompanhar = new Thread(new Runnable() {
            public void run() {
                try {
                    acompanhar(ativo, corretora);
                    try (Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel()) {

                        String[] body = { "venda", ativo, "" + quant, "" + val, String.valueOf(corretora) };
                        channel.exchangeDeclare("exchange_broker_bolsa", BuiltinExchangeType.TOPIC);

                        channel.basicPublish("exchange_broker_bolsa", "venda." + ativo, null, getMessage(body).getBytes());
                        System.out.println("\n [x] Sent '" + "venda." + ativo + "':'" + getMessage(body) + "'");
                    }
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });

        threadAcompanhar.start();
    }

    public void acompanhar(String ativo, char[] corretora) throws IOException, TimeoutException {
        Thread threadAcompanhar = new Thread(new Runnable() {
            public void run() {
                try {
                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();

                    String[] BINDING_KEYS = { "compra." + ativo, "venda." + ativo };

                    channel.exchangeDeclare("BOLSADEVALORES", BuiltinExchangeType.TOPIC);
                    String queueName = channel.queueDeclare().getQueue();

                    for (String bindingKey : BINDING_KEYS) {
                        channel.queueBind(queueName, "BOLSADEVALORES", bindingKey);
                    }

                    System.out.println("\n [*] " + String.valueOf(corretora) + " Waiting for messages in " + ativo + ".");

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        System.out.println("\n [x] Broker Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                    };

                    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                    });
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });

        threadAcompanhar.start();
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
