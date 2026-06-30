#!/bin/bash

curl -X POST "http://192.168.1.9:8082/api/documents" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Mon Livre",
    "subtitle": "Sous-titre du livre",
    "documentType": "BOOK",
    "isbn": "978-2-1234-5678-9",
    "publisher": "Éditions Test",
    "publicationYear": 2024,
    "language": "Français",
    "description": "Description du livre de test",
    "items": [
      "Exemplaire 1",
      "Exemplaire 2"
    ]
  }'