package com.youyu.oauth2.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Custom Deserializer for {@link User} class. This is already registered with
 * {@link SysUserMixin}. You can also use it directly with your mixin class.
 *
 * @author Jitendra Singh
 * @see SysUserMixin
 * @since 4.2
 */
class SysUserDeserializer extends JsonDeserializer<LoginUser> {

    private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET = new TypeReference<Set<SimpleGrantedAuthority>>() {
    };

    /**
     * This method will create {@link User} object. It will ensure successful object
     * creation even if password key is null in serialized json, because credentials may
     * be removed from the {@link User} by invoking {@link User#eraseCredentials()}. In
     * that case there won't be any password key in serialized json.
     *
     * @param jp   the JsonParser
     * @param ctxt the DeserializationContext
     * @return the user
     * @throws IOException if a exception during IO occurs
     */
    @Override
    public LoginUser deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        // 解析authorities
        Set<SimpleGrantedAuthority> authorities = mapper.convertValue(jsonNode.get("authorities"),
                SIMPLE_GRANTED_AUTHORITY_SET);
        // 解析UserFramework
        JsonNode userNode = readJsonNode(jsonNode, "user");
        Long userId = readJsonNode(userNode, "id").asLong();
        String username = readJsonNode(userNode, "username").asText();
        String password = readJsonNode(userNode, "password").asText("");
        String nickname = readJsonNode(jsonNode, "nickname").asText();
        String avatar = readJsonNode(jsonNode, "avatar").asText();
        Integer sex = readJsonNode(jsonNode, "sex").asInt();
        Integer adcode = readJsonNode(jsonNode, "adcode").asInt();
        String adname = readJsonNode(jsonNode, "adname").asText();
        Integer level = readJsonNode(jsonNode, "level").asInt();
        int status = readJsonNode(jsonNode, "status").asInt();

        // 解析permissions
        JsonNode permissionNode = readJsonNode(jsonNode, "permission");
        List<String> permissions = mapper.convertValue(permissionNode, new TypeReference<>() {});
        UserFramework user = new UserFramework(userId, username, password, nickname, avatar, sex, adcode, adname, level, status);

        return new LoginUser(user, permissions, authorities);
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }

}
