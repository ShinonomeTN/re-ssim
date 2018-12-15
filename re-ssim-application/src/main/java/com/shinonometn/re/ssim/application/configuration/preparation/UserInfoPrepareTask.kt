package com.shinonometn.re.ssim.application.configuration.preparation

import com.shinonometn.re.ssim.service.user.PermissionService
import com.shinonometn.re.ssim.service.user.RoleService
import com.shinonometn.re.ssim.service.user.UserService
import com.shinonometn.re.ssim.service.user.entity.Permission
import com.shinonometn.re.ssim.service.user.entity.Role
import com.shinonometn.re.ssim.service.user.entity.User
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

@Component
class UserInfoPrepareTask(
        private val userService: UserService,
        private val roleService: RoleService,
        private val permissionService: PermissionService
) : ServerInitializeTask {

    private val log = LoggerFactory.getLogger(UserInfoPrepareTask::class.java)

    @Value("\${app.dataDir:./}")
    private var dataDir = "./"

    override fun order() = 2

    override fun onlyAtFirstTime() = true

    override fun run() {
        if (userService.hasUser()) return

        val defaultUser: User = run {
            val defaultUsername = "admin"
            val defaultPassword = "123456"

            val stubFilename = "superUser.txt"

            val user = userService.save(User().apply {

                username = defaultUsername
                password = Hex.encodeHexString(DigestUtils.sha256(defaultPassword))
                enable = true

                registerDate = Date()
            })

            val stubFile = File(dataDir, stubFilename)
            with(BufferedWriter(FileWriter(stubFile))) {
                write("Server auto created user info:\n" +
                        "Username: $defaultUsername\n" +
                        "Password: $defaultPassword\n" +
                        "Password(Hashed):${user.password}\n\n" +
                        "Remember to change user password or disable this super powered user." +
                        "${Date()}")

                close()
            }

            log.info("System has no user, create default user $defaultUsername, password ${user.password}(hashed)." +
                    " You can check superUser.txt under data directory for details.")

            user
        }

        val permissionInfo = permissionService.findByUser(defaultUser.username!!)
        if (permissionInfo.isPresent) return

        val superPowerRoleName = "superUser"
        val superPowerRole = roleService.findByName(superPowerRoleName).orElseGet {
            roleService.save(Role().apply {
                name = superPowerRoleName
                enabled = true
                permissions.add("*")
            })
        }
        // If the role disabled (probably not happen) force enable it
        if (!superPowerRole.enabled) superPowerRole.enabled = true
        roleService.save(superPowerRole)
        log.info("Created role with name $superPowerRoleName")


        // Grant permission to the user
        permissionService.save(Permission().apply {
            user = defaultUser.username
            roles.add(superPowerRole.name!!)
        })

        log.info("Granted super power to role ${superPowerRole.name}")
    }

}