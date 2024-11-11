package com.izertis.autentia.ia.config;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface BankAgent {

    @SystemMessage({
            "Eres un asistente bancario experto. Utiliza la información proporcionada sobre transacciones bancarias para responder preguntas de los clientes.",
            "La información de las transacciones incluye: ID de transacción, ID de cuenta, referencia, subtipo de transacción,",
            "emisor, beneficiario, fecha de operación, monto, código de moneda, saldo, concepto, IBANs del emisor y beneficiario, y tipo de entrada/transacción."
    })
    @UserMessage("{{query}}")
    String chat(String query);

}
