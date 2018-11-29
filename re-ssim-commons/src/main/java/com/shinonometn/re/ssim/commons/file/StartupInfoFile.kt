package com.shinonometn.re.ssim.commons.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.shinonometn.re.ssim.commons.JSON
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@JsonInclude(JsonInclude.Include.NON_NULL)
class StartupInfoFile {
    var timestamp: Long = 0

    var errorInfo: MutableMap<String, Any?>? = null

    var meta : MutableMap<String, Any?>? = null

    fun toFile(file: File) = JSON.write(FileOutputStream(file), this)

    companion object {
        fun filename() = "startup.flag"
        fun from(file: File): StartupInfoFile = JSON.read(FileInputStream(file), object : TypeReference<StartupInfoFile>() {})
    }
}