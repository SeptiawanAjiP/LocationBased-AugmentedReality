# AUF-AR
![video](https://raw.githubusercontent.com/SeptiawanAjiP/AugmentedReality-LocationBased/master/video-demo.gif)

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
    // to handle permission request
    implementation 'pub.devrel:easypermissions:3.0.0'
    
    // AUF-AR library
    implementation 'com.github.SeptiawanAjiP:AugmentedReality-LocationBased:Tag'
}
```
replace Tag with the latest version.

## Usage

To use this library, just extend the ARActivity class in your activity class. To add points, just make ArrayList of Place, and call ARInitData with ArrayList of Place as the parameter. You must add radius (in meter) as parameter too. You can access the point that user clicked inside onARPointSelected's method.

```bash
class MainActivity : ARActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       
        val list = ArrayList<Place>()
        list.add(
            Place("1", "Coffee Shop", -6.174870735058176, 106.82620041234728, description = "Promotion available here")
        )
        list.add(
            Place("2", "Restaurant", -6.122310891453182, 106.83357892611079, description = "Good Resto")
        )
        // You want to display places within a radius of 50 meters.
        ARInitData(list, 50.00)
    }

    override fun onARPointSelected(place: Place) {
        Toast.makeText(applicationContext, place.name, Toast.LENGTH_SHORT).show()
    }
}
```
## ToDo
- [x] Filter Place with radius of distance
- [ ] Load url image on cardview
- [ ] Customize the cardview