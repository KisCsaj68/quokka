#!/bin/bash

set -o errexit
set -o pipefail

FULLURL="http://admin:admin@localhost:4000"

for dasboard in $(curl -s  "$FULLURL/api/search?query=&" | jq -r '.[] | select(.type == "dash-db") | .uid +";"+ .title '); do
    array=(${dasboard//;/ })
    uid=${array[0]}
    title=${array[1]}
    curl -s "$FULLURL/api/dashboards/uid/${uid}" | jq -r . > $title.json
    slug=$(cat ${title}.json | jq -r '.meta.slug')
    mv $title.json "grafana/${slug}_dashboard_tmp.json"
    cat "grafana/${slug}_dashboard_tmp.json" | jq -r '.dashboard' > "grafana/${slug}_dashboard.json"
    rm "grafana/${slug}_dashboard_tmp.json"
done