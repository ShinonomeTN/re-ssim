package com.shinonometn.re.ssim.services;

import com.shinonometn.re.ssim.models.*;
import com.shinonometn.re.ssim.repository.RoleRepository;
import com.shinonometn.re.ssim.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManagementService implements UserDetailsService{

    private final Logger logger = LoggerFactory.getLogger("com.shinonometn.re.ssim.management");

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final RoleRepository roleRepository;

    @Autowired
    public ManagementService(UserRepository userRepository,
                             MongoTemplate mongoTemplate,
                             RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.roleRepository = roleRepository;

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

    public Set<CaterpillarSettings> listSettings(String id){
        Optional<User> userResult = userRepository.findById(id);
        return userResult.map(User::getCaterpillarSettings).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByUsername(username);
        if(user == null) throw new UsernameNotFoundException("No user named "+ username + ".");

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user);

        if(user.getRoles() == null) {
            userDetailsDTO.setAttributeGrantedAuthorities(new ArrayList<>());
            return userDetailsDTO;
        }

        List<Role> roles = roleRepository.findAllByName(user.getRoles());
        userDetailsDTO.setAttributeGrantedAuthorities(roles
                .stream()
                .filter(role -> role.getPermissionList() != null && role.getPermissionList().size() > 0)
                .flatMap(role -> role.getPermissionList().stream())
                .collect(Collectors.toList()));

        return userDetailsDTO;
    }

    /*
    *
    *
    *
    * */

    public Role findRole(String roleName){
        return roleRepository.findByName(roleName);
    }

    public void saveRole(@NotNull Role newRole) {
        roleRepository.save(newRole);
    }
}
