package io.github.kloping.qqbot;

import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.AutoStandAfter;
import io.github.kloping.MySpringTool.annotations.Entity;
import io.github.kloping.MySpringTool.h1.impl.component.HttpStatusReceiver;
import io.github.kloping.MySpringTool.interfaces.Logger;
import io.github.kloping.MySpringTool.interfaces.component.HttpClientManager;
import io.github.kloping.common.Public;
import io.github.kloping.qqbot.api.BotContent;
import io.github.kloping.qqbot.entities.Bot;
import io.github.kloping.qqbot.entities.qqpd.message.RawMessage;
import io.github.kloping.qqbot.http.data.ActionResult;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.lang.reflect.Method;

/**
 * @author github.kloping
 */
@Entity
public class HttpClientConfig implements HttpStatusReceiver {
    @AutoStandAfter
    public void after(HttpClientManager manager) {
        manager.addHttpStatusReceiver(this);
    }

    @AutoStand
    Logger logger;

    @AutoStand
    Bot bot;

    @Override
    public void receive(String url, Integer code, Class<?> interface0, Method method,
                        Connection.Method reqMethod, Class<?> cla, Object o, Document metadata) {

        logger.log(String.format("Use the (%s) method through the (%s) interface to request " +
                        "the data obtained by the response code of the (%s) URL is (%s), " +
                        "and (%s) may be converted to (%s) type Will be processed and filtered"
                , reqMethod.name(), interface0.getSimpleName(), url, code, o, cla.getSimpleName()
        ));
        if (o == null) return;
        fillAll(cla, o);

        Public.EXECUTOR_SERVICE.submit(() -> {
            if (o instanceof ActionResult) {
                ActionResult result = (ActionResult) o;
                if (result.getSent()) {
                    RawMessage rawMessage = result.getRawMessage();
                    if (url.contains("dms")) {
                        logger.info(String.format("Bot(%s): %s <= %s",
                                bot.getInfo().getUsername(),
                                rawMessage.getChannelId() + "(私信)", rawMessage.getContent().trim()));
                    } else {
                        logger.info(String.format("Bot(%s): %s <= %s",
                                bot.getInfo().getUsername(),
                                bot.getGuild(rawMessage.getGuildId()).getChannel(rawMessage.getChannelId()).getName(),
                                rawMessage.getContent().trim()));
                    }
                }
            }
        });
    }

    public void fillAll(Class<?> cla, Object o) {
        if (o instanceof BotContent) {
            BotContent content = (BotContent) o;
            content.setBot(bot);
        }

        if (cla.isArray()) {
            Object[] obs = (Object[]) o;
            for (Object ob : obs) {
                if (ob instanceof BotContent) {
                    BotContent content = (BotContent) ob;
                    content.setBot(bot);
                }
            }
        }
    }
}
