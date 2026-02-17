package com.github.alwaysdarkk.currencies.config;

import com.hypixel.hytale.server.core.Message;

public class MessagesConfig {
    public static final Message ONLY_PLAYER = Message.translation("commands.errors.currency.only-player");
    public static final Message PLAYER_NOT_FOUND = Message.translation("commands.errors.currency.player-not-found");
    public static final Message INVALID_AMOUNT = Message.translation("commands.errors.currency.invalid-amount");
    public static final Message PAY_DISABLE = Message.translation("commands.errors.currency.pay-disable");
    public static final Message SAME_PLAYER = Message.translation("commands.errors.currency.same-player");

    public static final Message PLAYER_BALANCE = Message.translation("commands.currency.player-balance");
    public static final Message TARGET_BALANCE = Message.translation("commands.currency.target-balance");

    public static final Message ADD_BALANCE = Message.translation("commands.currency.add-balance");
    public static final Message REMOVE_BALANCE = Message.translation("commands.currency.remove-balance");
    public static final Message SET_BALANCE = Message.translation("commands.currency.set-balance");

    public static final Message PLAYER_PAY = Message.translation("commands.currency.player-pay");
    public static final Message TARGET_PAY = Message.translation("commands.currency.target-pay");

    public static final Message LEADERBOARD_HEADER = Message.translation("commands.currency.leaderboard-header");
    public static final Message LEADERBOARD_USER_FORMAT =
            Message.translation("commands.currency.leaderboard-user-format");
    public static final Message LEADERBOARD_FOOTER = Message.translation("commands.currency.leaderboard-footer");
}
