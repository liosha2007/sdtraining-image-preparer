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
* Deleting image

### Creating new project

![](screens/create-project.png)

### Used technologies and libraries

* Kotlin Compose
* [Chrynan Navigation](https://github.com/chRyNaN/navigation)
* [compose-multiplatform-file-picker](https://github.com/Wavesonics/compose-multiplatform-file-picker)
* [Koin Dependency Injection Framework](https://github.com/InsertKoinIO/koin)

### TODO list
* Convert image to another format
* Refresh list of images using F5
* Default position of crop rectangle must be in the center of an image after opening next image
* Button to make square in crop mode
* Different sorting of keywords on right panel
* Adding keywords to all caption files

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

### Known bugs

* Preview images are shown as progressbar until scroll
* Opening next image when app is focused after some pause

### Useful links

* [compose-multiplatform-goes-alpha](https://blog.jetbrains.com/kotlin/2021/08/compose-multiplatform-goes-alpha/)
* [compose-multiplatform-tutorials](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/)
* [compose-multiplatform-examples](https://github.com/JetBrains/compose-multiplatform#examples)



