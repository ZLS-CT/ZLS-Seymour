package com.zephy.zls;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DrawSingleItemStackEvent {
    private static final Map<String, Draw> registeredListeners = new ConcurrentHashMap<>();

    public static void register(String eventID, Draw listener) {
        if (registeredListeners.containsKey(eventID)) {
            return;
        }

        registeredListeners.put(eventID, listener);
        DRAW_ITEM_RENDER_DATA.register(listener);
    }

    public static final Event<Draw> DRAW_ITEM_RENDER_DATA = EventFactory.createArrayBacked(Draw.class,
        listeners -> (itemRenderData) -> {
            for (Draw listener : listeners) {
                listener.onDrawSingleItem(itemRenderData);
            }
        }
    );

    public static void drawSingleItemStack(ItemRenderData itemRenderData) {
        DRAW_ITEM_RENDER_DATA.invoker().onDrawSingleItem(itemRenderData);
    }

    @FunctionalInterface
    public interface Draw {
        void onDrawSingleItem(ItemRenderData itemRenderData);
    }
}
