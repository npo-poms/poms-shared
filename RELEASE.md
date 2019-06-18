

```bash
mvn release:branch -DbranchName=REL-5.10-SNAPSHOT -DdevelopmentVersion=5.11-SNAPSHOT
git checkout REL-5.10-SNAPSHOT
mvn -Pdeploy,ossrh release:prepare release:perform -DreleaseVersion=5.10.0 -DdevelopmentVersion=5.10.1-SNAPSHOT
```
