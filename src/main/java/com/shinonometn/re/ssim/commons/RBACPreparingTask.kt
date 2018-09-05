package com.shinonometn.re.ssim.commons

import com.shinonometn.commons.tools.Names
import com.shinonometn.re.ssim.models.AttributePermission
import com.shinonometn.re.ssim.models.GrantedPermission
import com.shinonometn.re.ssim.models.Role
import com.shinonometn.re.ssim.models.User
import com.shinonometn.re.ssim.repository.AttributePermissionRepository
import com.shinonometn.re.ssim.security.AuthorityGroup
import com.shinonometn.re.ssim.security.AuthorityRequired
import com.shinonometn.re.ssim.services.ManagementService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*
import kotlin.collections.ArrayList

@Component
class RBACPreparingTask(private val managementService: ManagementService,
                        private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
                        private val attributePermissionRepository: AttributePermissionRepository
) : ApplicationListener<ContextRefreshedEvent> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun onApplicationEvent(contextRefreshedEvent: ContextRefreshedEvent) {

        initEndpointPermissionInfo()

        initRoles()
        initUser()

    }

    /**
     *
     * Scan and save all endpoint info
     *
     */
    private fun initEndpointPermissionInfo() {
        val handlerMethods = requestMappingHandlerMapping.handlerMethods.values

        val scanningTimestamp = Date()

        attributePermissionRepository.saveAll(handlerMethods
                .filter { it.hasMethodAnnotation(AuthorityRequired::class.java) }
                .map {

                    val method = it.method

                    val methodSignName = method.name +
                            Names.getShortClassNameList(method.parameterTypes.toCollection(ArrayList<Class<*>>())) +
                            "@${Names.getShortClassName(method.declaringClass.name)}"

                    val annotation = method.getAnnotation(AuthorityRequired::class.java)
                    val fatherGroupName: String? = method.run {

                        if (!declaringClass.isAnnotationPresent(AuthorityGroup::class.java)) null
                        else {
                            declaringClass.getAnnotation(AuthorityGroup::class.java).value
                        }
                    }

                    AttributePermission().apply {
                        identity = annotation.name
                        methodSign = methodSignName
                        description = annotation.description
                        group = if (!StringUtils.isEmpty(annotation.group)) annotation.group else fatherGroupName
                        scanTime = scanningTimestamp
                    }

                }.toList()
        ).also {
            logger.info("${it.size} endpoint(s) be discovered.")
        }

        attributePermissionRepository.deleteAllOldItems(scanningTimestamp)

        logger.info("Endpoint list updated")
    }

    /**
     *
     * Init roles
     *
     */
    private fun initRoles() {
        val adminRole = managementService.findRole("admin")
        if (adminRole != null) return

        val allowAllPermissions = GrantedPermission().apply {
            expression = ".+"
            type = GrantedPermission.Type.REGEX
        }

        val newRole = Role().apply {
            name = "admin"
            grantedPermission = ArrayList<GrantedPermission>().apply {
                add(allowAllPermissions)
            }
        }

        managementService.saveRole(newRole)
        logger.info("Create role ${newRole.name} with authority ${newRole.grantedPermission}.")
    }

    /**
     *
     * Init users
     *
     */
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
        managementService.saveUser(newUser)

        logger.info("Created user ${newUser.username} with password ${newUser.password}, has roles ${newUser.roles}.")
    }
}
