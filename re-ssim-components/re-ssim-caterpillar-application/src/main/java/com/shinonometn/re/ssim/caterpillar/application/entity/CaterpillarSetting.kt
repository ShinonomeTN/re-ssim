package com.shinonometn.re.ssim.caterpillar.application.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.shinonometn.re.ssim.caterpillar.application.utils.JsonMapAttributeConverter
import java.io.Serializable
import javax.persistence.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_caterpillar_settings")
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
    var caterpillarProfile: MutableMap<String, Any?>? = HashMap()

    @Transient
    fun getAgentCode(): String? = caterpillarProfile?.getOrDefault("agentCode", null)?.toString()
}
