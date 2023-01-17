# Release workflow

The release workflow of the SDK is based on tags creation.
We support library modules and each module needs to have its own tag.

Example:
module: `sdk.core` need to have a tag that starts with `core-ThenSemVer (core-1.0.0)`

## Supported tags

### `core-*`

the core tag is used for the `sdk.core` module.

### `android-camera-*`

The android camera tag is used for the `sdk.camera` module, it does not depends on any module.

### `android-ui-*`

the android ui tag is used for the `sdk.ui` module, it depends on `sdk.core` and `sdk.camera`
modules.

## The workflows

### Core release workflow

To publish a new version of the core module, you need to make sure to create a tag that starts with
`core-`. When you push it to Github, the `publish_core.yml` action is triggered to build the core
for all supported platform and publish it to Github / packages.

### Android Camera release workflow

To publish a new version of the camera, you need to make sure to create a tag that starts
with `android-camera-`. When you push it to Github, the `publish_android_camera` action is
triggered to build and publish the android camera module to Github packages.

### Android UI release workflow

To publish a new camera version, you need to make a sire to create a tag that starts with
`android-ui`. When you push it to Github, the `publish_android_ui.yml` action is triggered to
build and publish the android ui module to Github packages.

Please, keep in mind when you publish new versions of `sdk.core` or `sdk.camera`, you need to bump
the tag of `sdk.ui.android` to the respective version.

### Android Demo release workflow

The publishing of the Android Demo app is done automatically, whenever you create new tags `core-*`
or `android-*`. The action `publish_android_demo.yml` will take care of publishing the demo app to
firebase app distribution.

Please keep in mind, the Android demo app uses the version name of the pushed tag.

## Pipelines

### Android

For Android, we support multiple release pipelines. all the release pipelines for android need to
start with `publish_android_*.yml`.