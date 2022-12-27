# defining variables for the make file. Specifying default values if these values are not passed in by the caller
docker_tag ?= latest
java_components := oms api persister
#components := $(java_components) frontend
#components := $(java_components) assetcache
components := $(java_components)

.PHONY: docker-build
docker-build:
	for component in $(components); do \
  		docker buildx build -t quokka/$$component:$(docker_tag) . -f $$component/Dockerfile ; \
  	done && \
  	pushd frontend  && \
	docker buildx build -t quokka/frontend:$(docker_tag) .  && \
	popd && \
  	pushd assetcache  && \
	docker buildx build -t quokka/assetcache:$(docker_tag) .  && \
	popd

.PHONY: mvn-build
mvn-build:
	mvn clean install --file common/pom.xml
	for component in $(java_components); do \
  		mvn clean package -DskipTests --file $$component/pom.xml ; \
  	done

.PHONY: build
build: mvn-build docker-build

.PHONY: up
up:
	docker-compose up -d

.PHONY: logs
logs:
	docker-compose logs -f --since 5s

.PHONY: down
down:
	docker-compose down

.PHONY: frontend-build
frontend-build:
	pushd frontend  && \
	docker buildx build -t quokka/frontend:$(docker_tag) .  && \
	popd

.PHONY: assetcache-build
assetcache-build:
	pushd assetcache  && \
	docker buildx build -t quokka/assetcache:$(docker_tag) .  && \
	popd

.PHONY: oms-build
oms-build:
	docker buildx build -t quokka/oms:$(docker_tag) . -f oms/Dockerfile

.PHONY: api-build
api-build:
	docker buildx build -t quokka/api:$(docker_tag) . -f api/Dockerfile

.PHONY: persister-build
persister-build:
	docker buildx build -t quokka/persister:$(docker_tag) . -f persister/Dockerfile

.PHONY: last-container-logs
last-container-logs:
	docker logs -f $$(docker container ls -n 1 -q)

.PHONY: download-dashboards
download-dashboards:
	./scripts/download_dashboards.sh
