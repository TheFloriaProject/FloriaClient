#!/usr/bin/env bash
#
# Copyright (c) 2026 lyranie
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <https://www.gnu.org/licenses/>.
#

set -euo pipefail

LAST_CHANGELOG_COMMIT=$(git log --follow -n 1 --skip 1 --pretty=format:"%H" -- CHANGELOG.md)

if [ -z "$LAST_CHANGELOG_COMMIT" ]; then
    echo "No previous changelog commit found, using full file"
    cp CHANGELOG.md release_notes.txt
elif ! git diff --quiet "$LAST_CHANGELOG_COMMIT" HEAD -- CHANGELOG.md; then
    git diff "$LAST_CHANGELOG_COMMIT" HEAD -- CHANGELOG.md \
        | grep '^+' \
        | grep -v '^+++' \
        | grep -v '^+#' \
        | sed 's/^+//' \
        > release_notes.txt || true
else
    : > release_notes.txt
fi

echo "Release notes:"
cat release_notes.txt
