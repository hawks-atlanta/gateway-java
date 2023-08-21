#/usr/bin/env bash

case $1 in

	"clang-check")
		echo "Checking formatting..."
		exec clang-format --dry-run -Werror -ferror-limit=1 `find ./app/src/ -type f -regex '.*\.\(java\)'`
		;;

	"clang-format")
		echo "Applying formatting..."
		exec clang-format -i `find ./app/src/ -type f -regex '.*\.\(java\)'`
		;;

	*)
		echo "Not recognized"
		;;
esac

exit 1
