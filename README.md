# clone-detector

## Resolving artifact

```groovy
repositories {
    maven { url  "https://dl.bintray.com/accula/clone-detector" }
    maven { url  "https://dl.bintray.com/vorpal-research/kotlin-maven/" }
}
```

```groovy
dependencies {
    implementation "org.accula:clone-detector:$version"
}
```

## Publishing to the artifactory

1. Create gradle.properties at project root directory of the following content
    ```properties
    bintrayUser=<Your Bintray username>
    #Can be found at https://bintray.com/profile/edit > API key
    bintrayApiKey=<Your Bintray API key>
    ```
2. Run `bintrayUpload` Gradle task which will assemble a fat JAR and will upload it to the artifactory
