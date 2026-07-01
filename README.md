# Payment Gateway Mock Server

A Spring Boot mock of the **Payment Gateway API** (`payment-gateway-api-openapi.yaml`).
Every endpoint declared in the spec is implemented and returns randomly generated,
schema-shaped data. Values supplied in a request body (amount, currency, email, …)
are echoed back so responses stay internally consistent.

## Tech stack
- Java 21, Spring Boot 3.3.x, Maven
- No database — responses are generated on the fly for each request

## Run

```bash
mvn spring-boot:run
# or
mvn clean package
java -jar target/payment-gateway-mock-2.0.0.jar
```

The server listens on **http://localhost:8080** and serves the API under the
`/v1` context path (matching the spec's URL versioning), e.g.
`http://localhost:8080/v1/payments`.

Change the port in [`application.properties`](src/main/resources/application.properties)
via `server.port`.

## API docs (Swagger UI)

Interactive docs are served by springdoc-openapi and backed by the curated spec
(not auto-generated, since the mock's request bodies are intentionally loose):

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/v1/swagger-ui.html |
| OpenAPI spec file | http://localhost:8080/v1/openapi.yaml |

On a deployed OpenShift route the URLs become
`https://<route-host>/v1/swagger-ui.html` and `https://<route-host>/v1/openapi.yaml`.
The spec includes a relative `/v1` server entry, so **Try it out** in Swagger UI
calls the running mock directly. The served spec lives at
[`src/main/resources/static/openapi.yaml`](src/main/resources/static/openapi.yaml).

## Implemented endpoints

| Group | Endpoints |
|-------|-----------|
| **Payments** | `POST/GET /payments`, `GET /payments/{id}`, `POST /payments/{id}/{capture,void,confirm,cancel}` |
| **Refunds** | `POST/GET /payments/{id}/refunds`, `GET /refunds`, `GET /refunds/{id}` |
| **Customers** | `POST/GET /customers`, `GET/PUT/DELETE /customers/{id}` |
| **Payment Methods** | `POST /payment-methods/{tokenize,validate}`, `POST/GET /customers/{id}/payment-methods`, `GET/PUT/DELETE /customers/{id}/payment-methods/{methodId}`, `PUT .../default` |
| **Invoices** | `POST/GET /invoices`, `GET/PUT/DELETE /invoices/{id}`, `POST /invoices/{id}/{finalize,pay,send,void}` |
| **Balance** | `GET /balance`, `GET /balance/transactions`, `GET /balance/transactions/{id}` |

## Behaviour notes
- **Random data**: each response is freshly generated (ids, amounts, card brands,
  timestamps, etc.). List endpoints honour `limit` and return a random number of items.
- **Echoing**: `POST /payments {"amount":4200,"currency":"EUR","capture_method":"manual"}`
  returns those values with `status: requires_capture`; automatic capture returns
  `status: succeeded`. Similar echoing applies to customers, refunds and invoices.
- **Lenient requests**: request bodies are optional and accepted with or without a
  `Content-Type` header, so any payload (or none) works.
- **Errors**: unknown paths return the spec's `ApiErrorResponse` envelope
  (`NOT_FOUND` → 404, malformed input → 400, unexpected → 500).
- **JSON conventions**: `snake_case` field names, ISO-8601 timestamps, null fields omitted.

## Examples

```bash
# Create a payment (values echoed back)
curl -X POST http://localhost:8080/v1/payments \
  -H "Content-Type: application/json" \
  -d '{"amount":2000,"currency":"USD","customer_id":"cus_123"}'

# Retrieve a payment (id preserved, rest random)
curl http://localhost:8080/v1/payments/pay_abc123

# Current balance
curl http://localhost:8080/v1/balance
```

## Container image

A multi-stage [`Dockerfile`](Dockerfile) builds the jar and packages it on a slim
Red Hat UBI runtime image. It runs as a non-root user and listens on 8080, so it is
compatible with OpenShift's restricted SCC (arbitrary UID, group 0).

```bash
# Local build (Docker or Podman)
docker build -t payment-gateway-mock:2.0.0 .
docker run --rm -p 8080:8080 payment-gateway-mock:2.0.0
```

Health probes (via Spring Boot Actuator), served under the `/v1` context path:
- Liveness:  `/v1/actuator/health/liveness`
- Readiness: `/v1/actuator/health/readiness`

## Deploy to OpenShift

Manifests live in [`openshift/`](openshift/). Two-step flow — build the image in the
cluster, then run it:

```bash
# 1. Log in and select/create a project
oc login <cluster-url>
oc new-project payment-gateway            # or: oc project <existing>

# 2. Create the ImageStream + BuildConfig, then build from local sources
oc apply -f openshift/build.yaml
oc start-build payment-gateway-mock --from-dir=. --follow

# 3. Deploy (Deployment + Service + Route). The image trigger rolls out the
#    freshly built image automatically.
oc apply -f openshift/deployment.yaml

# 4. Get the public URL
oc get route payment-gateway-mock -o jsonpath='https://{.spec.host}/v1/balance{"\n"}'
```

Notes:
- The `Route` terminates TLS at the edge and redirects HTTP → HTTPS.
- To build straight from a Git repo instead of `--from-dir`, swap the `source`
  block in `build.yaml` for a `Git` source (a commented example is included).
- `oc new-app` also works if you prefer a one-liner:
  `oc new-app https://github.com/<org>/<repo>.git --strategy=docker` then `oc expose svc/...`.

## Project layout

```
src/main/java/com/paygateway/mock/
├── PaymentGatewayMockApplication.java   # entry point
├── controller/                          # one controller per resource group + error handler
├── model/                               # POJOs mirroring the OpenAPI schemas
└── support/
    ├── Rand.java                        # random primitive generators
    ├── Req.java                         # lenient request-body parsing/accessors
    └── MockFactory.java                 # assembles models with random + echoed data
```
