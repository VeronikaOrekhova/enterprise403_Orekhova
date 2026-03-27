package com.enterprise.CustomerManagement.jms;

import com.enterprise.CustomerManagement.model.eventDto.WelcomeEmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final JmsTemplate jmsTemplate;

    @Value("${app.queue.email}")
    private String emailQueue;

    /**
     * Отправляет сообщение о необходимости отправить приветственное письмо
     * Метод возвращает управление немедленно, реальная отправка происходит асинхронно
     */
    public void sendWelcomeEmail(Long customerId, String email, String firstName) {
        WelcomeEmailMessage message = new WelcomeEmailMessage(customerId, email, firstName);
        log.info("Отправка сообщения в очередь {}: {}", emailQueue, message);

        jmsTemplate.convertAndSend(emailQueue, message,
                postProcessor -> {
                    postProcessor.setStringProperty("messageType", "welcome-email");
                    postProcessor.setLongProperty("customerId", customerId);
                    return postProcessor;
                });
    }
}
