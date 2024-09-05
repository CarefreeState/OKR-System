package com.macaku.user.websocket.session;

import com.macaku.common.util.session.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.websocket.Session;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 16:37
 */
@Repository
@Slf4j
public class WsSessionMap extends SessionMap<Session> {

}
