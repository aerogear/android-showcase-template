# Contributing to the AeroGear Android Showcase Template

The AeroGear Android Showcase Template is part of the [AeroGear project](https://aerogear.org/), see the [Community Page](https://aerogear.org/community) for general guidelines for contributing to the project.

This document details specifics for contributions to the Android Showcase Template.

## Issue tracker

The tracking of issues for the AeroGear Android Showcase Template is done in the [AeroGear Project](https://issues.jboss.org/projects/AEROGEAR/issues) in the [JBoss Developer JIRA](https://issues.jboss.org).

See the [AeroGear JIRA Usage and Guidelines Guide](https://aerogear.org/docs/guides/JIRAUsage/) for information on how the issue tracker relates to contributions to this project.

## Asking for help

Whether you're contributing a new feature or bug fix, or simply submitting a
ticket, the Aerogear team is available for technical advice or feedback. 
You can reach us at [#aerogear](ircs://chat.freenode.net:6697/aerogear) on [Freenode IRC](https://freenode.net/) or the 
[aerogear google group](https://groups.google.com/forum/#!forum/aerogear)
-- both are actively monitored.

# Developing the Android Showcase Template

## Prerequisites

Ensure you have the following installed in your machine:

- [Java Development Kit](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
- [Android Studio and Android SDK](https://developer.android.com/studio/index.html) check [build.gradle](./app/build.gradle) file for the required SDK versions and build tools version
- [Git SCM](http://git-scm.com/)

## Cloning the repository

```bash
git clone git@github.com:aerogear/android-showcase-template.git
cd aerogear-showcase-template/
```

## Installing dependencies and building the ShowCase Template

Open showcase app in Android Studio and select the build variant `release` or `debug` to run the showcase with the latest release.

With the variant selected navigate to `build` on the toolbar in Android Studio and select `Build Project`

## Installing Local Build of SDK

If you are developing the Android SDK itself, it may be helpful to build the showcase using local dependencies.  

### Clone and build the Android SDK

```bash
git clone https://github.com/aerogear/aerogear-android-sdk
cd aerogear-android-sdk
./gradlew install
```

It's possible to verify the install went well by checking the local maven repository:

```bash
ls ~/.m2/repository/org/aerogear
# Should output android-push android-core android-auth android-security 
```

### MavenLocal()

If the build of the SDK is from source, add mavenLocal() to the [build.gradle](https://github.com/aerogear/android-showcase-template/blob/master/build.gradle) file in the root directory of this project.

```groovy
allprojects {
    repositories {
        mavenLocal() // <-- Add This line
        google()
        jcenter()
    }
}
```

### Reference the Dependencies

In the [build.gradle](./app/build.gradle) file in the app directory, add the dependencies provided by the SDK.

```groovy
dependencies {
    ...
    implementation 'org.aerogear:android-push:[version]-SNAPSHOT
    implementation 'org.aerogear:android-auth:[version]-SNAPSHOT
    implementation 'org.aerogear:android-security:[version]-SNAPSHOT
}
```
