## Rag sobre Elasticsearch

- Chat interactivo sobre las transacciones de un usuario. Se podrán realizar preguntas sobre las transacciones y el `Agente Bancario`
te contestará

- Las transacciones se encuentran en un fichero tx.json, cada linea representa una transacción en formato json

- Al arrancar la aplicación se lee este fichero y por cada linea (transacción) se construye un resumen con los datos más relevantes, se genera un 'embedding' (un vector con su representación)
y se inserta en Elasticsearch

```json
{
  "vector": [
    -0.011392268,
    -0.01085579,
    ....
    -0.025077717,
    0.019513072,
    0.013001702
  ],
  "text": "Transacción: 0e86dbc4-c86b-11e8-a8d5-f2801f1b9fd1, Tipo: TRANSFER, Emisor: María Núñez García, Beneficiario: Irene Sanz Castro, Monto: 745.00 EUR, Fecha: 2018-10-05T02:36:24.803, Concepto: Alquiler Piso, Comercio: , Ciudad: ",
  "metadata": {
    "transaction_id": "0e86dbc4-c86b-11e8-a8d5-f2801f1b9fd1",
    "transaction_json": "{\"transaction_id\":\"0e86dbc4-c86b-11e8-a8d5-f2801f1b9fd1\",\"account_id\":\"756e0d28-333f-453a-99a1-394e8a332d74\",\"reference\":\"Alquiler Piso\",\"transaction_subtype\":\"TRANSFER_ISSUED\",\"issuer\":\"María Núñez García\",\"beneficiary\":\"Irene Sanz Castro\",\"operation_date\":\"2018-10-05T02:36:24.803\",\"amount\":745.0,\"currency_code\":\"EUR\",\"entry_type\":\"DEBIT\",\"transaction_type\":\"TRANSFER\"}"
  }
}

```

- Por cada consulta que realiza el usuario se genera un 'embedding' con el se realiza la consulta a Elasticsearch y con estos resultados se realiza
la consulta al LLM.

## Como arrancar la aplicación

```
mvn spring-boot:run -Dspring-boot.run.arguments="--open-ai.secret.key=my api key"
```

## Juego de datos

