package com.base.community.component;

import com.base.community.exception.CustomException;
import com.base.community.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static com.base.community.exception.ErrorCode.NOT_VALID_USER;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider provider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("# stomp header: " + message.getHeaders());

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.CONNECT) {
            if (!provider.validateToken(accessor.getFirstNativeHeader("X-AUTH-TOKEN"))) {
                throw new CustomException(NOT_VALID_USER);
            }
        }

        return message;
    }
}
