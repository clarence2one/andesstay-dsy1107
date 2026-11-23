# AndesStay — DSY1107 Desarrollo Cloud Native I

Repositorio único de entrega del caso **AndesStay** (EP5 – encargo, 12 % y EP6 – presentación, 18 %).
Contiene el backend completo (8 microservicios Spring Boot), el frontend Angular y la infraestructura
(Docker Compose + despliegue AWS) con la documentación de la presentación.

> **Enlace de entrega:** https://github.com/clarence2one/andesstay-dsy1107

## Estructura

```
ms-andesstay-bff/               API Gateway interno + validación de JWT (puerto 8080)
ms-andesstay-reservations/      Reservas, productor Kafka y productor RabbitMQ
ms-andesstay-catalog/           Catálogo, cupos atómicos e imágenes en S3
ms-andesstay-notify/            Consumidores RabbitMQ + DLQ (email, voucher, housekeeping)
ms-andesstay-audit/             Consumidor de reservations.events, productor de audit.timeline
ms-andesstay-report/            KPIs + consumidor de reservations.events y audit.timeline
ms-andesstay-rabbit-admin/      Administración de colas, exchanges y bindings
ms-andesstay-kafka-admin/       Administración de tópicos, configs, consumer groups y lag
frontend-andesstay/             Angular + MSAL (OAuth2/OIDC con PKCE)
infra/
  db/, mq/, kafka/              Docker Compose local y de clúster
  aws/                          provision.sh, deploy-apps.sh, redeploy.sh, infra-user-data.sh
  docs/matriz-funcionalidades.md
```

Las carpetas se dejan como hermanas en la raíz porque los scripts de `infra/aws/` buscan
`../ms-andesstay-*` e `infra/db/`; no cambiar este layout al clonar.

Cada componente conserva su propio `.gitignore` (Maven en el backend, Angular en el frontend),
por lo que no se suben `target/`, `node_modules/`, `.angular/` ni artefactos de compilación.

## Documentación para la presentación

- [Matriz de funcionalidades](infra/docs/matriz-funcionalidades.md)
- [Instrucciones de infraestructura y verificación del clúster](infra/README.md)
- [README del administrador de Kafka](ms-andesstay-kafka-admin/README.md)

## Puntos clave de la arquitectura

- **Kafka:** 3 Zookeeper + 3 brokers, dos tópicos de negocio (`reservations.events` y
  `audit.timeline`) con 3 particiones y 3 réplicas, `min.insync.replicas=2`, retenciones por flujo
  y `reservations.events.DLT` para mensajes fallidos. Ambos tópicos tienen productor y consumidor.
- **Administración:** `ms-andesstay-kafka-admin` crea, describe, modifica y elimina tópicos, y
  expone offsets/lag por partición. `ms-andesstay-rabbit-admin` hace lo propio con colas/exchanges.
- **RabbitMQ:** 2 nodos en clúster, 3 colas de comandos con reintento y DLQ, exchanges `direct` y `topic`.
- **Seguridad:** Azure AD (IDaaS) con OAuth2/OIDC + PKCE en el frontend; validación de JWT,
  issuer/audiencia y roles en BFF y microservicios; API Gateway de AWS como API Manager.
- **Nube:** EC2 para infraestructura y microservicios, Oracle Cloud DB, S3 y API Gateway.
