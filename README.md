# KumuluzEE Version

[![Build Status](https://img.shields.io/travis/kumuluz/kumuluzee-version/master.svg?style=flat)](https://travis-ci.org/kumuluz/kumuluzee-version)


## Usage

You can enable KumuluzEE Version by adding the following dependency to the project:
``` xml
<dependency>
    <groupId>com.kumuluz.ee.version</groupId>
    <artifactId>kumuluzee-version</artifactId>
    <version>${kumuluzee-version.version}</version>
</dependency>
```

### KumuluzEE Version configuration

KumuluzEE Version will have a default endpoint at /version. You can change this with the KumuluzEE configuration
framework by setting the following key:

```yaml
kumuluzee:
    version:
      endpoint: /myCustomEndpoint
```

KumuluzEE Version will look for values to expose in a json file inside of the META-INF folder,
 the filepath is VERSION.json by default.

The default filepath for this file can be changed with the KumuluzEE configuration framework by setting the following key:

```yaml
kumuluzee:
    version:
      file-path: test/MY_CUSTOM_.json
``` 

Kumuluzee Version also looks for a docker-image-name configuration value, that then gets exposed. This can be set
with the following key:
```yaml
kumuluzee:
    version:
      docker-image-name: myDockerImageName

```

### Example of VERSION.json
 
The following is an example of such a file:

``` json
{
  "maven_group_id": "${project.groupId}",
  "maven_artifact_id": "${project.parent.artifactId}",
  "maven_version": "${project.version}",
  "project_artifactId": "${project.artifactId}",
  "git_commit_id": "${buildNumber}"
}
```

KumuluzEE Version expects the fields to be like ```${...}``` and to get filled by some external plugin. If a field
remains unfilled then a warning gets logged.