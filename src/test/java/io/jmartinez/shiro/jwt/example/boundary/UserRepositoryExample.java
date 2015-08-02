package io.jmartinez.shiro.jwt.example.boundary;

import io.jmartinez.shiro.jwt.example.entity.UserDefaultExample;
import io.jmartinez.shiro.jwt.repository.UserDefault;
import io.jmartinez.shiro.jwt.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.shiro.authc.credential.PasswordService;
import org.ops4j.pax.shiro.cdi.ShiroIni;

@Singleton
public class UserRepositoryExample implements UserRepository {

    @Inject
    @ShiroIni
    PasswordService passwordService;

    private byte[] sharedKey;
    private Map<Object, UserDefaultExample> userIdValues = new HashMap<>();
    private final Map<Object, UserDefaultExample> idValues = new HashMap<>();

    @PostConstruct
    public void init() {

        UserDefaultExample udt1 = new UserDefaultExample("id1", "userId1", passwordService.encryptPassword("password1"));
        UserDefaultExample udt2 = new UserDefaultExample("id2", "userId2", passwordService.encryptPassword("password2"));

        userIdValues.put("userId1", udt1);
        userIdValues.put("userId2", udt2);

        idValues.put("id1", udt1);
        idValues.put("id2", udt2);
    }

    @Override
    public UserDefault findByUserId(Object userId) {
        return userIdValues.get(userId);
    }

    @Override
    public UserDefault findById(Object id) {
        return idValues.get(id);
    }

    @Override
    public String getIssuer() {
        return "issuer";
    }

    @Override
    public long getExpirationDate() {
        return 1000 * 5;
    }

    @Override
    public byte[] getSharedKey() {
        if (sharedKey == null) {
            sharedKey = generateSharedKey();
        }
        return sharedKey;
    }

}
