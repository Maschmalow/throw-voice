package tech.gdragon;


import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class App {

  private static final Logger logger = LoggerFactory.getLogger(App.class);


  /**
   * Starts a simple HTTP Service, whose only response is to redirect to the bot's page.
   */
  public static void main(String[] args) {
    String token = System.getenv("BOT_TOKEN");
    String port = System.getenv("PORT");
    String clientId = System.getenv("CLIENT_ID");

    new DiscordBot(token);

  }
}
