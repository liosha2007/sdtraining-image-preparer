## Stable Diffusion training - Image preparer

The application is aimed to prepare images for training Stable Diffusion (using AUTOMATIC1111 interface)

**Early alfa version**

### Main window

![](screens/main-window.png)

### Current functionality

* Creating caption files for each image in the directory
* Adding/removing keywords from caption files by selecting them in list of keywords (right panel)
* Preparing keyword list analyzing exist captions files
* Cropping/resizing image so that it will be in specified size

### Creating new project

![](screens/create-project.png)

### Used technologies and libraries

* Kotlin Compose
* [Chrynan Navigation](https://github.com/chRyNaN/navigation)
* [compose-multiplatform-file-picker](https://github.com/Wavesonics/compose-multiplatform-file-picker)
* [Koin Dependency Injection Framework](https://github.com/InsertKoinIO/koin)

### TODO list
* Refresh thumbnails after cropping (now there is a bug - it is refreshed after next recompisition)
* Hotkeys for cancelling and applying cropping
* Button to close project
* Button to create/drop all caption files
* Button to delete source image
* Square cropping area
* Convert image to another format 

### Compiling and running

Run project

```shell
gradlew.bat clean compileKotlin run
```

Run distributable

```shell
gradlew.bat clean compileKotlin runReleaseDistributable
```

Create distributable

```shell
gradlew.bat clean compileKotlin createDistributable
```

### Useful links

* [compose-multiplatform-goes-alpha](https://blog.jetbrains.com/kotlin/2021/08/compose-multiplatform-goes-alpha/)
* [compose-multiplatform-tutorials](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/)
* [compose-multiplatform-examples](https://github.com/JetBrains/compose-multiplatform#examples)



