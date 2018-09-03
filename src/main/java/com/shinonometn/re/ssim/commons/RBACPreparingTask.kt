package com.shinonometn.re.ssim.commons

import com.shinonometn.re.ssim.models.AttributeGrantedAuthority
import com.shinonometn.re.ssim.models.Role
import com.shinonometn.re.ssim.models.User
import com.shinonometn.re.ssim.services.ManagementService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class RBACPreparingTask(private val managementService: ManagementService) : ApplicationListener<ContextRefreshedEvent> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun onApplicationEvent(contextRefreshedEvent: ContextRefreshedEvent) {

        initRoles()
        initUser()
    }

    private fun initRoles() {
        val adminRole = managementService.findRole("admin")
        if (adminRole != null) return

        val newRole = Role().apply {
            name = "admin"
            permissionList = ArrayList<AttributeGrantedAuthority>().apply {
                add(AttributeGrantedAuthority("*:*"))
            }
        }

        managementService.saveRole(newRole)
        logger.info("Create role ${newRole.name} with authority ${newRole.permissionList}.")
    }

    private fun initUser() {
        val adminUser = managementService.getUser("admin")
        if (adminUser != null) return

        val newUser = User().apply {
            username = "admin"
            password = "123456"
            roles = ArrayList<String>().apply {
                add("admin")
            }
        }
        managementService.saveUser(adminUser)

        logger.info("Created user ${newUser.username} with password ${newUser.password}, has roles ${newUser.roles}.")
    }
}
