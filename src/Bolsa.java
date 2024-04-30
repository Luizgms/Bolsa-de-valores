import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Bolsa {

    private final static String QUEUE_NAME = "BROKER";
    private final static String EXCHANGE_NAME = "broker_bolsa";
    private static ConnectionFactory factory = new ConnectionFactory();
    private static Livro livro = new Livro();

    public static void main(String[] args)  throws IOException {
        try {
            initFactory();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        threadRecv.start();
    }

    static Thread threadRecv = new Thread(new Runnable() {
        public void run() {
            try {
                System.out.println("Inicializando a bolsa...\n");
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
            // Tópico recebido
            String message = new String(delivery.getBody(), "UTF-8");
            String topic = delivery.getEnvelope().getRoutingKey();
            System.out.println("\n [x] Bolsa de Valores Received '" + topic + "':'" + message + "'");
            // Tratamento da mensagem
            String[] strMensagem = new String[5];
            if(delivery.getEnvelope().getRoutingKey().startsWith("venda")){
                strMensagem[0] = "venda";
            } else {
                strMensagem[0] = "compra";
            }
            String[] aux = message.split(" ");
            strMensagem[1] = aux[0];
            strMensagem[2] = aux[1];
            strMensagem[3] = aux[2];
            strMensagem[4] = aux[3];
            // Enviando tópico recebido para o exchange BOLSADEVALORES
            sendMessage(strMensagem);
            // Enviando tópico recebido para o livro de ofertas
            livro.processarOrdem(strMensagem);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    public static void sendMessage(String[] message) {
        try {
            Connection connection = factory.newConnection();
            Channel sendingChannel = connection.createChannel();
            sendingChannel.exchangeDeclare("BOLSADEVALORES", BuiltinExchangeType.TOPIC);
            sendingChannel.basicPublish("BOLSADEVALORES", message[0] + "." + message[1], null, getMessage(message).getBytes());
            System.out.println("\n [x] Sent '" + message[0] + "." + message[1] + "':'" + getMessage(message) + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
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
