ShapeImageView
====
[ ![Download](https://api.bintray.com/packages/vextil/maven/shapeimageview/images/download.svg) ](https://bintray.com/vextil/maven/shapeimageview/_latestVersion)

An ImageView that supports rounded corners, circles and borders using shaders which is the fastest rendering method. Based on [EffectiveShapeView](https://github.com/TangXiaoLv/EffectiveShapeView).

Gradle
----
```
dependencies {
    compile 'io.vextil:shapeimageview:1.0.1'
}
```

Usage
---
```xml
<io.vextil.shapeimageview.ShapeImageView
        android:src="@drawable/android"
        app:shape="rounded"
        app:radius="6dp"/>
```
        
Changelog
---
**v1.0.1**
- Added "radius"
- Use dimen for radius_x, radius_y and radius

**v1.0.0**
- Initial release 

License
---
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
