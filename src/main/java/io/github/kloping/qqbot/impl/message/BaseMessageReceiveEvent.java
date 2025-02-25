package io.github.kloping.qqbot.impl.message;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.qqbot.api.message.MessageReceiveEvent;
import io.github.kloping.qqbot.entities.Bot;
import io.github.kloping.qqbot.entities.qqpd.Channel;
import io.github.kloping.qqbot.entities.qqpd.Guild;
import io.github.kloping.qqbot.entities.qqpd.message.RawMessage;

/**
 * @author github.kloping
 */
public class BaseMessageReceiveEvent extends BaseMessageEvent implements MessageReceiveEvent {
    public BaseMessageReceiveEvent() {
    }

    public BaseMessageReceiveEvent(RawMessage message, JSONObject jo, Bot bot) {
        super(message, jo, bot);
        message.setBot(bot);
        this.content = getRawMessage().getContent() == null ? "" : getRawMessage().getContent();
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public Guild getGuild() {
        return null;
    }

    protected String content;

    @Override
    public String getContent() {
        return content;
    }
}