package com.shinonometn.re.ssim.service.user;

import com.shinonometn.re.ssim.service.user.entity.Role;
import com.shinonometn.re.ssim.service.user.repository.UserRepository;
import com.shinonometn.re.ssim.service.statistics.ManagementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.WeakHashMap;

@Service
public class SecurityInfoService {

    private final UserRepository userRepository;

    private final ManagementService managementService;

    private WeakHashMap<String, Role> roleCache;

    @Value("${app.cache.role.use-second-level-cache}")
    private Boolean isUseSecondLevelRoleCache = true;

    public SecurityInfoService(UserRepository userRepository,
                               ManagementService managementService) {
        this.userRepository = userRepository;
        this.managementService = managementService;

        if (isUseSecondLevelRoleCache) roleCache = new WeakHashMap<>();
    }

    /**
     * Get role from database
     * <p>
     * It will cache a role in a WeakHashMap, if the
     * map not containing a role, query from DB
     *
     * @param name name represented a role
     * @return Role that probably cached
     */
    public Role getRoleByName(String name) {

        if (isUseSecondLevelRoleCache)
            if (roleCache.containsKey(name)) return roleCache.get(name);

        Role result = managementService.findRole(name);
        if (result == null) return null;

        roleCache.put(name, result);
        return result;
    }

//    /**
//     * Get UserDetails by Username
//     *
//     * @param username a name represented an user
//     * @return UserDetails not mutable
//     * @throws UserNotFoundException If no username is matched the given
//     */
//    @Override
//    public UserDetails getUserDetailsByUsername(String username) throws UserNotFoundException {
//        User user = userRepository.getByUsername(username);
//        if (user == null) throw new UserNotFoundException("No user named " + username + ".");
//
//        return new UserDetails() {
//
//            @Override
//            public String getUsername() {
//                return user.getUsername();
//            }
//
//            @Override
//            public Boolean isAvailable() {
//                return true;
//            }
//
//            @Override
//            public List<GrantedAuthority> getGrantedAuthority() {
//                if (user.getRoles() == null) return new ArrayList<>();
//                return user.getRoles().stream().map(i -> (GrantedAuthority) () -> i).collect(Collectors.toList());
//            }
//        };
//    }
}
