init:
			make pre-commit
			# add something to do in the initial stage

pre-commit:
			echo '#!/bin/bash' > .git/hooks/pre-commit
			echo 'make test'  >> .git/hooks/pre-commit

test:
			docker build --tag maven-test --target test .
			docker run maven-test