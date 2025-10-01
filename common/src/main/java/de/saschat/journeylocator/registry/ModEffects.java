package de.saschat.journeylocator.registry;

import de.saschat.journeylocator.effects.NullEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModEffects {
    public static Map<ResourceLocation, MobEffect> effects = new HashMap<>();
    private static List<String> names = List.of("xaeroworldmap:no_cave_maps","xaerominimap:no_entity_radar_harmful","xaeroworldmap:no_cave_maps_harmful","xaerominimap:no_waypoints_harmful","xaerominimap:no_cave_maps","xaeroworldmap:no_world_map","xaerominimap:no_minimap","xaerominimap:no_entity_radar","xaerominimap:no_cave_maps_harmful","xaerominimap:no_waypoints","xaeroworldmap:no_world_map_harmful","xaerominimap:no_minimap_harmful");
    static {
        for (String name : names) {
            effects.put(ResourceLocation.parse(name), new NullEffect());
        }
    }
}
