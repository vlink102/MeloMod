package me.vlink102.melomod.util.translation;

import lombok.Getter;

@Getter
public enum Feature {
    ERROR_COULD_NOT_CONNECT_TO_SERVER("errors.could_not_connect"),
    ERROR_CLIENT_RECEIVED_INVALID_DATA("errors.client_received_invalid_data"),
    ERROR_UNEXPECTED("errors.unexpected"),
    ERROR_UNKNOWN("errors.unknown"),
    ERROR_POTENTIALLY_SEVERE("errors.potentially_severe"),
    ERROR_INCOMPATIBLE_VERSION("errors.incompatible_version"),
    ERROR_LOST_CONNECTION("errors.lost_connection"),
    GENERIC_PLAYTIME("generic.playtime"),
    GENERIC_NONE("generic.none"),
    GENERIC_CLICK("generic.click"),
    GENERIC_HOVER("generic.hover"),
    GENERIC_CLIENT("generic.client"),
    GENERIC_SERVER("generic.server"),
    GENERIC_UPDATE_LINK("generic.update_link"),
    GENERIC_UP_TO_DATE("generic.up_to_date"),
    GENERIC_AUTHORS("generic.authors"),
    GENERIC_VERSION("generic.version"),
    GENERIC_STATUS("generic.status"),
    GENERIC_LEFT_CLICK_JOIN("generic.left_click_to_join"),
    GENERIC_REPORT_ERROR("generic.report_error"),
    GENERIC_REPORT_WARNING("generic.report_warning"),
    GENERIC_CLICK_TO_REPLY("generic.click_to_reply"),
    GENERIC_PAGE("generic.page"),
    GENERIC_AVAILABLE_COMMANDS("generic.available_commands"),
    GENERIC_ONLINE_PLAYERS("generic.online_players"),
    GENERIC_SINGLE_PLAYER("generic.single_player"),
    GENERIC_OFFLINE("generic.offline"),
    GENERIC_MAP("generic.map"),
    GENERIC_DOES_CONTAIN_DELIMITER("generic.does_contain_delimiter"),
    GENERIC_CONNECTION_LOST("generic.connection_lost"),
    GENERIC_REASON("generic.reason"),
    GENERIC_SENT_TO_SELF("generic.sent_to_self"),
    GENERIC_PRIVATE_MESSAGE("generic.private_message"),
    GENERIC_FROM("generic.from"),
    GENERIC_TO("generic.to"),
    GENERIC_BANNED("generic.banned"),
    GENERIC_BANNED_2("generic.banned_2"),
    GENERIC_BAN_INFO_SUMMARY("generic.ban_info.summary"),
    GENERIC_BAN_INFO_REASON("generic.ban_info.reason"),
    GENERIC_BAN_INFO_ADMIN("generic.ban_info.admin"),
    GENERIC_BAN_INFO_DURATION("generic.ban_info.duration"),
    GENERIC_BAN_INFO_ISSUED("generic.ban_info.issued"),
    GENERIC_BAN_INFO_EXPIRY("generic.ban_info.expiry"),
    GENERIC_BAN_INFO_REMAINING("generic.ban_info.remaining"),
    GENERIC_CHANNELS_CHAT("generic.channels.chat"),
    GENERIC_CHANNELS_DIRECT_MESSAGE("generic.channels.direct_message"),
    GENERIC_CHANNELS_SYSTEM_NOTIFICATION("generic.channels.system_notification"),
    GENERIC_CHANNELS_DEBUG("generic.channels.debug"),
    GENERIC_CHANNELS_ERROR("generic.channels.error"),
    GENERIC_CHANNELS_WARNING("generic.channels.warning"),
    GENERIC_DEBUG_DEBUG("generic.debug.debug"),
    GENERIC_DEBUG_CLICK_TO_DISABLE("generic.debug.click_to_disable"),
    GENERIC_DEBUG_ADDED_TO_QUEUE("generic.debug.added_to_queue"),
    GENERIC_DEBUG_JOINED_WORLD("generic.debug.joined_world"),
    GENERIC_DEBUG_HYPIXEL_DETECTED("generic.debug.hypixel_detected"),
    GENERIC_DEBUG_FAILED_SYNC("generic.debug.failed_sync"),
    GENERIC_DEBUG_DISCONNECT("generic.debug.disconnect"),
    GENERIC_DEBUG_MESSAGE("generic.debug.message"),
    GENERIC_DEBUG_MESSENGER("generic.debug.messenger"),
    GENERIC_DEBUG_TARGET("generic.debug.target"),
    GENERIC_DEBUG_DATA("generic.debug.data"),
    GENERIC_DEBUG_CHAT_COMPONENT("generic.debug.chat_component"),
    GENERIC_DEBUG_INSERTED("generic.debug.inserted"),
    GENERIC_DEBUG_SENT_PACKET("generic.debug.sent_packet"),
    GENERIC_WARNING_FAILED_TO_EXECUTE("generic.warning.failed_to_execute"),
    GENERIC_WARNING_DETECTED_OVERSIZED_COMPONENT("generic.warning.detected_oversized_component"),
    GENERIC_WARNING_SOCKET_CLOSED("generic.warning.socket_closed"),
    GENERIC_WARNING_CLIENT_OUT_OF_DATE("generic.warning.client_out_of_date"),
    GENERIC_WARNING_CONNECTION_TERMINATED("generic.warning.connection_terminated"),
    GENERIC_WARNING_OUTDATED_VERSION("generic.warning.outdated_version"),
    GENERIC_WARNING_MALFORMED_PACKET("generic.warning.malformed_packet"),
    GENERIC_COMMANDS_ENABLED_DEBUG("generic.commands.enabled_debug"),
    GENERIC_COMMANDS_DISABLED_DEBUG("generic.commands.disabled_debug"),
    GENERIC_SYSTEM_CONNECTED("generic.system.connected"),
    GENERIC_SYSTEM_DISCONNECTED("generic.system.disconnected"),
    GENERIC_SYSTEM_GAME_DISCONNECT("generic.system.game_disconnect");

    private final String path;

    Feature(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public String getMessage(String... variables) {
        if (path != null) {
            return Translations.getMessage(path, (Object[]) variables);
        }

        return null;
    }

}
