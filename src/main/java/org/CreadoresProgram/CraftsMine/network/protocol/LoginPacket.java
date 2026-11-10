/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 *                       Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 *
 * You can view LICENCE file for details. 
 *
 * @author The Dragonet Team
 */
package org.CreadoresProgram.CraftsMine.network.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
import org.CreadoresProgram.CraftsMine.nukkitLib.entity.data.Skin;
import org.CreadoresProgram.CraftsMine.nukkitLib.utils.Zlib;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

public class LoginPacket extends MCDPacket {

    public String username;
    public int protocol;
    public long clientId;
    public UUID clientUUID;
    public String serverAddress;
    public String clientSecret;
    public String identityPublicKey;
    public long nbf;
    public long exp;

    public String skinId = null;
    public Skin skin;
    

    @Override
    public byte pid() {
        return ProtocolInfo.LOGIN_PACKET;
    }

    @Override
    public void encode() {
    }

    @Override
    public void decode() {
        this.protocol = this.getInt();
        if(this.protocol >= 90){
            return;
        }
        byte[] str;
        try{
            str = Zlib.inflate(this.get(this.getInt()), 1024 * 1024 * 64);
        }catch(Exception e){
            return;
        }
        this.setBuffer(str, 0);
        decodeChainData();
        decodeSkinData();
    }
    public int getProtocol(){
        return protocol;
    }
    private void decodeChainData(){
        Map<String, List<String>> map = JSON.parseObject(new String(this.get(getLInt()), StandardCharsets.UTF_8), new TypeReference<Map<String, List<String>>>() {});
        if(map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
        List<String> chains = map.get("chain");
        for(String c : chains){
            JSONObject chainMap = decodeToken(c);
            if(chainMap == null) continue;
            if(chainMap.containsKey("extraData")){
                JSONObject extra = chainMap.getJSONObject("extraData");
                if(extra.containsKey("displayName")) this.username = extra.getString("displayName");
                if(extra.containsKey("identity")) this.clientUUID = UUID.fromString(extra.getString("identity"));
            }
            if(chainMap.containsKey("identityPublicKey")){
                this.identityPublicKey = chainMap.getString("identityPublicKey");
            }
            if(chainMap.containsKey("nbf")){
                this.nbf = chainMap.getLong("nbf");
            }
            if(chainMap.containsKey("exp")){
                this.exp = chainMap.getLong("exp");
            }
        }
    }
    private void decodeSkinData(){
        JSONObject skinToken = decodeToken(new String(this.get(this.getLInt())));
        if(skinToken == null) return;
        if(skinToken.containsKey("ClientRandomId")) this.clientId = skinToken.getLong("ClientRandomId");
        if(skinToken.containsKey("ServerAddress")) this.serverAddress = skinToken.getString("ServerAddress");
        if(skinToken.containsKey("SkinId")) this.skinId = skinToken.getString("SkinId");
        if(skinToken.containsKey("SkinData")){
            // Defensive: Skin constructor validates byte length (SINGLE_SKIN_SIZE
            // or DOUBLE_SKIN_SIZE); any other size throws IllegalArgumentException
            // which would abort decode() and leave username/UUID unset.
            try{
                this.skin = new Skin(skinToken.getString("SkinData"), skinId);
            }catch(IllegalArgumentException ex){
                this.skin = new Skin(new byte[Skin.SINGLE_SKIN_SIZE]);
            }
        }
    }
    private JSONObject decodeToken(String token){
        String[] base = token.split("\\.");
        if(base.length < 2) return null;
        return JSON.parseObject(new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8));
    }
    public Skin getSkin(){
        return this.skin;
    }
}
