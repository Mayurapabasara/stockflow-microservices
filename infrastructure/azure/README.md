# Azure deployment direction

This starter does not provision Azure resources. For a production deployment, build and scan each service image, store it in Azure Container Registry, then deploy to Azure Container Apps or AKS. Use managed identity and Azure Key Vault for secrets; do not copy local `.env` values into source control.

Use managed MySQL for service-owned databases and a managed Kafka-compatible event platform (or Azure Event Hubs with the Kafka endpoint) after defining event contracts. Add private networking, TLS, centralized logs/metrics, backups, and infrastructure-as-code before exposing production endpoints.
