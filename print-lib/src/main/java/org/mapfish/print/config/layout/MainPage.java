package org.mapfish.print.config.layout;

public class MainPage extends Page {
    public MapBlock getMap() {
        MapBlock result = null;
        for (int i = 0; i < items.size() && result == null; i++) {
            Block block = items.get(i);
            result = block.getMap();
        }
        return result;
    }
}
