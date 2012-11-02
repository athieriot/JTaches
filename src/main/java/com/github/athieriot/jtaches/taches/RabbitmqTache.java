package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.taches.internal.ConfiguredTache;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.info;
import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.getInteger;
import static java.nio.file.Paths.get;

public class RabbitmqTache extends ConfiguredTache {

    public static final String CONFIGURATION_USERNAME = "username";
    public static final String CONFIGURATION_PASSWORD = "password";
    public static final String CONFIGURATION_VIRTUAL_HOST = "virtualHost";
    public static final String CONFIGURATION_HOST = "host";
    public static final String CONFIGURATION_PORT = "port";

    public static final String CONFIGURATION_EXCHANGE_NAME = "exchangeName";
    public static final String CONFIGURATION_CREATE_EVENT_ROUTING_KEY = "createEventRoutingKey";
    public static final String CONFIGURATION_MODIFY_EVENT_ROUTING_KEY = "modifyEventRoutingKey";
    public static final String CONFIGURATION_DELETE_EVENT_ROUTING_KEY = "deleteEventRoutingKey";

    public static final String CONFIGURATION_ABSOLUTE_PATH = "absolutePath";

    private static final String EXCHANGE_TYPE = "direct";

    public RabbitmqTache(Map<String, String> configuration) {
        super(configuration, CONFIGURATION_EXCHANGE_NAME);
    }

    @Override
    public void onCreate(WatchEvent<?> event) {
        send(event);
    }

    @Override
    public void onDelete(WatchEvent<?> event) {
        send(event);
    }

    @Override
    public void onModify(WatchEvent<?> event) {
        send(event);
    }

    private void send(WatchEvent<?> event) {
        try {
            sendEventToExchange(event);
        } catch (IOException e) {
            info("An error occured while sending message to the exchange: " + getConfiguration().get(CONFIGURATION_EXCHANGE_NAME) + " - " + e.getMessage());
        }
    }

    private void sendEventToExchange(WatchEvent<?> watchEvent) throws IOException {
        String file = eventToFilePath(watchEvent);
        String routingKey = eventToRoutingKey(watchEvent);

        sendMessageToExchange(file, routingKey);
    }

    String eventToFilePath(WatchEvent<?> watchEvent) {
        String path = getConfiguration().get(CONFIGURATION_PATH) + "/" + watchEvent.context().toString();

        if(parseBoolean(getConfiguration().get(CONFIGURATION_ABSOLUTE_PATH))) {
            path = get(path).toAbsolutePath().normalize().toString();
        }

        return path;
    }

    String eventToRoutingKey(WatchEvent<?> watchEvent) {
        String routingKey = watchEvent.kind().name();

        switch (routingKey) {
            case "ENTRY_CREATE":
                if(getConfiguration().containsKey(CONFIGURATION_CREATE_EVENT_ROUTING_KEY)) {
                    routingKey = getConfiguration().get(CONFIGURATION_CREATE_EVENT_ROUTING_KEY);
                } break;
            case "ENTRY_DELETE":
                if(getConfiguration().containsKey(CONFIGURATION_DELETE_EVENT_ROUTING_KEY)) {
                    routingKey = getConfiguration().get(CONFIGURATION_DELETE_EVENT_ROUTING_KEY);
                } break;
            case "ENTRY_MODIFY":
                if(getConfiguration().containsKey(CONFIGURATION_MODIFY_EVENT_ROUTING_KEY)) {
                    routingKey = getConfiguration().get(CONFIGURATION_MODIFY_EVENT_ROUTING_KEY);
                } break;
        }

        return routingKey;
    }

    private void sendMessageToExchange(String file, String routingKey) throws IOException {
        Connection connection = null;
        Channel channel = null;

        try {
            connection = createConnection();
            channel = connection.createChannel();

            publish(file, routingKey, channel);
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    //TODO: Trying to test RabbitMQ
    private void publish(String file, String routingKey, Channel channel) throws IOException {
        channel.exchangeDeclare(getConfiguration().get(CONFIGURATION_EXCHANGE_NAME), EXCHANGE_TYPE);

        channel.basicPublish(getConfiguration().get(CONFIGURATION_EXCHANGE_NAME), routingKey, null, file.getBytes());

        info("Event '" + routingKey + "' on file '" + file + "' has been sent to the '" + getConfiguration().get(CONFIGURATION_EXCHANGE_NAME) + "' exchange");
    }

    private Connection createConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        if(getConfiguration().containsKey(CONFIGURATION_USERNAME)) {
            factory.setUsername(getConfiguration().get(CONFIGURATION_USERNAME));
        }
        if(getConfiguration().containsKey(CONFIGURATION_PASSWORD)) {
            factory.setPassword(getConfiguration().get(CONFIGURATION_PASSWORD));
        }
        if(getConfiguration().containsKey(CONFIGURATION_VIRTUAL_HOST)) {
            factory.setVirtualHost(getConfiguration().get(CONFIGURATION_VIRTUAL_HOST));
        }
        if(getConfiguration().containsKey(CONFIGURATION_HOST)) {
            factory.setHost(getConfiguration().get(CONFIGURATION_HOST));
        }
        if(getConfiguration().containsKey(CONFIGURATION_PORT)) {
            factory.setPort(getInteger(getConfiguration().get(CONFIGURATION_PORT)));
        }

        return factory.newConnection();
    }
}
