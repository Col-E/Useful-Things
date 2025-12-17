# Using JReleaser

Publishing to maven isn't a hard process, but its tedious to set-up some aspects of it when doing it manually. I use [JReleaser](https://github.com/jreleaser) to offload most of the work and also orchestrate publishing the release to github at the same time. Once its working in one place its more or less a copy paste job to get it working on any other project.

I've fiddled around with using it in a few ways, which include:

- Gradle
    - [`build.gradle` for single-module project](https://github.com/Col-E/javafx-access-agent)
    - [`build.gradle` for single-module project, with additional setup to accommodate for non-standard publish task setup](https://github.com/Col-E/BentoFX)
    - [`jreleaser.yml` for multi-module project, but only releasing a specific sub-module](https://github.com/Col-E/dex-translator)
- Maven
    - [`jreleaser.yml` for single-module project](https://github.com/Col-E/LL-Java-Zip)
    - [`jreleaser.yml` for multi-module project](https://github.com/Col-E/CAFED00D/) _(github release artifacts not attaching for some unknown reason)_
