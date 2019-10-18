package com.shinonometn.re.ssim.caterpillar.application.component

import com.fasterxml.jackson.core.type.TypeReference
import com.shinonometn.re.ssim.caterpillar.application.commons.TermLabelItem
import com.shinonometn.re.ssim.commons.JSON
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.BoundValueOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.stereotype.Component
import java.util.*

@Component
class TermListCacheManager(redisConnectionFactory: RedisConnectionFactory) {

    private val ops: BoundValueOperations<String, Collection<TermLabelItem>>

    init {
        val redisTemplate = RedisTemplate<String, Collection<TermLabelItem>>()
        redisTemplate.connectionFactory = redisConnectionFactory

        redisTemplate.valueSerializer = object : RedisSerializer<Collection<TermLabelItem>> {
            private val typeReference = object : TypeReference<Collection<TermLabelItem>>() {}

            override fun serialize(p0: Collection<TermLabelItem>?): ByteArray? {
                return JSON.writeToByte(p0)
            }

            override fun deserialize(p0: ByteArray?): Collection<TermLabelItem>? {
                return if(p0 == null) null else JSON.read(p0, typeReference)
            }

        }

        redisTemplate.afterPropertiesSet()

        ops = redisTemplate.boundValueOps("re_ssim_caterpilar_application_term_list_cache")
    }

    fun save(termLabelItems: Collection<TermLabelItem>) {
        ops.set(termLabelItems)
    }

    fun get(): Collection<TermLabelItem> {
        return ops.get() ?: Collections.emptyList()
    }
}