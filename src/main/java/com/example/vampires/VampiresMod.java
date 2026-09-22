package com.example.vampires;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VampiresMod implements ModInitializer {
    public static final String MOD_ID = "vampires-and-spices";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final int BURN_DURATION_TICKS = 160; // 8 секунд

    @Override
    public void onInitialize() {
        LOGGER.info("Vampires and Spices: инициализация серверной логики");

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                // 1. Проверяем режим игры
                GameType gameMode = player.gameMode.getGameModeForPlayer();
                if (gameMode != GameType.SURVIVAL && gameMode != GameType.ADVENTURE) {
                    continue;
                }

                // 2. Проверяем тег "Vampire"
                if (!player.entityTags().contains("Vampire")) {
                    continue;
                }

                // 3. Получаем уровень небесного света
                Level world = player.level();
                int skyLight = world.getBrightness(LightLayer.SKY, player.blockPosition());

                // 4. Поджигаем, если свет >= 10
                if (skyLight >= 10) {
                    player.setRemainingFireTicks(BURN_DURATION_TICKS);
                }
            }
        });
    }
}