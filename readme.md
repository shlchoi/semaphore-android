# Semaphore - Android

[![CircleCI](https://circleci.com/gh/shlchoi/semaphore-android.svg?style=svg&circle-token=cbed7a3c4f3cdd5a531b9cfe97c04a9fe6c26f24)](https://circleci.com/gh/shlchoi/semaphore-android)

Semaphore is a system that monitors physical mailboxes for deliveries and notifies users when mail arrives. In addition, Semaphore also categorizes and counts the number of items that are currently inside the mailbox and displays the information to the user in an associated smartphone application.  

This repository contains the Android Smartphone Component.

Semaphore was created for the Electrical and Computer Engineering Capstone Design Symposium 2017 at the University of Waterloo.


## About Semaphore
See the [main project page](https://shlchoi.github.io/semaphore) for more information.

### Other Semaphore Repositories
* [Mailbox Device](https://github.com/shlchoi/semaphore-mailbox)
* [Web Server](https://github.com/shlchoi/semaphore-server)
* [Image Processing Algorithm](https://github.com/mattcwc/semaphore-algorithm)
* [iOS Application](https://github.com/shlchoi/semaphore-ios)

## Setup
1. Clone project using `git clone git@github.com:shlchoi/semaphore-android.git`
2. In the [Firebase Console](https://console.firebase.google.com), create a new project
3. Add a mobile Android app for the debug build variant using the package name `ca.semaphore.app.debug`
4. Add another mobile Android app for the release build variant using the package name `ca.semaphore.app`
5. Download the generated `google-services.json` file, and put it into the `app/` folder
6. Open project in Android Studio
7. Download the Android Support Libraries using the built-in Android SDK Manager in Android Studio
8. Create a file called `keystore.properties` in the root project folder
9. Create a new keystore or using an existing one, and add the following information to `keystore.properties`
```
storeFile=[path to file]
storePassword=[password for the keystore]
keyAlias=[name of the key used for signing the application]
keyPassword=[password for the key]
```
10. Build the application


## Authors

* Samson Choi 	[Github](https://github.com/shlchoi)
* Matthew Chum 	[Github](https://github.com/mattcwc)
* Lawrence Choi	[Github](https://github.com/l2choi)
* Matthew Leung [Github](https://github.com/mshleung)


## Acknowledgments
* Google, The Android Open Source Project - [Android Support Libraries](https://developer.android.com/topic/libraries/support-library/index.html)
* [Firebase](https://firebase.google.com) - [Android Firebase Libraries](https://firebase.google.com/docs/android/)
* [Daniel Cachapa](https://github.com/cachapa) - [Expandable Layout Library](https://github.com/cachapa/ExpandableLayout)
* [Markus Junginger, GreenRobot](http://greenrobot.org/) - [EventBus](http://greenrobot.org/eventbus/)
* Facebook - [Stetho](http://facebook.github.io/stetho/)
* [Evan Tatarka](https://github.com/evant) - [Retrolambda Plugin](https://github.com/evant/gradle-retrolambda)
* [Jake Wharton](https://github.com/JakeWharton) - [Butterknife](http://jakewharton.github.io/butterknife/)


## License

Distributed under the GNU GPLv3 license. See [LICENSE](https://github.com/shlchoi/semaphore-android/blob/master/LICENSE) for more information.

Libraries are used under Google's [Google API Terms of Service](https://developers.google.com/terms/), Firebase's [Terms of Service](https://www.firebase.com/terms/terms-of-service.html), the [BSD License](https://opensource.org/licenses/BSD-3-Clause) and [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

### BSD License
* [Stetho](http://facebook.github.io/stetho/)

### Apache License
* [Android Support Libraries](https://developer.android.com/topic/libraries/support-library/index.html)
* [Expandable Layout Library](https://github.com/cachapa/ExpandableLayout)
* [EventBus](http://greenrobot.org/eventbus/)
* [Retrolambda Plugin](https://github.com/evant/gradle-retrolambda)
* [Butterknife](http://jakewharton.github.io/butterknife/)
