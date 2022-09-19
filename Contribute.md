# Developers guide.

If found a bug, please [open an issue](https://github.com/CodecoolGlobal/el-proyecte-grande-sprint-1-java-KisCsaj68/issues/new/choose)
If you like to solve an issue assign the issue to your self and create a PR, and ask a few core devs to review your contribution.

Cover your code with tests.
The CI must be able to pass in order for the PR to be merge-able.


## Versioning
This project follows the [Semantic Version v2](https://semver.org/#semantic-versioning-200)
While you work on your PR, you can use `x.y.z-SNAPSHOT` in the maven projects. 
Before merging, please update your version according to the rules of schemantic versioning:
- Bugfix release increment the patch version
- New backward compatible features increment the minor version and resets lower level versions
- Breaking changes increment the major version and resets lower versions

Please notify developers of components that are depending on your component about the new version, so they can update their compent to use your latest version.

## Initial setup
You will need Postgres and RabbitMQ for local development on your machine. 
If needed please add their env-var configs to the [.envrc](.envrc) file.

RabbitMQ and Postgres should be part of the [docker compose](docker-compose.yaml) file.

## Adding a new component

- Create a subfolder in this repo for the new component
- the new component should provide an easy to use mechanism to start up the component
  - eg.: java components should provide a fat jar (they need to configure their `pom.xml` for it) that can be run like `java -jar my-new-component.jar`
- Make sure that the component is Dockerized by adding a Dockerfile in that folder
  - if the component is written in a compiled language, make sure the Dockerfile utilizes [multi-stage build](https://docs.docker.com/develop/develop-images/multistage-build/#name-your-build-stages)
  - if the component needs startup arguments, use `CMD` with `ENTRYPOINT` directive
    - ```Dockerfile
      # java
      CMD ['-jar', 'my-component.jar']
      ENTRYPOINT ['java']
      ```
    - ```Dockerfile
      # python
      CMD ['my_component.py']
      ENTRYPOINT ['python']
      ```
- Make sure to add the new component to the [docker compose](docker-compose.yaml) file.
- Add e2e tests covering the new component's functionality.