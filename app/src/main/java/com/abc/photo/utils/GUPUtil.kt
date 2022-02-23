package com.abc.photo.utils

import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.*
import java.util.*

object GUPUtil {
     fun createFilterForType(
         type: FilterType
     ): GPUImageFilter {
        return when (type) {
            FilterType.CONTRAST -> GPUImageContrastFilter(
                2.0f
            )
            FilterType.INVERT -> GPUImageColorInvertFilter()
            FilterType.PIXELATION -> GPUImagePixelationFilter()
            FilterType.HUE -> GPUImageHueFilter(
                90.0f
            )
            FilterType.GAMMA -> GPUImageGammaFilter(
                2.0f
            )
            FilterType.SEPIA -> GPUImageSepiaFilter()
            FilterType.GRAYSCALE -> GPUImageGrayscaleFilter()
            FilterType.SHARPEN -> {
                val sharpness = GPUImageSharpenFilter()
                sharpness.setSharpness(2.0f)
                sharpness
            }
            FilterType.EMBOSS -> GPUImageEmbossFilter()
            FilterType.SOBEL_EDGE_DETECTION -> GPUImageSobelEdgeDetection()
            FilterType.POSTERIZE -> GPUImagePosterizeFilter()
            FilterType.FILTER_GROUP -> {
                val filters: MutableList<GPUImageFilter> = LinkedList()
                filters.add(GPUImageContrastFilter())
                filters.add(GPUImageDirectionalSobelEdgeDetectionFilter())
                filters.add(GPUImageGrayscaleFilter())
                GPUImageFilterGroup(filters)
            }
            FilterType.SATURATION -> GPUImageSaturationFilter(
                1.0f
            )
            FilterType.VIGNETTE -> {
                val centerPoint = PointF()
                centerPoint.x = 0.5f
                centerPoint.y = 0.5f
                GPUImageVignetteFilter(centerPoint, floatArrayOf(0.0f, 0.0f, 0.0f), 0.3f, 0.75f)
            }
            FilterType.KUWAHARA -> GPUImageKuwaharaFilter()
            FilterType.SKETCH -> GPUImageSketchFilter()
            FilterType.TOON -> GPUImageToonFilter()
            FilterType.HAZE -> GPUImageHazeFilter()
            FilterType.LEVELS_FILTER_MIN -> {
                val levelsFilter = GPUImageLevelsFilter()
                levelsFilter.setMin(0.0f, 3.0f, 1.0f)
                levelsFilter
            }
        }
    }
    
    fun getFilters(): FilterList {
        val filters = FilterList()
        filters.addFilter(
            "Contrast",
            FilterType.CONTRAST
        )
        filters.addFilter(
            "Invert",
            FilterType.INVERT
        )
        filters.addFilter(
            "Pixelation",
            FilterType.PIXELATION
        )
        filters.addFilter(
            "Hue",
            FilterType.HUE
        )
        filters.addFilter(
            "Gamma",
            FilterType.GAMMA
        )
        filters.addFilter(
            "Sepia",
            FilterType.SEPIA
        )
        filters.addFilter(
            "Grayscale",
            FilterType.GRAYSCALE
        )
        filters.addFilter(
            "Sharpness",
            FilterType.SHARPEN
        )
        filters.addFilter(
            "Emboss",
            FilterType.EMBOSS
        )
        filters.addFilter(
            "Sobel Edge Detection",
            FilterType.SOBEL_EDGE_DETECTION
        )
        filters.addFilter(
            "Posterize",
            FilterType.POSTERIZE
        )
        filters.addFilter(
            "Grouped filters",
            FilterType.FILTER_GROUP
        )
        filters.addFilter(
            "Saturation",
            FilterType.SATURATION
        )
        filters.addFilter(
            "Vignette",
            FilterType.VIGNETTE
        )
        filters.addFilter(
            "Kuwahara",
            FilterType.KUWAHARA
        )
        filters.addFilter(
            "Sketch",
            FilterType.SKETCH
        )
        filters.addFilter(
            "Toon",
            FilterType.TOON
        )
        filters.addFilter(
            "Haze",
            FilterType.HAZE
        )
        filters.addFilter(
            "Levels Min (Mid Adjust)",
            FilterType.LEVELS_FILTER_MIN
        )
        return filters
    }
    
}