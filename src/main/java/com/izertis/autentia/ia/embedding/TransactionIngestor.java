package com.izertis.autentia.ia.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class TransactionIngestor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionIngestor.class);
    private final EmbeddingModel embeddingModel;
    private final ElasticsearchEmbeddingStore embeddingStore;
    private final Resource transactionsFile;
    private final ObjectMapper objectMapper;

    public TransactionIngestor(EmbeddingModel embeddingModel,
                               ElasticsearchEmbeddingStore embeddingStore,
                               @Value("classpath:data/tx.json") Resource transactionsFile,
                               ObjectMapper objectMapper) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.transactionsFile = transactionsFile;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {

        embeddingStore.removeAll();

        logger.info("::: Loading embedding model ...");

        loadTransactions();

        logger.info("::: Finish embedding store ...");

    }

    public void loadTransactions() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(transactionsFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JsonNode transaction = objectMapper.readTree(line);
                String transactionId = transaction.get("transaction_id").asText();
                String transactionText = createTextFromTransaction(transaction);

                Embedding embedding = embeddingModel.embed(transactionText).content();
                Metadata metadata = new Metadata();
                metadata.put("transaction_id", transactionId);
                metadata.put("transaction_json", transaction.toString());
                embeddingStore.add(embedding, TextSegment.from(transactionText, metadata));
            }

        }
    }

    private String createTextFromTransaction(JsonNode transaction) {

        return String.format(
                "Transacción: %s, Tipo: %s, Emisor: %s, Beneficiario: %s, Monto: %.2f %s, Fecha: %s, Concepto: %s, Comercio: %s, Ciudad: %s",
                transaction.path("transaction_id").asText(),
                transaction.path("transaction_type").asText(),
                transaction.path("issuer").asText(""),
                transaction.path("beneficiary").asText(""),
                transaction.path("amount").asDouble(),
                transaction.path("currency_code").asText(),
                transaction.path("operation_date").asText(),
                transaction.path("reference").asText(""),
                transaction.path("cardAcceptorName").asText(""),
                transaction.path("cardAcceptorAddress").path("city").asText("")
        );
    }
}