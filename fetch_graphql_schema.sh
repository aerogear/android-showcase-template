#!/bin/bash
mkdir -p ./app/src/main/graphql/org/aerogear/mobile/app
apollo-codegen download-schema http://localhost:8080/graphql --output ./app/src/main/graphql/org/aerogear/mobile/app/schema.json

