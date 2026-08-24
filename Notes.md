# Pre Requisite to start this backend

# Qdrant Vector DB Image:

    # docker run -d --name qdrant -p 6333:6333 -p 6334:6334 -e API_KEY="Qdrant_API_KEY" -v qdrant_storage:/qdrant/storage qdrant/qdrant:latest

# Redis DB Image:

    # docker run -d --name my-redis -p 6379:6379 redis:latest

# Asymmetric Key

    # Private Key: openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
    # Public Key: openssl rsa -pubout -in private_key.pem -out public_key.pem

# Streaming Response With Curl

    # $session=New-Object Microsoft.PowerShell.Commands.WebRequestSession; $session.Cookies.Add((New-Object System.Net.Cookie("accessToken","ACCESS_TOKEN","/","localhost"))); Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:8080/api/chat/documents/550c7cc5-e69d-48c7-b683-9b7e644d7058/query/stream" -Method POST -ContentType "application/json" -Headers @{"Accept"="text/event-stream";"X-Conversation-ID"="2ef3b907-3d3a-4620-a9b9-9f2dfd8011b2"} -WebSession $session -Body '{"query":"What is Spring AI?","provider":"NVIDIA"}'

