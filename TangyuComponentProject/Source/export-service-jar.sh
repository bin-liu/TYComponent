rm -rf jars
mkdir jars
# service jar
sed -i '' 's#jar-name=.*#jar-name=tangyu-github-service#g' build.properties
sed -i '' 's#source.path.dir=.*#source.path.dir=/src/com/tangyu/component/service/#g' build.properties
ant startJar packageSrc
mv bin/tangyu-github-* jars/

# view jar
sed -i '' 's#jar-name=.*#jar-name=tangyu-github-view#g' build.properties
sed -i '' 's#source.path.dir=.*#source.path.dir=/src/com/tangyu/component/view/#g' build.properties
ant startJar packageSrc
mv bin/tangyu-github-* jars/

