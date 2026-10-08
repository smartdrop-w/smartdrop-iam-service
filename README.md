# smartdrop-iam-service

> **SmartDrop - IoT Liquid Monitoring & Quality Management**  
> *UPC - Fundamentos de Arquitectura de Software (2026-20)*  
> *Autor Responsable:* **Eslander Celis Berrospi**

---

## Descripcion General

Microservicio encargado de la autenticacion de usuarios, generacion y validacion de tokens JWT, gestion de roles de seguridad y administracion de perfiles y preferencias.

---

## Ejecucion en Entorno Local

Para compilar y ejecutar el proyecto localmente sin preconfiguraciones externas:

``powershell
# Compilacion y arranque con Maven Wrapper
./mvnw spring-boot:run
``

## Configuracion de Puertos y Endpoints

* **Puerto Local:** 8081
* **Swagger UI:** [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
* **OpenAPI Especificacion JSON:** [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)
* **Health Check Liveness Probe:** [http://localhost:8081/api/v1/health](http://localhost:8081/api/v1/health)

---

## Pruebas Automatizadas

Para validar la suite de pruebas unitarias y de integracion:

``powershell
./mvnw test
```