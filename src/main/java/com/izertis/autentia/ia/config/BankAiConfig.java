package com.izertis.autentia.ia.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchConfigurationScript;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.model.openai.OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

@Configuration
public class BankAiConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Bean
    public BankAgent bankAgent(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever) {
        return AiServices.builder(BankAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .contentRetriever(contentRetriever)
                .build();
    }


    @Bean
    EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(TEXT_EMBEDDING_3_SMALL)
                .build();
    }

    @Bean
    public ElasticsearchEmbeddingStore embeddingStore() {

        RestClient client = RestClient.builder(HttpHost.create("http://localhost:9200"))
                .build();
        return ElasticsearchEmbeddingStore.builder()
                .restClient(client)
                .indexName("transactions")
                .configuration(ElasticsearchConfigurationScript.builder().build())
                .build();
    }


    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

        int maxResults = 100;
        double minScore = 0.6;

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