| transaction_id | account_id | reference | transaction_subtype | issuer | beneficiary | operation_date | amount | currency_code | entry_type | transaction_type | customerId | cardAcceptorName | category | creditor | debtor |
|----------------|------------|-----------|---------------------|--------|-------------|----------------|--------|---------------|------------|------------------|------------|------------------|----------|----------|--------|
| 7a62f42b-bd74-4909-a1e6-e23d6d5326f9 | 756e0d28-333f-453a-99a1-394e8a332d74 | Deudas pendientes | TRANSFER_RECEIVED | Juan García Santos | María Núñez García | 2018-09-06T11:11:11.803 | 2000.000 | EUR | CREDIT | TRANSFER | | | | | |
| bfba0900-8e59-44b8-9f15-333bacbde539 | 756e0d28-333f-453a-99a1-394e8a332d74 | Gasolina viaje | TRANSFER_ISSUED | María Núñez García | Irene Sanz Castro | 2018-09-15T10:36:24.803 | 45.000 | EUR | DEBIT | TRANSFER | | | | | |
| fb15c8cf-16dc-4efe-acae-f36de958c510 | 756e0d28-333f-453a-99a1-394e8a332d74 | Apartamento vacaciones | TRANSFER_ISSUED | María Núñez García | Irene Sanz Castro | 2018-09-20T12:40:20.803 | 122.850 | EUR | DEBIT | TRANSFER | | | | | |
| eee50273-7fda-4032-9555-761e06be6fd9 | 756e0d28-333f-453a-99a1-394e8a332d74 | Parking | TRANSFER_ISSUED | María Núñez García | Irene Sanz Castro | 2018-09-23T10:36:24.803 | 120.000 | EUR | DEBIT | TRANSFER | | | | | |
| f3920319-a9b0-4fb4-bc61-11b68aa80922 | 756e0d28-333f-453a-99a1-394e8a332d74 | Comida playa | TRANSFER_RECEIVED | Irene Sanz Castro | María Núñez García | 2018-09-21T10:36:24.803 | 33.800 | EUR | CREDIT | TRANSFER | | | | | |
| 7b4efebe-c86b-11e8-a8d5-f2801f1b9fd1 | 756e0d28-333f-453a-99a1-394e8a332d74 | Cena de empresa | TRANSFER_ISSUED | María Núñez García | Irene Sanz Castro | 2018-10-02T20:36:24.803 | 10.000 | EUR | DEBIT | TRANSFER | | | | | |
| 9e9b13de-ec90-49f6-8f5d-2e34594058cf | 756e0d28-333f-453a-99a1-394e8a332d74 | Parking | TRANSFER_RECEIVED | Irene Sanz Castro | María Núñez García | 2018-10-02T10:36:24.803 | 200.250 | EUR | CREDIT | TRANSFER | | | | | |
| 58ad5472-c86c-11e8-a8d5-f2801f1b9fd1 | 756e0d28-333f-453a-99a1-394e8a332d74 | Desayuno Finca | TRANSFER_RECEIVED | Irene Sanz Castro | María Núñez García | 2018-10-03T15:06:24.803 | 12.50 | EUR | CREDIT | TRANSFER | | | | | |
| 0e86dbc4-c86b-11e8-a8d5-f2801f1b9fd1 | 756e0d28-333f-453a-99a1-394e8a332d74 | Alquiler Piso | TRANSFER_ISSUED | María Núñez García | Irene Sanz Castro | 2018-10-05T02:36:24.803 | 745.000 | EUR | DEBIT | TRANSFER | | | | | |
| d00d41f9-2653-4ce6-adbb-337e1b2039c4 | 5452c69d-2cca-40ca-8481-d5ba40e9e210 | | CARD_PAYMENT | | | 2020-01-09T10:56:15.697828Z | 10.99 | EUR | DEBIT | CARD | 5f156b42-410c-4d79-bfe8-1aa64dba1fc9 | STRADIVARIUSS Las Rozas | shop | | |
| e5348200-d268-4a40-88c1-da8a6b69972c | 5452c69d-2cca-40ca-8481-d5ba40e9e210 | | CARD_PAYMENT | | | 2020-02-09T10:56:15.697828Z | 12.99 | EUR | DEBIT | CARD | 5f156b42-410c-4d79-bfe8-1aa64dba1fc9 | STRADIVARIUSS Alacant | shop | | |
| 30b4dfa0-4e5c-4958-991c-1ca7a776ffb6 | 5452c69d-2cca-40ca-8481-d5ba40e9e210 | | CARD_PAYMENT | | | 2020-03-09T10:56:15.697828Z | 50.99 | EUR | DEBIT | CARD | 5f156b42-410c-4d79-bfe8-1aa64dba1fc9 | Nike Factory | shop | | |
| d04c8b47-43d9-4665-8886-c237acbc03d0 | 5452c69d-2cca-40ca-8481-d5ba40e9e210 | | CARD_PAYMENT | | | 2020-03-09T10:56:15.697828Z | 8.50 | EUR | DEBIT | CARD | 5f156b42-410c-4d79-bfe8-1aa64dba1fc9 | MCDONALDS LA PAZ | amenity | | |
| f48dbc6f-a952-4b3e-ab70-f286d35f6637 | ed25fc9a-338c-4988-aba9-e9d983f4eb2b | Recibo de la luz Febrero | DIRECT_DEBIT_DEBITED | | | 2020-02-09T08:39:47Z | 24.30 | EUR | DEBIT | DIRECT_DEBIT | | | Suministros | Iberdrola S.A | Jorge Perez |
| 121107a8-730f-42b1-8a45-17ebabbcf1bd | ed25fc9a-338c-4988-aba9-e9d983f4eb2b | Recibo Movistar + Febrero | DIRECT_DEBIT_DEBITED | | | 2020-09-19T08:39:47Z | 15.80 | EUR | DEBIT | DIRECT_DEBIT | | | Entretenimiento | Telefonica S.A | Jorge Perez |
| 76fc0acc-cb97-4f55-9e34-9f0b39134231 | ed25fc9a-338c-4988-aba9-e9d983f4eb2b | Netflix febrero | DIRECT_DEBIT_DEBITED | | | 2020-09-19T08:39:47Z | 12.38 | EUR | DEBIT | DIRECT_DEBIT | | | Entretenimiento | Netflix S.A | Jorge Perez |
| 1a69c8d0-5a51-4704-9e32-4e4d40fa1595 | ed25fc9a-338c-4988-aba9-e9d983f4eb2b | Steam Marzo | DIRECT_DEBIT_DEBITED | | | 2020-09-19T08:39:47Z | 5.28 | EUR | DEBIT | DIRECT_DEBIT | | | Entretenimiento | Steam S.A | Jorge Perez |
| 2fc57a95-94fb-426b-9dd2-b4849e0cd489 | ed25fc9a-338c-4988-aba9-e9d983f4eb2b | Recibo del agua Junio | DIRECT_DEBIT_DEBITED | | | 2021-01-19T08:39:47Z | 7.23 | EUR | DEBIT | DIRECT_DEBIT | | | Suministros | Canal de Isabel II | Jorge Perez |