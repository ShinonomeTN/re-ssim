package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.application.security.WebSubjectUtils
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.service.caterpillar.CaterpillarDataService
import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/caterpillar/profiles")
class CaterpillarProfileController(private val caterpillarProfileDataService: CaterpillarDataService) {

    @GetMapping
    @ApiDescription(title = "List all caterpillar profile", description = "List all caterpillar profiles")
    @RequiresPermissions("profile:caterpillar:list")
    fun list(@PageableDefault pageable: Pageable): Page<CaterpillarSetting> = caterpillarProfileDataService.findAll(pageable)

    @PostMapping
    @ApiDescription(title = "Create a caterpillar profile", description = "Profile will binding to current user, reject if profile exists.")
    @RequiresPermissions("profile:caterpillar:write")
    fun save(@RequestBody caterpillarSetting: CaterpillarSetting): CaterpillarSetting {

        val username = WebSubjectUtils.currentUser().username!!

        caterpillarProfileDataService.findProfile(username, caterpillarSetting.name!!).run {
            return if (isPresent) {
                val oldOne = get();
                if (username != caterpillarSetting.username) throw BusinessException("not_profile_owner")
                BeanUtils.copyProperties(caterpillarSetting, oldOne, "id")
                caterpillarProfileDataService.save(oldOne)
            } else {
                caterpillarSetting.owner = username
                caterpillarProfileDataService.save(caterpillarSetting)
            }
        }
    }

}
