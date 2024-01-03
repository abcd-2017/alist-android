package com.android.alist.utils

import com.android.alist.R

/**
 * 文件类型对应的SVG代码
 */
object FileUtils {
    //根据文件后缀名，返回不同的svg图片代码
    fun getSvgCodeByFileType(fileName: String): Int {
        return when (fileName.substring(fileName.lastIndexOf(".") + 1)) {
            "xls", "xlsx" -> R.drawable.filetype_excel
            "pdf" -> R.drawable.filetype_pdf
            "folder" -> R.drawable.filetype_folder
            "doc", "docx" -> R.drawable.filetype_word
            "ppt", "pptx" -> R.drawable.filetype_ppt
            "mp3", "flac", "ogg", "wav" -> R.drawable.filetype_music
            "exe" -> R.drawable.filetype_exe
            "apk" -> R.drawable.filetype_apk
            "sql" -> R.drawable.filetype_database
            "txt", "json", "yml", "yaml", "properties", "xml" -> R.drawable.filetype_txt
            "java", "kt", "c", "cpp", "cs", "py", "js", "ts", "html", "css", "rb", "swift", "go", "php", "r", "sh", "scala", "rs", "dart" -> R.drawable.filetype_code
            "zip", "rar", "7z", "tar", "deb", "rpm" -> R.drawable.filetype_package
            "jpeg", "jpg", "png", "bmp", "webp", "svg", "tif", "tiff" -> R.drawable.filetype_image
            "mp4", "avi", "mov", "wmv", "flv", "mkv", "mpeg", "mpg", "webm" -> R.drawable.filetype_video
            "url", "lnk" -> R.drawable.filetype_link
            else -> R.drawable.filetype_none
        }
    }

    fun conversionFileSize(size: Long): String {
        val sizeUnit = arrayOf("B", "K", "M", "G", "T", "P")
        var index = 0
        var temp = size.toFloat()
        while (temp > 1024) {
            temp /= 1024
            index++
        }
        return "${String.format("%.2f", temp)}${sizeUnit[index]}"
    }
}