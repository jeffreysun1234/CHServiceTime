# CHServiceTime

This application is an architecture blueprint project. CHServiceTime is about setting a set of schedules to control the silence or vibrate of your phone. For example, you can set the vibrate or silence in the work time and set the ringing in the other time. And it also implements to backup/restore your schedule to Firebase.

The application demonstrates a couple of technologies:

- MVP Architecture
- RxJava/RxAndroid
- SQLBrite & SQLDelight
- Retrofit & Gson
- AutoValue
- Firebase Authentication
- Espresso & UI Automator

--------------------

## Development Tools

- Android Studio v2.3.3
- gradle-plugin v2.3.3
- Android SDK Build Tools v25.0.3
- MinSdkVersion 16
- CompileSDKVersion 25
- Retrolambda v3.6.0

--------------------

## Code Quality Tools

- Jacoco
- Android Hint


## Flavors

The project has a flavor dimension to let the developer test against fake data or the real local data source. mock will use a fake data source, and prod the real, production local data source.

--------------------

## Dependency Injection

Only For testability, I use the injection based on Dagger2. The Injection class exists in both mock/ and prod/ directories so it's replaced depending on the selected flavor to provide the different modules in compilation time.

I use the ThirtyInch MVP library and The architecture and the best practices of the library is standard, so I don't apply the injection to the view layer of the MVP library.

--------------------

## Testing

### Instrumentation tests

There are two options when it comes to running Android tests:

- **Running against the mock version.** Execution will be isolated from the network and the database because it will be using fake data. Tests run very fast and no production data will be modified.

    Run tests with the mock version executing:
    > $ ./gradlew connectedMockDebugAndroidTest
    
- **Running against the prod version.** Tests will take longer because the network latency will be simulated, or use the real network and the database source.

### Unit tests

Those tests are for individual classes and make use of Mocks.

|Test Type|Test Points|
| :---: | :--- |
|Unit Test|Presenters, Repository, Algorithms, Business Logics and as such|
|Android Mock Test|Single Screens|
|Android Proc Test|Base UI flow, SQLite data source|

### Code Coverage

1. All unit tests run on the Mock flavor.
2. Android Tests include both Proc and Mock flavors.
3. Create a custom gradle task for Jacoco to make a merging coverage report of both UnitTest and AndroidTest.