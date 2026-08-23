package com.pranit.docmind.ai.factory;

import com.pranit.docmind.ai.stratergy.ChatModelStrategy;
import com.pranit.docmind.entities.constant.Provider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public final class ChatModelProviderFactory {

    private final Map<Provider, ChatModelStrategy> strategies;

    public ChatModelProviderFactory(List<ChatModelStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        ChatModelStrategy::getProviderName,
                        strategy -> strategy
                ));
    }

    public ChatModelStrategy getStrategy(Provider provider) {
        return strategies.getOrDefault(provider, strategies.get(Provider.NVIDIA));
    }

}
