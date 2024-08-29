package com.github.paicoding.forum.core.rabbitmq;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RabbitmqConnectionPool {

    private static BlockingQueue<RabbitmqConnection> pool;

    public static void initRabbitmqConnectionPool(String host, int port, String userName, String password,
                                             String virtualhost,
                                           Integer poolSize) throws InterruptedException, IOException {
        pool = new LinkedBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.add(new RabbitmqConnection(host, port, userName, password, virtualhost));
        }
    }

    public static RabbitmqConnection getConnection() throws InterruptedException {
        return pool.take();
    }

    public static void returnConnection(RabbitmqConnection connection) {
        pool.add(connection);
    }

    public static void close() {
        pool.forEach(RabbitmqConnection::close);
    }
}
