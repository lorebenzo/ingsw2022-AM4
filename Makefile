pre-commit:
			echo '#!/bin/bash' > .git/hooks/pre-commit
			echo 'mvn clean test'  >> .git/hooks/pre-commit
			chmod +x .git/hooks/pre-commit
