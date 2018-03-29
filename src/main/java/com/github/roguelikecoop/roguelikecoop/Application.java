package io.github.roguelikecoop.roguelikecoop;

import org.codetome.zircon.api.Position;
import org.codetome.zircon.api.Size;
import org.codetome.zircon.api.SwingTerminalBuilder;
import org.codetome.zircon.api.resource.CP437TilesetResource;
import org.codetome.zircon.api.terminal.Terminal;

public class Application {
    public static final String TITLE = "Roguelike-Coop";

    private Terminal terminal;
    private Language lang;

    private Application () {
        terminal = new SwingTerminalBuilder()
            .title(TITLE)
            .font(CP437TilesetResource.WANDERLUST_16X16.toFont())
            .initialTerminalSize(Size.of(40, 25))
            .build();

        lang = Language.loadResource(Language.DEFAULT);
    }

    public String getString (String key) {
        return lang.getString(key);
    }

    public static void main (String[] args) {
        Application app = new Application();
        int x = 0;

        for (char ch : app.getString("helloWorld").toCharArray()) {
            app.terminal.setCharacterAt(Position.of(x, 0), ch);
            x++;
        }

        app.terminal.flush();
    }
}
