package com.shinonometn.re.ssim.commons.security

import com.shinonometn.commons.tools.Names
import com.shinonometn.re.ssim.data.security.*
import com.shinonometn.re.ssim.data.security.AttributePermissionRepository
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
import kotlin.collections.HashSet

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

        var creatingCount = 0
        var updatingCount = 0

        handlerMethods.filter { it.hasMethodAnnotation(AuthorityRequired::class.java) }.map {

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

        }.forEach {
            val oldItem = attributePermissionRepository.findByMethodSign(it.methodSign)

            if (oldItem == null) attributePermissionRepository.save(it).also {
                creatingCount++
            } else attributePermissionRepository.save(oldItem.apply {
                identity = it.identity
                description = it.description
                group = it.group
                scanTime = it.scanTime
            }).also {
                updatingCount++
            }

        }

        attributePermissionRepository.deleteAllOldItems(scanningTimestamp)

        logger.info("Endpoint list updated, $creatingCount new item and $updatingCount updated.")
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
        }
        managementService.saveUser(newUser)

        val userPermissionTable = UserPermission().apply {
            roles = HashSet<String>().apply {
                add("admin")
            }
            userId = newUser.id
        }
        managementService.saveUserPermissionTable(userPermissionTable)


        logger.info("Created user ${newUser.username} with password ${newUser.password}, has roles ${newUser.roles}.")
    }
}
