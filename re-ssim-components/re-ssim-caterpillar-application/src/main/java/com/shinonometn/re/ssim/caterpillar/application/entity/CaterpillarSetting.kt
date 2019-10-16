package com.shinonometn.re.ssim.caterpillar.application.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.caterpillar.application.utils.JsonMapAttributeConverter
import org.springframework.data.annotation.Id
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@JsonInclude(JsonInclude.Include.NON_NULL)
class CaterpillarSetting : Serializable {

    /*
        Base info
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
    @Column(columnDefinition = "VARCHAR(64)")
    var owner: String? = null
    @Column(columnDefinition = "VARCHAR(64)")
    var name: String? = null
    var description: String? = null

    /*
        Caterpillar profile details
     */
    @Convert(converter = JsonMapAttributeConverter::class)
    @Column(columnDefinition = "TEXT")
    var caterpillarProfile: MutableMap<String, Any?>? = null

}
