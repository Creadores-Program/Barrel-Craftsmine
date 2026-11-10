package com.nukkitx.protocol.bedrock.data;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GameRuleData<T> {
    String name;
    T value;

    @Override
    public String toString() {
        return this.name + '=' + this.value;
    }
}
