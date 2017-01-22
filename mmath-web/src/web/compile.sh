#!/usr/bin/env bash
echo "Compiling less files"
cd css/
for file in *.less; do lessc --clean-css="--s1 --advanced --compatibility=ie8"  --strict-imports $file `basename $file | sed -e "s/less/css/"` ; done
cd ../

echo "compiling pug files"
pug ./

echo "Compiling jsx"
npm run build