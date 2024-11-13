## FaceRec
Android application that can compute face recognition and face verification using FaceNet model and Pytorch. Using [MDC-Android](https://m3.material.io/develop/android/mdc-android) Material 3 for Android.

<div align="center">
<img src="/mockups/screen mockups.png" width="700"/>
</div>

#### How to build
This repo does NOT contain the library `libexecutorch.so` and the converted model `facenet_xnn.pte` due to size issue. However, you can find the python script to generate it in the /models folder.
To build `libexecutorch.so` feel free to follow the [executorch tutorial](https://pytorch.org/executorch/stable/demo-apps-android.html)

- `libexecutorch.so` must be copied into the following path `/app/src/main/jniLibs/arm64-v8a/`
- `facenet_xnn.pte` must be added into the assets

#### References
 1. David Sandberg's facenet repo : https://github.com/davidsandberg/facenet
 2. Pretrained Pytorch facenet repo : https://github.com/timesler/facenet-pytorch
 3. Executorch : https://pytorch.org/executorch-overview
