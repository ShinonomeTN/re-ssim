package com.shinonometn.re.ssim.application.configuration

import com.shinonometn.re.ssim.application.controller.froms.LoginForm
import com.shinonometn.re.ssim.commons.validation.ValidationBuilder
import com.shinonometn.re.ssim.commons.validation.ValidationFunctions
import com.shinonometn.re.ssim.commons.validation.ValidationMeta
import com.shinonometn.re.ssim.commons.validation.Validator
import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting
import com.shinonometn.re.ssim.service.courses.entity.SchoolCalendarEntity
import com.shinonometn.re.ssim.service.user.entity.User
import org.intellij.lang.annotations.Language
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class WebFormValidationConfiguration {

    @Language("RegExp")
    private val usernameValidator = ValidationFunctions.forRegex("^[A-Za-z0-9\\-_]{4,64}$")
    private val passwordValidator = ValidationFunctions.forLength(6, 64)
    private val urlValidator = ValidationFunctions.forLength(0,256);

    @Language("RegExp")
    private val caterpillarProfileNameValidator = ValidationFunctions.forRegex("^[\\w\\d ]{1,32}$")
    @Language("RegExp")
    private val caterpillarUsernameValidator = ValidationFunctions.forRegex("^[A-Za-z\\d_-]{1,32}$")
    private val caterpillarEncodingValidator = ValidationFunctions.forRegex("^(utf8|UTF8|gbk|GBK|gbk2312|GBK2312)$")
    private val caterpillarThreadValidator = ValidationFunctions.forLength(1, 8)

    private val notNullValidator = ValidationFunctions.notNull()
    private val nullValidator = ValidationFunctions.shouldNull()
    private val notEmptyValidator = ValidationFunctions.notEmpty()

    @Bean
    open fun validationMeta(): ValidationMeta = ValidationBuilder
            .create()

            // Login form
            .of(LoginForm::class.java)
            .addValidator("username", usernameValidator)
            .addValidator("password", passwordValidator)

            // User create form
            .of(User::class.java)
            .addValidator("username", usernameValidator)
            .addValidator("password", passwordValidator)
            .addValidator("avatar",urlValidator)
            .addValidator("enable", notNullValidator)
            .addValidator("registerDate", nullValidator)

            // User edit form
            .ofGroup("user?edit")
            .baseOn(User::class.java)
            .exclude("username")
            .addValidator("username", nullValidator)

            // Caterpillar setting form
            .of(CaterpillarSetting::class.java)
            .addValidator("owner", nullValidator)
            .addValidator("name", caterpillarProfileNameValidator)
            .addValidator("username", caterpillarUsernameValidator)
            .addValidator("password", passwordValidator)
            .addValidator("role", notEmptyValidator)
            .addValidator("userAgent", notEmptyValidator)
            .addValidator("encoding", caterpillarEncodingValidator)
            .addValidator("threads", caterpillarThreadValidator)

            // Calendar
            .of(SchoolCalendarEntity::class.java)
            .addValidator("term", notEmptyValidator)
            .addValidator("termName", notEmptyValidator)
            .addValidator("startDate", notNullValidator)
            .addValidator("endDate", notNullValidator)
            .addValidator("createTime", nullValidator)

            .build()

    @Bean
    open fun validator(validationMeta: ValidationMeta) = Validator(validationMeta)
}
