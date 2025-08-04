#!/bin/bash

GRAFANA_URL=http://localhost:3000
GRAFANA_USER=admin
GRAFANA_PASS=admin
OUTPUT_DIR=./grafana/dashboards

mkdir -p "$OUTPUT_DIR"

# Get all dashboard UIDs
uids=$(curl -s -u "$GRAFANA_USER:$GRAFANA_PASS" "$GRAFANA_URL/api/search?query=" | jq -r '.[] | select(.type=="dash-db") | .uid')

# Export each dashboard
for uid in $uids; do
  dashboard=$(curl -s -u "$GRAFANA_USER:$GRAFANA_PASS" "$GRAFANA_URL/api/dashboards/uid/$uid")
  name=$(echo "$dashboard" | jq -r '.dashboard.title' | tr ' ' '_' | tr '/' '_')
  echo "$dashboard" | jq '.' > "$OUTPUT_DIR/${name}.json"
  echo "✅ Exported: $name"
done
