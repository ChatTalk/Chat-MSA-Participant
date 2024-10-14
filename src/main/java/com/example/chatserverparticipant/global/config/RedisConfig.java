package com.example.chatserverparticipant.global.config;

import com.example.chatserverparticipant.domain.dto.ChatUserReadDTO;
import com.example.chatserverparticipant.global.redis.RedisMessageListenerService;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@EnableAutoConfiguration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    public final RedisMessageListenerService redisMessageListenerService;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);
        redisConfiguration.setDatabase(0);

        final SocketOptions socketoptions = SocketOptions.builder().connectTimeout(Duration.ofSeconds(10)).build();
        final ClientOptions clientoptions = ClientOptions.builder().socketOptions(socketoptions).build();

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientoptions)
                .commandTimeout(Duration.ofMinutes(1))
                .shutdownTimeout(Duration.ZERO)
                .build();

        return new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
    }

    @Bean(name = "pubSubTemplate")
    public RedisTemplate<String, ChatUserReadDTO> pubSubTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getStringChatUserReadDTOTemplate(redisConnectionFactory);
    }

//    // 채팅창의 접속자 인원 관리(초기 데이터용)
//    @Bean(name = "participatedTemplate")
//    public RedisTemplate<String, Boolean> participatedTemplate(RedisConnectionFactory redisConnectionFactory) {
//        return getStringBooleanTemplate(redisConnectionFactory);
//    }
//
//    private RedisTemplate<String, String> getStringStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//
//        return redisTemplate;
//    }
//
//    private RedisTemplate<String, Boolean> getStringBooleanTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Boolean> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        // 키와 해시 키는 String으로 설정
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//
//        // 해시 값은 Boolean으로 설정
//        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class));
//        // 값을 Boolean으로 설정
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class));
//
//        return redisTemplate;
//    }

    private RedisTemplate<String, ChatUserReadDTO> getStringChatUserReadDTOTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ChatUserReadDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 키와 해시 키는 String으로 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 해시 값은 Boolean으로 설정
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ChatUserReadDTO.class));
        // 값을 Boolean으로 설정
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatUserReadDTO.class));

        return redisTemplate;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(redisMessageListenerService);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisMessageListenerService, new PatternTopic("chat_*"));
        return container;
    }
}