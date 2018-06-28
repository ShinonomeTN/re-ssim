package com.shinonometn.re.ssim.services;

import com.shinonometn.re.ssim.models.BaseUserInfoDTO;
import com.shinonometn.re.ssim.models.CaterpillarSettings;
import com.shinonometn.re.ssim.models.User;
import com.shinonometn.re.ssim.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    private final Logger logger = LoggerFactory.getLogger("com.shinonometn.re.ssim.management");

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SettingService(UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;

        init();
    }

    private void init(){

        // If no user, create one
        if(mongoTemplate.getCollection(mongoTemplate.getCollectionName(User.class)).count() == 0){

            final String username = "admin";
            final String password = "admin123";

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            mongoTemplate.save(user);

            logger.info("There has no user, auto create a default user. username is {}, password {}", username, password);
        }

    }

    @Nullable
    public User getUser(String username) {
        return userRepository.getByUsername(username);
    }

    public boolean checkToken(String username, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username).and("password").is(password));
        return !mongoTemplate.find(query,User.class).isEmpty();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void removeUser(@NotNull String id) {
        userRepository.deleteById(id);
    }

    @NotNull
    public List<BaseUserInfoDTO> listUsers() {
        return userRepository.findAllDto();
    }
}
