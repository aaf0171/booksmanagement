#!/bin/bash

# Configuration de la base de données
DB_HOST="127.0.0.1"
DB_PORT="3306"
DB_USER="bookadmin"
DB_NAME="booksmanagement"
BACKUP_DIR="/run/media/aaf/DATAS/developpement/booksmanagement/docker/backups"

# Génération du nom du fichier avec timestamp
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/backup_schema_${DB_NAME}_${TIMESTAMP}.sql"

# Vérifier que mariadb-dump est disponible
if ! command -v mariadb-dump &> /dev/null; then
    echo "Erreur : mariadb-dump n'est pas installé sur le système."
    exit 1
fi

# Création du backup (structure uniquement)
echo "Début de la sauvegarde de la structure de '${DB_NAME}'..."
mariadb-dump \
    --host="$DB_HOST" \
    --port="$DB_PORT" \
    --user="$DB_USER" \
    --password="cerise" \
    --single-transaction \
    --no-data \
    --skip-comments \
    --routines \
    --triggers \
    --events \
    "$DB_NAME" > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    sed -i -e '/\/\*M!.*/d' -e '/\/\*!40.*/d' -e '/^DROP TABLE/i\\' "$BACKUP_FILE"
    SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    echo "Sauvegarde réussie : ${BACKUP_FILE} (${SIZE})"
else
    echo "Erreur lors de la sauvegarde."
    rm -f "$BACKUP_FILE"
    exit 1
fi
