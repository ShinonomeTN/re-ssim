package com.shinonometn.re.ssim.service.bus;

public class Message {
    private String topic;
    private Object payload;

    public Message(String topic, Object payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
