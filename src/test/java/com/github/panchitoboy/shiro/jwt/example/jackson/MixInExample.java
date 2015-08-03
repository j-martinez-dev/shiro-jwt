package com.github.panchitoboy.shiro.jwt.example.jackson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.panchitoboy.shiro.jwt.example.entity.UserDefaultExample;
import com.github.panchitoboy.shiro.jwt.repository.UserDefault;

public abstract class MixInExample {

    @JsonDeserialize(as=UserDefaultExample.class)
    @JsonSerialize(as=UserDefaultExample.class)
    abstract UserDefault getUser();
}
