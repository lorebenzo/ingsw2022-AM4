init:
		make pre-commit
		make build

build:
		docker build --tag mvn-test --target test .

pre-commit:
		echo '#!/bin/bash' > .git/hooks/pre-commit
		echo 'make test'  >> .git/hooks/pre-commit
test:
		docker run -it --rm --name mvn-test -v "$(pwd)":/usr/src/ing-sw -w /usr/src/ing-sw mvn-test mvn test

up:
		docker-compose up

run-server:
		java -jar ServerEriantys.jar
