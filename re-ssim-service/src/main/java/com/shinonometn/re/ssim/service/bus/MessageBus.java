package com.shinonometn.re.ssim.service.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MessageBus {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Queue<Message> messageQueue = new LinkedBlockingQueue<>();
    private Map<String, List<Listener>> registeredListeners = new ConcurrentHashMap<>();

    private AtomicBoolean shutdown = new AtomicBoolean(false);

    private BusThread theBusThread = new BusThread();

    public MessageBus(TaskExecutor executor) {
        executor.execute(theBusThread);
    }

    public long getMessageConsumed() {
        return theBusThread.messageCount.get();
    }

    public synchronized void register(Listener listener) {
        if(!registeredListeners.containsKey(listener.getTopic()))
            registeredListeners.put(listener.getTopic(),new Vector<>());

        registeredListeners.get(listener.getTopic()).add(listener);
    }

    public synchronized void remove(Listener listener) {
        if (!registeredListeners.containsKey(listener.getTopic())) return;
        registeredListeners.get(listener.getTopic()).remove(listener);
    }

    public Set<String> topics() {
        return registeredListeners.keySet();
    }

    public void emit(Message message) {
        messageQueue.offer(message);
    }

    public void shutdown() {
        shutdown.set(true);
    }

    private class BusThread implements Runnable {

        AtomicLong messageCount = new AtomicLong(0);

        @Override
        public void run() {
            while (!shutdown.get()) {
                try {
                    Message message = messageQueue.poll();
                    List<Listener> listeners = registeredListeners.keySet()
                            .stream()
                            .filter(s -> s.equals(message.getTopic()) || s.startsWith(message.getTopic()))
                            .map(k -> registeredListeners.get(k))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

                    if (listeners.size() <= 0)
                        logger.warn("Got a message, but no one accept it, it will be drop: {}", message.getTopic());
                    else
                        listeners.stream().parallel().forEach(l -> l.getWorks().accept(message));

                    messageCount.incrementAndGet();
                    Thread.sleep(300);
                } catch (Exception ignore) {

                }
            }
        }
    }

}
