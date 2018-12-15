package com.shinonometn.re.ssim.service.bus;

import java.util.function.Consumer;

public class Listener {

    private String topic;
    private Consumer<Message> works;

    public Listener(String topic, Consumer<Message> works) {
        this.topic = topic;
        this.works = works;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Consumer<Message> getWorks() {
        return works;
    }

    public void setWorks(Consumer<Message> works) {
        this.works = works;
    }
}
