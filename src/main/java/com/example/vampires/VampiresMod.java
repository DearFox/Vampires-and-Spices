package com.example.vampires;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VampiresMod implements ModInitializer {
    public static final String MOD_ID = "vampires-and-spices";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Время горения в тиках (20 тиков = 1 секунда). 8 секунд как у огня.
    private static final int BURN_DURATION_TICKS = 80;

    @Override
    public void onInitialize() {
        LOGGER.info("Vampires and Spices: инициализация серверной логики");

        // Регистрируем событие, которое срабатывает в конце каждого тика сервера
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Перебираем всех игроков на сервере
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // 1. Проверяем, что игрок в режиме выживания или приключения
                GameMode gameMode = player.interactionManager.getGameMode();
                if (gameMode != GameMode.SURVIVAL && gameMode != GameMode.ADVENTURE) {
                    continue;
                }

                // 2. Проверяем наличие тега "Vampire"
                if (!player.getCommandTags().contains("Vampire")) {
                    continue;
                }

                // 3. Получаем уровень небесного света в позиции игрока
                World world = player.getWorld();
                int skyLight = world.getLightingProvider()
                        .get(LightType.SKY)
                        .getLightLevel(player.getBlockPos());

                // 4. Если уровень света >= 10, поджигаем игрока
                if (skyLight >= 10) {
                    // Устанавливаем огонь на BURN_DURATION_TICKS тиков
                    player.setFireTicks(BURN_DURATION_TICKS);
                    // Опционально: можно отправить сообщение в лог для отладки
                    // LOGGER.info("Подожжён вампир: {} (свет: {})", player.getName().getString(), skyLight);
                }
            }
        });
    }
}