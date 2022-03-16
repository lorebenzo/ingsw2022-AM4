init:
			mingw32-make pre-commit
			# add something to do in the initial stage

pre-commit:
			echo '#!/bin/sh' > .git/hooks/pre-commit
			echo 'mingw32-make test'  >> .git/hooks/pre-commit

test:
			docker build --tag maven-test --target test .
			docker run maven-test