# cyan - A Software Rendering 3D Engine for the Web

This is a pure software rendering 3D engine I wrote back in 2002.

By pure software rendering I mean that there's no OpenGL, no hardware acceleration, no nothing. The location and color of every pixel is worked out in software.  Transformation, clipping, lighting, rasterization, all done in software, pixel by pixel, for every frame.

The engine was running as a Java 1.1 Applet in a web browser and allowed for realtime 3D in a browser ("Web 3D" was just starting out back then). 

Performance was pretty stunning, considering the hardware of the time. I developed and tested cyan on my [600 MHz PowerPC G3-equipped Late 2001 12" iBook](https://en.wikipedia.org/wiki/IBook), yet the GaliNuva mesh with over 10.000 triangles rendered at around 30 frames per second on a 400 by 400 pixel canvas. A key factor to reaching such excellent performance was manual memory management with all-static buffers. The Garbage Collector never kicked in during the render loop. All memory needed for running the rendering pipeline was allocated _before_ entering the render loop. In the render loop, it was all manual buffer management, possible because the data structures involved were all of a known fixed size, and the number of triangles and vertices was fixed (or rather, bounded; clipping would produce some extra vertices and triangles, but a fixed-size safety buffer was enough to make it work).

The code contains implementations of some fundamental computer graphics algorithms, for example a Sutherland-Hodgeman polygon clipper, quaternion-based rotation animation (nowadays you'd probably use [Geometric Algebra](https://bivector.net)), and several flavors of triangle rasterizers (flat-shading, Gouraud-shading, texture-mapping, wireframe). Figuring out how to correctly rasterize triangles without over- or underfill was one of the harder problems. I relied heavily on Chris Hecker's epic series on [perspective texture mapping](https://chrishecker.com/Miscellaneous_Technical_Articles) published in Game Developer Magazine, because you couldn't find a detailed explanation of triangle rasterization in Computer Graphics textbooks (e.g., Foley - van Dam - Feiner - Hughes, Second Edition) back then.

If you want to understand how it all works, I suggest `RenderPipeline.java` as a starting point.

----
_Edit:_ Unfortunately, the formulas in the PDF's on Chris' Hecker's site are all a bit messed up. Here's the articles as published in Game Developer Magazine:
- [Perspective Texture Mapping, Part 1: Foundations](https://archive.org/details/GDM_AprMay_1995/page/n7/mode/2up) - GDMag Apr/May 95
- [Perspective Texture Mapping, Part 2: Rasterization](https://archive.org/details/GDM_JuneJuly_1995/page/n9/mode/2up) - GDMag Jun/Jul 95
- [Perspective Texture Mapping, Part 3: Endpoints and Mapping](https://archive.org/details/GDM_AugSept_1995/page/n9/mode/2up) - GDMag Aug/Sep 95
- [Perspective Texture Mapping, Part 4: Approximations](https://archive.org/details/GDM_DecJan_1995/page/n11/mode/2up) - GDMag Dec/Jan 96
- [Perspective Texture Mapping, Part 5: It's About Time](https://archive.org/details/GDM_AprMay_1996/page/n9/mode/2up) - GDMag Apr/May 96
