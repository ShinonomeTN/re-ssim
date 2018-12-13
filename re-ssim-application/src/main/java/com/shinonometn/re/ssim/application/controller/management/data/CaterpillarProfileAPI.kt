package com.shinonometn.re.ssim.application.controller.management.data

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.application.security.WebSubjectUtils
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.service.caterpillar.CaterpillarDataService
import com.shinonometn.re.ssim.service.caterpillar.CaterpillarTaskService
import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/caterpillar/profiles")
class CaterpillarProfileAPI(private val caterpillarProfileDataService: CaterpillarDataService,
                            private val caterpillarTaskService: CaterpillarTaskService,
                            private val validator: Validator) {

    @GetMapping
    @ApiDescription(title = "List all data profile", description = "List all data profiles")
    @RequiresPermissions("profile:data:list")
    fun list(@PageableDefault pageable: Pageable): Page<CaterpillarSetting> =
            caterpillarProfileDataService.findAllByUser(WebSubjectUtils.currentUser().username!!, pageable)

    @PostMapping
    @ApiDescription(title = "Create a data profile", description = "Profile will binding to current user, reject if profile exists.")
    @RequiresPermissions("profile:data:write")
    fun save(@RequestBody caterpillarSetting: CaterpillarSetting): CaterpillarSetting {

        validator.validate(caterpillarSetting)

        val username = WebSubjectUtils.currentUser().username!!

        caterpillarProfileDataService.findProfile(username, caterpillarSetting.name!!).run {
            return if (isPresent) {
                val oldOne = get()
                if (username != caterpillarSetting.username) throw BusinessException("not_profile_owner")
                BeanUtils.copyProperties(caterpillarSetting, oldOne, "id")
                caterpillarProfileDataService.save(oldOne)
            } else {
                caterpillarSetting.owner = username
                caterpillarProfileDataService.save(caterpillarSetting)
            }
        }
    }

    @PostMapping(value = ["/{name}"], params = ["validate"])
    @ApiDescription(title = "Caterpillar Settings", description = "Validate a data setting.")
    @RequiresPermissions("profile:data:validate")
    fun checkSettings(@PathVariable("name") name: String) {
        val caterpillarSetting = caterpillarProfileDataService
                .findProfile(WebSubjectUtils.currentUser().username!!, name)
                .orElseThrow { BusinessException("profile_not_found") }

        RexModel<Any>()
                .withMessage(if (caterpillarTaskService.validateSettings(caterpillarSetting)) "pass" else "failed")
    }

}
