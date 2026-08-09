package me.vovanov.economy.repositories;

import me.vovanov.economy.model.EconomyUser;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface BankAccountRepository {
    
    /**
     * Инициализирует репозиторий: подключается к базе данных и создает необходимые таблицы.
     */
    void setup() throws Exception;

    /**
     * Создает новый банковский счет для игрока по его UUID.
     *
     * @param playerUUID UUID игрока
     */
    void createNewAccount(UUID playerUUID);

    /**
     * Загружает данные пользователя из базы данных.
     *
     * @param playerUUID UUID игрока, данные которого нужно загрузить
     * @return Объект EconomyUser или null, если аккаунт не найден
     */
    EconomyUser load(UUID playerUUID);

    /**
     * Сохраняет состояние пользователя в базу данных.
     *
     * @param user Данные пользователя для сохранения
     */
    void save(EconomyUser user);
    
    /**
     * Закрывает соединение с базой данных при отключении плагина.
     */
    void close();
}
