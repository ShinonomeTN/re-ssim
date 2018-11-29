package com.shinonometn.re.ssim.service.statistics;

import com.shinonometn.re.ssim.service.caterpillar.entity.CaterpillarSetting;
import com.shinonometn.re.ssim.service.caterpillar.repository.CaterpillarSettingRepository;
import com.shinonometn.re.ssim.commons.CacheKeys;
import com.shinonometn.re.ssim.service.user.entity.Role;
import com.shinonometn.re.ssim.service.user.entity.User;
import com.shinonometn.re.ssim.service.user.repository.RoleRepository;
import com.shinonometn.re.ssim.service.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagementService {

    private final Logger logger = LoggerFactory.getLogger("com.shinonometn.re.ssim.management");

    private final MongoTemplate mongoTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CaterpillarSettingRepository caterpillarSettingRepository;
//    private final UserPermissionRepository userPermissionRepository;

    @Autowired
    public ManagementService(UserRepository userRepository,
                             MongoTemplate mongoTemplate,
                             StringRedisTemplate stringRedisTemplate,
                             RoleRepository roleRepository,
                             CaterpillarSettingRepository caterpillarSettingRepository
//                             UserPermissionRepository userPermissionRepository
    ) {

        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.roleRepository = roleRepository;
        this.caterpillarSettingRepository = caterpillarSettingRepository;
//        this.userPermissionRepository = userPermissionRepository;

        init();
    }

    private void init() {

        // If no user, create one
        if (mongoTemplate.getCollection(mongoTemplate.getCollectionName(User.class)).count() == 0) {

            final String username = "admin";
            final String password = "admin123";

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            mongoTemplate.save(user);

            logger.info("There has no user, auto create a default user. username is {}, password {}", username, password);
        }

    }

    /*
     *
     *
     *
     *
     *
     *
     * */

    @Nullable
    public User getUser(String username) {
        return userRepository.getByUsername(username);
    }

    public boolean checkToken(String username, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username).and("password").is(password));
        return !mongoTemplate.find(query, User.class).isEmpty();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void removeUser(@NotNull String id) {
        userRepository.deleteById(id);
    }

    public List<CaterpillarSetting> listSettings(String id) {
        return caterpillarSettingRepository.findAllByUserId(id);
    }

    /*
     *
     *
     *
     * */

    @CachePut(CacheKeys.SECURITY_ROLE_INFO)
    public Role findRole(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @CacheEvict({
            CacheKeys.SECURITY_ROLE_INFO
    })
    public void saveRole(@NotNull Role newRole) {
        roleRepository.save(newRole);
    }

    /*
     *
     *
     *
     *
     *
     * */

    public void increaseVisitCount() {
        if (stringRedisTemplate.hasKey(CacheKeys.WEBSITE_API_VISIT_COUNT))
            stringRedisTemplate.opsForValue().increment(CacheKeys.WEBSITE_API_VISIT_COUNT, 1);
        else
            stringRedisTemplate.opsForValue().set(CacheKeys.WEBSITE_API_VISIT_COUNT, "1");
    }

    public Long getVisitCount() {
        return Long.valueOf(stringRedisTemplate.opsForValue().get(CacheKeys.WEBSITE_API_VISIT_COUNT));
    }

//    public void saveUserPermissionTable(@NotNull UserPermission userPermissionTable) {
//        userPermissionRepository.save(userPermissionTable);
//    }
}
