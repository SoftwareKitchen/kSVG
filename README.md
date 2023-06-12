# kSVG
## What is kSVG
It's a library that lets you draw SVGs onto Java Images
## How dows it work?
``` kotlin
val svg = SVGImage(File("foo.svg"))
val img = BufferedImage(800,600,BufferedImage.TYPE_INT_ARGB)
svg.draw(img)
```
