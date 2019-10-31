package com.shinonometn.re.ssim.data.kingo.application.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.shinonometn.re.ssim.commons.JSON
import javax.persistence.AttributeConverter

open class JsonMapAttributeConverter : AttributeConverter<MutableMap<String, Any?>?, String?> {

    private val typeReference = object : TypeReference<MutableMap<String, Any?>?>() {}

    override fun convertToDatabaseColumn(attribute: MutableMap<String, Any?>?): String {
        return JSON.parse(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): MutableMap<String, Any?>? {
        return JSON.read(dbData, typeReference)
    }

}