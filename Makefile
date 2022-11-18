# defining variables for the make file. Specifying default values if these values are not passed in by the caller
docker_tag ?= latest
java_components := oms api persister
#components := $(java_components) frontend
#components := $(java_components) assetcache
components := $(java_components)

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

mvn-build:
	mvn clean install --file common/pom.xml
	for component in $(java_components); do \
  		mvn clean package --file $$component/pom.xml ; \
  	done

build: mvn-build docker-build

up:
	docker-compose up