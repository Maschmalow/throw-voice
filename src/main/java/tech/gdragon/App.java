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

    new DiscordBot(token);

  }
}
