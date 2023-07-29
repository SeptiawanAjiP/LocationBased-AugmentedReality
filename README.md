# AUF-AR
![video](https://raw.githubusercontent.com/SeptiawanAjiP/AugmentedReality-LocationBased/master/video.gif)

AUF-AR is an Android library for displaying Location Based Augmented Reality features. This library is the result of a modification of [this app](https://github.com/dat-ng/ar-location-based-android) made by Mr dat-ng. (Thanks to Mr dat-ng, for creating this amazing app)

## Installation

Use Gradle. Add it in your settings.gradle at the end of repositories:

```bash
dependencyResolutionManagement {
    repositories {
        ....
        maven { url 'https://jitpack.io' }
    }
}
```
then, add the dependecy in your build.gradle file (Module)
```bash
dependencies {
    implementation 'com.github.SeptiawanAjiP:AugmentedReality-LocationBased:1.0.0'
}
```
replace Tag with the latest version.

## Usage

To use this library, just extend the ARActivity class in yout activity class. To add a point, just add an object to getARPoints(). You can access the point that user clicked inside onARPointSelected's method.

```bash
class MainActivity : ARActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentARPoints = getARPoints()
        currentARPoints.clear()
        currentARPoints.add(ARPoint("MONAS", -6.175311688689925, 106.82604761289097))
        currentARPoints.add(ARPoint("TMII",  -6.304430001834124, 106.89161623298563))
    }

    override fun onARPointSelected(arPoint: ARPoint) {
        Toast.makeText(applicationContext, arPoint.name, Toast.LENGTH_SHORT).show()
    }
}
```
