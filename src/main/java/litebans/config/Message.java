package litebans.config;

import java.util.Locale;
import litebans.chat.ChatColorCode;
import litebans.chat.JsonMessageParser;
import litebans.punishment.EntryPlaceholders;
import litebans.punishment.PunishmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Message implements CharSequence {
   CMD("command"),
   C_BAN(CMD, "ban"),
   C_MUTE(CMD, "mute"),
   C_WARN(CMD, "warn"),
   C_KICK(CMD, "kick"),
   C_HISTORY(CMD, "history"),
   C_STAFFHISTORY(CMD, "staffhistory"),
   C_WARNINGS(CMD, "warnings"),
   C_PRUNEHISTORY(CMD, "prunehistory"),
   C_STAFFROLLBACK(CMD, "staffrollback"),
   C_BANLIST(CMD, "banlist"),
   C_IPHISTORY(CMD, "iphistory"),
   C_NAMEHISTORY(CMD, "namehistory"),
   C_DUPEIP(CMD, "dupeip"),
   C_IPREPORT(CMD, "ipreport"),
   C_CHECKBAN(CMD, "checkban"),
   C_CHECKMUTE(CMD, "checkmute"),
   C_CHECKWARN(CMD, "checkwarn"),
   C_LASTUUID(CMD, "lastuuid"),
   C_LOCKDOWN(CMD, "lockdown"),
   C_GEOIP(CMD, "geoip"),
   C_LITEBANS(CMD, "litebans"),
   HEX_FORMATS("hex_formats", "&%s\n<%s>\n{%s}"),
   DATE_FORMAT("time_format", "yyyy-MM-dd"),
   DEFAULT_BAN_REASON("default_ban_reason", "The Ban Hammer has spoken!"),
   DEFAULT_MUTE_REASON("default_mute_reason", "Spamming"),
   DEFAULT_UNBAN_REASON("default_unban_reason", "No reason specified."),
   DEFAULT_UNMUTE_REASON("default_unmute_reason", "No reason specified."),
   DEFAULT_UNWARN_REASON("default_unwarn_reason", "No reason specified."),
   BANNED_MESSAGE_BASE("banned_message_base", ChatColorCode.RED + "You are banned from this server!" + ChatColorCode.WHITE + "\n\nBanned on: $dateStart\nBanned by: $executor\nReason: $reason" + ChatColorCode.WHITE + '\n'),
   BANNED_MESSAGE("banned_message", "$base\nExpires in: $duration\n$appealMessage"),
   BANNED_MESSAGE_PERMANENT("banned_message_permanent", "$base\nYou are permanently banned!\n$appealMessage"),
   BANNED_MESSAGE_APPEAL("banned_message_appeal_message", ""),
   BANNED_MESSAGE_GEOIP("banned_message_geoip_blacklist", ChatColorCode.RED + "You are banned from this server!" + ChatColorCode.WHITE + "\n\nYour location is blacklisted: $geoip"),
   BANNED_MESSAGE_SWITCH("proxy_switch_banned", ChatColorCode.RED + "You are banned on $serverScope! Reason:\n$reason"),
   BANNED_MESSAGE_SWITCH_TEMP("proxy_switch_tempbanned", ChatColorCode.RED + "You are banned on $serverScope! Reason:\n$reason\nExpires in: $duration"),
   MUTED("muted", ChatColorCode.RED + "You are muted! ($duration remaining)!\n" + ChatColorCode.RED + "Reason: " + ChatColorCode.YELLOW + "$reason"),
   MUTED_PERMANENT("muted_permanent", ChatColorCode.RED + "You are permanently muted!\n" + ChatColorCode.RED + "Reason: " + ChatColorCode.YELLOW + "$reason"),
   WARNED_JOIN_START("warned_join", ChatColorCode.RED + "You have new warnings:"),
   WARNED_JOIN_ENTRY("warned_join_entry", ChatColorCode.DARK_RED + " - Warned by " + ChatColorCode.RED + "$executor" + ChatColorCode.DARK_RED + ": " + ChatColorCode.RED + "$reason\n   " + ChatColorCode.DARK_RED + "(" + ChatColorCode.RED + "$timeSince ago" + ChatColorCode.DARK_RED + ")"),
   NOTIFY("notify"),
   NOTIFY_BANNED_PLAYER_JOIN(NOTIFY, "banned_player_join", ChatColorCode.RED + "$player" + ChatColorCode.WHITE + " tried to join, but is banned ($duration)!"),
   NOTIFY_BANNED_BLACKLIST(NOTIFY, "banned_geoip_blacklist", ChatColorCode.RED + "$player" + ChatColorCode.WHITE + " tried to join, but is blacklisted ($geoip)!"),
   ERROR_NO_PERMISSION("permission_error", ChatColorCode.RED + "You don't have permission!"),
   ERROR_NO_TEMPLATE("error_no_template_provided", ChatColorCode.RED + "You must provide a template for this punishment!"),
   ERROR_NO_REASON("error_no_reason_provided", ChatColorCode.RED + "You must provide a reason for this punishment!"),
   ERROR_NO_REASON_REMOVAL("error_no_reason_removal", ChatColorCode.RED + "You must provide a reason to remove this punishment!"),
   ERROR_NO_DATABASE("error_no_sql_connection", ChatColorCode.RED + "LiteBans is not connected to a database!"),
   ERROR_NO_UUID("error_no_uuid_found", ChatColorCode.RED + "Player does not exist."),
   ERROR_NO_DURATION("error_no_duration", ChatColorCode.RED + "No valid time specification detected!"),
   ERROR_STACK_OVERFLOW("error_stack_overflow", ChatColorCode.RED + "Stack overflow detected."),
   ERROR_CONSOLE("error_console_only", ChatColorCode.RED + "This command can only be used from console."),
   ERROR_DURATION_LIMIT("duration_limit_error", ChatColorCode.RED + "Maximum duration allowed: $duration"),
   ERROR_INTERNAL("internal_error", ChatColorCode.RED + "An internal error occurred while attempting to perform this command."),
   BAN_USAGE(C_BAN, "usage", ChatColorCode.RED + "Usage: $command [-s] <player> [duration] [reason]"),
   BAN_EXAMPLE(C_BAN, "example", ChatColorCode.RED + "Example: $command Player 7d [reason]"),
   BAN_SILENT_PREFIX(C_BAN, "silent_prefix", ChatColorCode.WHITE + "[" + ChatColorCode.GRAY + "Silent" + ChatColorCode.WHITE + "] "),
   BAN_BROADCAST(C_BAN, "broadcast_ban", ChatColorCode.getString2("$executor ") + ChatColorCode.WHITE + "banned " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   BAN_BROADCAST_TEMP(C_BAN, "broadcast_tempban", ChatColorCode.getString2("$executor ") + ChatColorCode.WHITE + "tempbanned " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for $tempDuration for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   BAN_BROADCAST_IP(C_BAN, "broadcast_ip_ban", ChatColorCode.getString2("$executor ") + ChatColorCode.WHITE + "IP-banned " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   BAN_BROADCAST_TEMP_IP(C_BAN, "broadcast_temp_ip_ban", ChatColorCode.getString2("$executor ") + ChatColorCode.WHITE + "temp IP-banned " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for $tempDuration for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   BAN_PREVIOUS_BAN_REMOVED(C_BAN, "previous_ban_removed", ChatColorCode.getString2("Previous ban for $playerName removed.")),
   BAN_PREVIOUS_BAN_EXISTING(C_BAN, "previous_ban_existing", ChatColorCode.getString3("$playerName is already banned, and you do not have permission to replace existing bans.")),
   BAN_ERROR_UNBAN_FAIL(C_BAN, "unban_fail", ChatColorCode.getString3("Target is not banned!")),
   BAN_ERROR_NO_OVERRIDE(C_BAN, "no_override", ChatColorCode.getString3("Player is already banned!")),
   BAN_ALTS_GUARDRAIL_DISCLAIMER(C_BAN, "alts_guardrail_disclaimer", ChatColorCode.getString5("// WARNING WARNING WARNING WARNING WARNING //\n") + ChatColorCode.getString( "Automatically banning accounts can result in unintended and unexpected outcomes, such as causing innocent players to be banned automatically.\n" ) + ChatColorCode.getString("If you want to enable this feature, please check and ensure the following:\n") + ChatColorCode.getString3( "1) Your players are not joining from a limited range of IP addresses belonging to one specific host, such as a DDoS protection service forwarding players to your server.\n" ) + ChatColorCode.getString3( "2) All of your servers have IP forwarding set up correctly (please consult BungeeCord or Velocity documentation to set up IP forwarding, if you have not done so).\n" ) + ChatColorCode.getString3( "3) None of your user accounts are being randomly associated when checking /alts or /ipreport to view linked accounts on the same IP.\n" ) + ChatColorCode.getString3( "4) If you have an offline-mode network or permit non-premium users to join, please add your authentication or lobby server to ignored_iphistory_servers to prevent falsely associated accounts (since anyone can join any account).\n" ) + ChatColorCode.getString5("// WARNING WARNING WARNING WARNING WARNING //\n") + ChatColorCode.getString2( "If you acknowledge and accept this disclaimer, please type " + ChatColorCode.getString4("litebans accept") + ChatColorCode.getString2(" to enable automatic bans.") )),
   BAN_ALTS_GUARDRAIL_BAN_SELF(C_BAN, "alts_guardrail_ban_self", ChatColorCode.getString3("Error: Banning associated accounts for $playerName would also ban your account!\nNo additional players will be banned.")),
   BAN_ALTS_GUARDRAIL_BAN_MANY(C_BAN, "alts_guardrail_ban_many", ChatColorCode.getString5( "Warning: Banning this player will also ban $num players!\nThese players will be banned if you click here to confirm:\n$list\n {run: $command $commandArgs --confirm}" )),
   BAN_EXEMPT(C_BAN, "exempt", ChatColorCode.getString3("You can't ban $player!")),
   BAN_COOLDOWN(C_BAN, "cooldown", ChatColorCode.getString3( "You need to wait " + ChatColorCode.getString5("$duration") + ChatColorCode.getString3(" before you can use this command again.") )),
   BAN_RESPONSE(C_BAN, "response", ""),
   BAN_MODIFY_SUCCESS(C_BAN, "modify_success", ChatColorCode.getString2("Punishment modified successfully.")),
   BAN_DELETE_SUCCESS(C_BAN, "delete_success", ChatColorCode.getString2("Punishment deleted successfully.")),
   BAN_HIDDEN_IP(C_BAN, "ip_hidden", "[IP Address]"),
   UNBAN_USAGE(C_BAN, "unban_usage", ChatColorCode.RED + "Usage: $command <player>"),
   UNBAN_BROADCAST(C_BAN, "broadcast_unban", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "unbanned " + ChatColorCode.GREEN + "$playerName " + ChatColorCode.WHITE + "for '" + ChatColorCode.GREEN + "$removalReason" + ChatColorCode.WHITE + '\''),
   UNBAN_QUEUED(C_BAN, "unban_queued", ChatColorCode.RED + "Target is not banned!" + ChatColorCode.GOLD + " If they try to reconnect, they will be unbanned."),
   MUTE_USAGE(C_MUTE, "usage", ChatColorCode.RED + "Usage: $command [-s] <player> [time spec] [reason]"),
   MUTE_EXAMPLE(C_MUTE, "example", ChatColorCode.RED + "Example: $command Player 7d [reason]"),
   MUTE_BROADCAST(C_MUTE, "broadcast", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "muted " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   MUTE_BROADCAST_TEMP(C_MUTE, "broadcast_tempmute", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "tempmuted " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for $tempDuration for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   MUTE_BROADCAST_IP(C_MUTE, "broadcast_ip_mute", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "IP-muted " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   MUTE_BROADCAST_TEMP_IP(C_MUTE, "broadcast_temp_ip_mute", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "temp IP-muted " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for $tempDuration for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   MUTE_MESSAGE(C_MUTE, "message", ChatColorCode.DARK_RED + "You have been muted by $executor" + ChatColorCode.DARK_RED + " for " + ChatColorCode.RED + '\'' + ChatColorCode.DARK_RED + "$reason" + ChatColorCode.RED + "'" + ChatColorCode.DARK_RED + ".\n" + ChatColorCode.DARK_RED + "This mute will expire in $duration."),
   MUTE_MESSAGE_PERMANENT(C_MUTE, "message_permanent", ChatColorCode.DARK_RED + "You have been permanently muted by $executor" + ChatColorCode.DARK_RED + " for " + ChatColorCode.RED + '\'' + ChatColorCode.DARK_RED + "$reason" + ChatColorCode.RED + "'" + ChatColorCode.DARK_RED + ".\n" + ChatColorCode.DARK_RED + "This mute will not expire."),
   MUTE_ERROR_UNMUTE_FAIL(C_MUTE, "unmute_fail", ChatColorCode.RED + "Target is not muted!"),
   MUTE_ERROR_NO_OVERRIDE(C_MUTE, "no_override", ChatColorCode.RED + "Player is already muted!"),
   MUTE_PREVIOUS_MUTE_REMOVED(C_MUTE, "previous_mute_removed", ChatColorCode.GREEN + "Previous mute for $playerName removed."),
   MUTE_PREVIOUS_MUTE_EXISTING(C_MUTE, "previous_mute_existing", ChatColorCode.RED + "$playerName is already muted, and you do not have permission to replace existing mutes."),
   MUTE_EXEMPT(C_MUTE, "exempt", ChatColorCode.RED + "You can't mute $player!"),
   MUTE_NOTIFY(C_MUTE, "notification", ChatColorCode.RED + "$playerDisplayName tried to speak, but is muted."),
   MUTE_DISABLED(C_MUTE, "error_not_enabled", ChatColorCode.RED + "Muting is not enabled in the configuration!"),
   MUTE_RESPONSE(C_MUTE, "response", ""),
   UNMUTE_USAGE(C_MUTE, "unmute_usage", ChatColorCode.RED + "Usage: $command <player>"),
   UNMUTE_BROADCAST(C_MUTE, "broadcast_unmute", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "unmuted " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for '" + ChatColorCode.GREEN + "$removalReason" + ChatColorCode.WHITE + '\''),
   WARN_USAGE(C_WARN, "usage", ChatColorCode.RED + "Usage: $command [-s] <player> [reason]"),
   WARN_EXAMPLE(C_WARN, "example", ""),
   WARN_BROADCAST(C_WARN, "broadcast", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "warned " + ChatColorCode.GREEN + "$playerDisplayName " + ChatColorCode.WHITE + "for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   WARN_MESSAGE(C_WARN, "message", ChatColorCode.DARK_RED + "You have been warned by $executor" + ChatColorCode.DARK_RED + " for " + ChatColorCode.RED + '\'' + ChatColorCode.DARK_RED + "$reason" + ChatColorCode.RED + '\'' + ChatColorCode.DARK_RED + ".\n" + ChatColorCode.DARK_RED + "This warning will expire in $duration."),
   WARN_LIST_ENTRY(C_WARN, "list_entry", ChatColorCode.WHITE + " \\- Warned by $executor: '$reason" + ChatColorCode.WHITE + "'"),
   WARN_UNWARN_SUCCESS(C_WARN, "unwarn_response", ChatColorCode.GREEN + "Last warning removed for $player."),
   WARN_ERROR_UNWARN_FAIL(C_WARN, "unwarn_fail", ChatColorCode.RED + "Target has no warnings!"),
   WARN_EXEMPT(C_WARN, "exempt", ChatColorCode.RED + "You can't warn $player!"),
   WARN_RESPONSE(C_WARN, "response", ""),
   UNWARN_USAGE(C_WARN, "unwarn_usage", ChatColorCode.RED + "Usage: $command <player>"),
   UNWARN_BROADCAST(C_WARN, "broadcast_unwarn", ChatColorCode.GREEN + "$executor " + ChatColorCode.WHITE + "unwarned " + ChatColorCode.GREEN + "$playerDisplayName"),
   KICK_USAGE(C_KICK, "usage", ChatColorCode.RED + "$command <player> [reason]"),
   KICK_NO_MATCH(C_KICK, "no_match", ChatColorCode.RED + "Error: " + ChatColorCode.DARK_RED + "Player not found."),
   KICK_REQUEST(C_KICK, "kick_requested", ChatColorCode.GOLD + "Player $player is not online on this server. Cross-server kick has been requested."),
   KICK_REASON(C_KICK, "message", "Kicked by $executor: $reason"),
   KICK_REASON_EMPTY(C_KICK, "message_no_reason", "Kicked by $executor."),
   KICK_RESPONSE(C_KICK, "response", ChatColorCode.GOLD + "$playerDisplayName has been kicked."),
   KICK_BROADCAST(C_KICK, "broadcast", ChatColorCode.GREEN + "$playerDisplayName" + ChatColorCode.WHITE + " was kicked by " + ChatColorCode.GREEN + "$executor" + ChatColorCode.WHITE + " for '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + "'."),
   KICK_BROADCAST_REASON_EMPTY(C_KICK, "broadcast_no_reason", ChatColorCode.GREEN + "$playerDisplayName" + ChatColorCode.WHITE + " was kicked by " + ChatColorCode.GREEN + "$executor" + ChatColorCode.WHITE + '.'),
   KICK_EXEMPT(C_KICK, "exempt", ChatColorCode.RED + "You can't kick $player!"),
   HISTORY_USAGE(C_HISTORY, "usage", ChatColorCode.RED + "$command <player> [entries=10]"),
   HISTORY_START(C_HISTORY, "start", ChatColorCode.GREEN + "History for $target (Limit: $limit):"),
   HISTORY_BAN(C_HISTORY, "ban_entry", ChatColorCode.GREEN + " -- [" + ChatColorCode.WHITE + "$timeSince ago" + ChatColorCode.GREEN + "] --" + ChatColorCode.WHITE + "\n" + ChatColorCode.WHITE + " $name was " + ChatColorCode.RED + "banned " + ChatColorCode.WHITE + "by $executor: '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   HISTORY_MUTE(C_HISTORY, "mute_entry", ChatColorCode.GREEN + " -- [" + ChatColorCode.WHITE + "$timeSince ago" + ChatColorCode.GREEN + "] --" + ChatColorCode.WHITE + "\n" + ChatColorCode.WHITE + " $name was " + ChatColorCode.GRAY + "muted " + ChatColorCode.WHITE + "by $executor: '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   HISTORY_WARN(C_HISTORY, "warn_entry", ChatColorCode.GREEN + " -- [" + ChatColorCode.WHITE + "$timeSince ago" + ChatColorCode.GREEN + "] --" + ChatColorCode.WHITE + "\n" + ChatColorCode.WHITE + " $name was " + ChatColorCode.GOLD + "warned " + ChatColorCode.WHITE + "by $executor: '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   HISTORY_KICK(C_HISTORY, "kick_entry", ChatColorCode.GREEN + " -- [" + ChatColorCode.WHITE + "$timeSince ago" + ChatColorCode.GREEN + "] --" + ChatColorCode.WHITE + "\n" + ChatColorCode.WHITE + " $name was " + ChatColorCode.YELLOW + "kicked " + ChatColorCode.WHITE + "by $executor: '" + ChatColorCode.GREEN + "$reason" + ChatColorCode.WHITE + '\''),
   HISTORY_UNBAN(C_HISTORY, "unban_entry", "\n " + ChatColorCode.WHITE + "$name was " + ChatColorCode.GRAY + "unbanned " + ChatColorCode.WHITE + "by $executor: '" + ChatColorCode.GRAY + "$removalReason" + ChatColorCode.WHITE + '\''),
   HISTORY_UNMUTE(C_HISTORY, "unmute_entry", "\n " + ChatColorCode.WHITE + "$name was " + ChatColorCode.GRAY + "unmuted " + ChatColorCode.WHITE + "by $executor: '" + ChatColorCode.GRAY + "$removalReason" + ChatColorCode.WHITE + '\''),
   HISTORY_UNWARN(C_HISTORY, "unwarn_entry", "\n " + ChatColorCode.WHITE + "$name was " + ChatColorCode.GRAY + "unwarned " + ChatColorCode.WHITE + "by $executor: '" + ChatColorCode.GRAY + "$removalReason" + ChatColorCode.WHITE + '\''),
   HISTORY_SUFFIX_ACTIVE(C_HISTORY, "active_suffix", ChatColorCode.WHITE + " [" + ChatColorCode.RED + "Active" + ChatColorCode.WHITE + ']'),
   HISTORY_SUFFIX_ACTIVE_TEMP(C_HISTORY, "active_suffix_temp", ChatColorCode.WHITE + " [" + ChatColorCode.RED + "Active" + ChatColorCode.WHITE + "]\n" + ChatColorCode.WHITE + "Expires in $duration."),
   HISTORY_SUFFIX_EXPIRED(C_HISTORY, "expired_suffix", ChatColorCode.WHITE + " [" + ChatColorCode.DARK_GRAY + "Expired" + ChatColorCode.WHITE + ']'),
   HISTORY_ERROR_NO_LOGINRECORD(C_HISTORY, "error_no_loginrecord", ChatColorCode.RED + "User has not joined before."),
   HISTORY_ERROR_NO_USER(C_HISTORY, "error_no_user", ChatColorCode.RED + "User not found."),
   WARNINGS_USAGE(C_WARNINGS, "usage", ChatColorCode.RED + "$command <player>"),
   WARNINGS_START(C_WARNINGS, "start", ChatColorCode.GREEN + "Active warnings for $target:"),
   PRUNEHISTORY_USAGE(C_PRUNEHISTORY, "usage", ChatColorCode.RED + "$command <player> [duration]"),
   PRUNEHISTORY_MESSAGE(C_PRUNEHISTORY, "message", ChatColorCode.GREEN + "History pruned."),
   STAFFHISTORY_USAGE(C_STAFFHISTORY, "usage", ChatColorCode.RED + "$command <player> [entries=10]"),
   STAFFHISTORY_START(C_STAFFHISTORY, "start", ChatColorCode.GREEN + "Staff history for $target (Limit: $limit):"),
   STAFFROLLBACK_USAGE(C_STAFFROLLBACK, "usage", ChatColorCode.RED + "$command <player> [duration]"),
   STAFFROLLBACK_MESSAGE(C_STAFFROLLBACK, "message", ChatColorCode.GREEN + "Rollback completed, total entries removed: $amount"),
   BANLIST_START(C_BANLIST, "start", ChatColorCode.WHITE + "=== " + ChatColorCode.GREEN + "Page " + ChatColorCode.GOLD + "$page" + ChatColorCode.GREEN + " out of " + ChatColorCode.GOLD + "$total" + ChatColorCode.WHITE + " ==="),
   IPHISTORY_USAGE(C_IPHISTORY, "usage", ChatColorCode.RED + "$command <player> [entries=10]"),
   IPHISTORY_START(C_IPHISTORY, "start", ChatColorCode.GREEN + "Login history for $target (Limit: $limit):"),
   IPHISTORY_ENTRY(C_IPHISTORY, "entry", ChatColorCode.GREEN + " - [" + ChatColorCode.WHITE + "$date" + ChatColorCode.GREEN + ']' + ChatColorCode.WHITE + " $name" + ChatColorCode.GREEN + ':' + ChatColorCode.WHITE + " $ip"),
   IPHISTORY_ERROR_NO_HISTORY(C_IPHISTORY, "error_no_history", ChatColorCode.RED + "No history found."),
   NAMEHISTORY_START(C_NAMEHISTORY, "start", ChatColorCode.GREEN + "Name history for $target (Limit: $limit):"),
   NAMEHISTORY_USAGE(C_NAMEHISTORY, "usage", ChatColorCode.RED + "$command <player> [entries=10]"),
   NAMEHISTORY_ENTRY(C_NAMEHISTORY, "entry", ChatColorCode.GREEN + " - [" + ChatColorCode.WHITE + "$playerLoginDate" + ChatColorCode.GREEN + ']' + ChatColorCode.GOLD + " $playerName"),
   DUPEIP_USAGE(C_DUPEIP, "usage", ChatColorCode.RED + "$command <player>"),
   DUPEIP_START(C_DUPEIP, "start", ChatColorCode.WHITE + "Scanning " + ChatColorCode.GREEN + "$name" + ChatColorCode.WHITE + " on " + ChatColorCode.GREEN + "$ip" + ChatColorCode.WHITE + ".\n" + ChatColorCode.WHITE + '[' + ChatColorCode.GREEN + "Online" + ChatColorCode.WHITE + "] [" + ChatColorCode.GRAY + "Offline" + ChatColorCode.WHITE + "] [" + ChatColorCode.RED + "Banned" + ChatColorCode.WHITE + "] [" + ChatColorCode.YELLOW + "IPBanned" + ChatColorCode.WHITE + ']'),
   DUPEIP_START_NO_IP(C_DUPEIP, "start_no_ip", ChatColorCode.WHITE + "Scanning " + ChatColorCode.GRAY + "$name" + ChatColorCode.WHITE + ".\n" + ChatColorCode.WHITE + '[' + ChatColorCode.GREEN + "Online" + ChatColorCode.WHITE + "] [" + ChatColorCode.GRAY + "Offline" + ChatColorCode.WHITE + "] [" + ChatColorCode.RED + "Banned" + ChatColorCode.WHITE + "] [" + ChatColorCode.YELLOW + "IPBanned" + ChatColorCode.WHITE + ']'),
   DUPEIP_END(C_DUPEIP, "end", ""),
   DUPEIP_MULTIPLE_ADDRESSES(C_DUPEIP, "multiple_addresses", "[$num addresses]"),
   DUPEIP_SEPARATOR(C_DUPEIP, "separator", ChatColorCode.WHITE + ", "),
   DUPEIP_ONLINE(C_DUPEIP, "online", ChatColorCode.GREEN),
   DUPEIP_OFFLINE(C_DUPEIP, "offline", ChatColorCode.GRAY),
   DUPEIP_BANNED(C_DUPEIP, "banned", ChatColorCode.RED),
   DUPEIP_IP_BANNED(C_DUPEIP, "ip_banned", ChatColorCode.YELLOW),
   DUPEIP_MUTED(C_DUPEIP, "muted", ChatColorCode.GOLD),
   IPREPORT_START(C_IPREPORT, "start", ChatColorCode.WHITE + "Scanning " + ChatColorCode.GREEN + "$num" + ChatColorCode.WHITE + " online players.. " + '[' + ChatColorCode.GREEN + "Online" + ChatColorCode.WHITE + "] [" + ChatColorCode.GRAY + "Offline" + ChatColorCode.WHITE + "] [" + ChatColorCode.RED + "Banned" + ChatColorCode.WHITE + ']'),
   IPREPORT_ENTRY(C_IPREPORT, "entry", ChatColorCode.WHITE + "$player" + ChatColorCode.GREEN + ": $result"),
   CHECKBAN_USAGE(C_CHECKBAN, "usage", ChatColorCode.RED + "$command <player>"),
   CHECKBAN_NO_BAN(C_CHECKBAN, "no_ban", ChatColorCode.RED + "Target is not banned!"),
   CHECKBAN_BANNED(C_CHECKBAN, "banned", ChatColorCode.GREEN + "Target " + ChatColorCode.WHITE + '[' + ChatColorCode.GREEN + "$target" + ChatColorCode.WHITE + ']' + ChatColorCode.GREEN + " is banned:\n" + ChatColorCode.GREEN + "Banned by: $executor\n" + ChatColorCode.GREEN + "Reason: $reason" + ChatColorCode.GREEN + "\n" + ChatColorCode.GREEN + "Banned on: $dateStart\n" + ChatColorCode.GREEN + "Banned until: $dateEnd ($duration)\n&aBanned on server &6&a$serverOrigin&a, server scope: &6$serverScope\n" + ChatColorCode.GREEN + "IP ban: $ipban, silent: $silent, permanent: $permanent"),
   CHECKMUTE_USAGE(C_CHECKMUTE, "usage", ChatColorCode.RED + "$command <player>"),
   CHECKMUTE_NO_MUTE(C_CHECKMUTE, "no_mute", ChatColorCode.RED + "Target is not muted!"),
   CHECKMUTE_MUTED(C_CHECKMUTE, "muted", ChatColorCode.GREEN + "Target &f[&a$target&f]&a is muted:\n&aMuted by: $executor\n&aReason: $reason&r\n&aMuted on: $dateStart\n&aMuted until: $dateEnd ($duration)\n&aMuted on server &6$serverOrigin&a, server scope: &6$serverScope\n&aIP mute: $ipban, silent: $silent, permanent: $permanent"),
   CHECKWARN_USAGE(C_CHECKWARN, "usage", ChatColorCode.RED + "$command <player>"),
   CHECKWARN_NO_WARN(C_CHECKWARN, "no_warn", ChatColorCode.RED + "Target is not warned!"),
   CHECKWARN_WARNED(C_CHECKWARN, "warned", ChatColorCode.GREEN + "Target &f[&a$target&f]&a is warned:\n&aWarned by: $executor\n&aReason: $reason&r\n&aWarned on: $dateStart\n&aWarned until: $dateEnd ($duration)\n&aWarned on server &6$serverOrigin&a, server scope: &6$serverScope\n&aIP warning: $ipban, silent: $silent, permanent: $permanent"),
   LASTUUID_USAGE(C_LASTUUID, "usage", ChatColorCode.RED + "$command <player|IP>"),
   LASTUUID_MESSAGE(C_LASTUUID, "message", ChatColorCode.WHITE + "Last UUID for " + ChatColorCode.GREEN + "$name" + ChatColorCode.WHITE + ": " + ChatColorCode.GREEN + "$uuid"),
   GEOIP_USAGE(C_GEOIP, "usage", ChatColorCode.RED + "$command <player|IP>"),
   GEOIP_MESSAGE(C_GEOIP, "message", ChatColorCode.GREEN + "$target" + ChatColorCode.WHITE + " is from: " + ChatColorCode.GREEN + "$result"),
   GEOIP_DISABLED(C_GEOIP, "error_disabled", ChatColorCode.RED + "GeoIP support is not enabled in the configuration!"),
   GEOIP_UNAVAILABLE(C_GEOIP, "error_unavailable", ChatColorCode.RED + "GeoIP support is currently unavailable, has it been downloaded yet?"),
   GEOIP_NONE_FOUND(C_GEOIP, "error_not_found", ChatColorCode.RED + "GeoIP information for $target not found."),
   LOCKDOWN_USAGE(C_LOCKDOWN, "usage", ChatColorCode.RED + "$command <reason> | $command end"),
   LOCKDOWN_MESSAGE(C_LOCKDOWN, "message", ChatColorCode.RED + "Server lockdown activated (reason: \"$reason" + ChatColorCode.RED + "\")"),
   LOCKDOWN_END(C_LOCKDOWN, "stopped", ChatColorCode.GREEN + "Lockdown has been deactivated."),
   LOCKDOWN_KICK_MESSAGE(C_LOCKDOWN, "kick_message", "Server lockdown active, try again later.\\nReason: $reason"),
   LOCKDOWN_KICK_MESSAGE_GLOBAL(C_LOCKDOWN, "kick_message_global", "Network lockdown active, try again later.\\nReason: $reason"),
   LOCKDOWN_ERROR_NOT_ACTIVE(C_LOCKDOWN, "error_not_active", ChatColorCode.RED + "Lockdown is not active!"),
   RELOAD_SUCCESS(C_LITEBANS, "reload_success", ChatColorCode.GREEN + "LiteBans reloaded successfully."),
   RELOAD_FAIL(C_LITEBANS, "reload_fail", ChatColorCode.RED + "Reload failed."),
   RELOAD_FAIL_CONNECT(C_LITEBANS, "reload_fail_connect", ChatColorCode.GREEN + "Litebans reloaded. " + ChatColorCode.RED + "Failed to connect to database."),
   RELOAD_FAIL_CONFIG(C_LITEBANS, "reload_fail_config", ChatColorCode.RED + "[LiteBans] " + ChatColorCode.DARK_RED + "config.yml is not valid and could not be loaded, the default configuration will be used.\\n" + ChatColorCode.RED + "Please check the server console for more information."),
   RELOAD_FAIL_CONFIG_MESSAGES(C_LITEBANS, "reload_fail_messages", ChatColorCode.RED + "[LiteBans] " + ChatColorCode.DARK_RED + "messages.yml is not valid and could not be loaded, default messages will be used.\\n" + ChatColorCode.RED + "Please check the server console for more information."),
   RELOAD_FAIL_CONFIG_TEMPLATES(C_LITEBANS, "reload_fail_templates", ChatColorCode.RED + "[LiteBans] " + ChatColorCode.DARK_RED + "templates.yml is not valid and could not be loaded. Configured templates will be unavailable until this has been fixed.\\n" + ChatColorCode.RED + "Please check the server console for more information."),
   FIXHISTORY_START(C_LITEBANS, "fix_history", ChatColorCode.GREEN + "Fixing history for table $table..."),
   FIXHISTORY_END(C_LITEBANS, "fix_history_result", ChatColorCode.GREEN + "Added $amount entries."),
   FIXHISTORY_OFFLINE_UUIDS(C_LITEBANS, "fix_history_offline_uuids", ChatColorCode.RED + "$amount UUIDs were not fetched from Mojang since they are offline-mode UUIDs."),
   IMPORT_USAGE(C_LITEBANS, "import_usage", ChatColorCode.RED + "$command import start"),
   IMPORT_START(C_LITEBANS, "import_start", ChatColorCode.GREEN + "Importing from $db, this might take a while..."),
   IMPORT_FINISH(C_LITEBANS, "import_finish", ChatColorCode.GREEN + "Import finished successfully. $bans bans imported, $ipbans IP-bans."),
   IMPORT_FINISH_LITEBANS(C_LITEBANS, "import_finish_litebans", ChatColorCode.GREEN + "Import finished successfully. Added $amount entries to the database."),
   IMPORT_FAIL(C_LITEBANS, "import_fail", ChatColorCode.RED + "Import failed. Check console."),
   IMPORT_UNSUPPORTED(C_LITEBANS, "import_unsupported", ChatColorCode.RED + "Importing from '$name' is not supported yet!"),
   ALLOW_USAGE(C_LITEBANS, "allow_usage", ChatColorCode.RED + "Usage: /litebans allow <add/remove/check> <user>"),
   ALLOW_ADDED(C_LITEBANS, "allow_added", ChatColorCode.YELLOW + "Player " + ChatColorCode.GOLD + "$playerName" + ChatColorCode.YELLOW + " has been " + ChatColorCode.GREEN + "allowed to join the server" + ChatColorCode.YELLOW + "."),
   ALLOW_REMOVED(C_LITEBANS, "allow_removed", ChatColorCode.YELLOW + "Player " + ChatColorCode.GOLD + "$playerName" + ChatColorCode.YELLOW + " has been " + ChatColorCode.RED + "removed from the allow list" + ChatColorCode.YELLOW + "."),
   ALLOW_CHECK_TRUE(C_LITEBANS, "allow_check_true", ChatColorCode.GREEN + "Player " + ChatColorCode.GOLD + "$playerName" + ChatColorCode.GREEN + " is allowed to join the server."),
   ALLOW_CHECK_FALSE(C_LITEBANS, "allow_check_false", ChatColorCode.RED + "Player " + ChatColorCode.GOLD + "$playerName" + ChatColorCode.RED + " is not allowed to join the server."),
   ALLOW_ERROR_ADD(C_LITEBANS, "allow_error_add", ChatColorCode.RED + "Error: $playerName is already allowed to join the server."),
   ALLOW_ERROR_REMOVE(C_LITEBANS, "allow_error_remove", ChatColorCode.RED + "Error: $playerName is not allowed to join the server."),
   RESET_DATABASE_CONFIRM(C_LITEBANS, "reset_database_confirm_quoted", "\n" + ChatColorCode.RED + "WARNING: " + ChatColorCode.YELLOW + "This command will reset your database, removing all active and inactive punishments permanently.\n" + ChatColorCode.RED + "WARNING: " + ChatColorCode.UNDERLINE + "This operation is irreversible without restoring a database backup.\n" + ChatColorCode.YELLOW + "Run '" + ChatColorCode.RED + "litebans $commandArgs confirm" + ChatColorCode.YELLOW + "' to continue."),
   RESET_DATABASE_RESULT(C_LITEBANS, "reset_database_result", ChatColorCode.GREEN + "Removed " + ChatColorCode.YELLOW + "$amount " + ChatColorCode.GREEN + "$type from " + ChatColorCode.YELLOW + "server:$serverScope" + ChatColorCode.GREEN + "."),
   DURATION("duration"),
   EXPIRED(DURATION, "expired", "expired"),
   FOREVER(DURATION, "forever", "forever"),
   YEAR(DURATION, "year", "year"),
   YEARS(DURATION, "years", "years"),
   MONTH(DURATION, "month", "month"),
   MONTHS(DURATION, "months", "months"),
   WEEK(DURATION, "week", "week"),
   WEEKS(DURATION, "weeks", "weeks"),
   DAY(DURATION, "day", "day"),
   DAYS(DURATION, "days", "days"),
   HOUR(DURATION, "hour", "hour"),
   HOURS(DURATION, "hours", "hours"),
   MINUTE(DURATION, "minute", "minute"),
   MINUTES(DURATION, "minutes", "minutes"),
   SECOND(DURATION, "second", "second"),
   SECONDS(DURATION, "seconds", "seconds"),
   DURATION_FORMAT(DURATION, "format", "%d %s"),
   DURATION_SEPARATOR(DURATION, "separator", ", "),
   STRINGS("strings"),
   STRINGS_GLOBAL(STRINGS, "global", "global"),
   STRINGS_NULL(STRINGS, "null", "undefined"),
   STRINGS_TRUE(STRINGS, "true", "yes"),
   STRINGS_FALSE(STRINGS, "false", "no"),
   WEB("webhook"),
   WEB_EVENT(WEB, "events"),
   WEB_BAN(WEB_EVENT, "embed_ban"),
   WEB_BAN_TITLE(WEB_BAN, "title", "Banned"),
   WEB_BAN_COLOR(WEB_BAN, "color", "#CC2200"),
   WEB_BAN_CONTENT(WEB_BAN, "content", "$playerName has been banned!\n• Banned by: $executor\n• Duration: $duration\n• Reason: $reason"),
   WEB_BAN_FIELDS(WEB_BAN, "fields", ""),
   WEB_BAN_THUMB(WEB_BAN, "thumbnail", ""),
   WEB_UNBAN(WEB_EVENT, "embed_unban"),
   WEB_UNBAN_TITLE(WEB_UNBAN, "title", "Unbanned"),
   WEB_UNBAN_COLOR(WEB_UNBAN, "color", "#727374"),
   WEB_UNBAN_CONTENT(WEB_UNBAN, "content", "$playerName has been unbanned!\n• Unbanned by: $executor\n• Reason: $removalReason"),
   WEB_UNBAN_FIELDS(WEB_UNBAN, "fields", ""),
   WEB_UNBAN_THUMB(WEB_UNBAN, "thumbnail", ""),
   WEB_MUTE(WEB_EVENT, "embed_mute"),
   WEB_MUTE_TITLE(WEB_MUTE, "title", "Muted"),
   WEB_MUTE_COLOR(WEB_MUTE, "color", "#BB9000"),
   WEB_MUTE_CONTENT(WEB_MUTE, "content", "$playerName has been muted!\n• Muted by: $executor\n• Duration: $duration\n• Reason: $reason"),
   WEB_MUTE_FIELDS(WEB_MUTE, "fields", ""),
   WEB_MUTE_THUMB(WEB_MUTE, "thumbnail", ""),
   WEB_UNMUTE(WEB_EVENT, "embed_unmute"),
   WEB_UNMUTE_TITLE(WEB_UNMUTE, "title", "Unmuted"),
   WEB_UNMUTE_COLOR(WEB_UNMUTE, "color", "#727374"),
   WEB_UNMUTE_CONTENT(WEB_UNMUTE, "content", "$playerName has been unmuted!\n• Unmuted by: $executor\n• Reason: $removalReason"),
   WEB_UNMUTE_FIELDS(WEB_UNMUTE, "fields", ""),
   WEB_UNMUTE_THUMB(WEB_UNMUTE, "thumbnail", ""),
   WEB_WARN(WEB_EVENT, "embed_warn"),
   WEB_WARN_TITLE(WEB_WARN, "title", "Warned"),
   WEB_WARN_COLOR(WEB_WARN, "color", "#FFAA11"),
   WEB_WARN_CONTENT(WEB_WARN, "content", "$playerName has been warned!\n• Warned by: $executor\n• Duration: $duration\n• Reason: $reason"),
   WEB_WARN_FIELDS(WEB_WARN, "fields", ""),
   WEB_WARN_THUMB(WEB_WARN, "thumbnail", ""),
   WEB_UNWARN(WEB_EVENT, "embed_unwarn"),
   WEB_UNWARN_TITLE(WEB_UNWARN, "title", "Unwarned"),
   WEB_UNWARN_COLOR(WEB_UNWARN, "color", "#727374"),
   WEB_UNWARN_CONTENT(WEB_UNWARN, "content", "$playerName has been unwarned!\n• Unwarned by: $executor\n• Reason: $removalReason"),
   WEB_UNWARN_FIELDS(WEB_UNWARN, "fields", ""),
   WEB_UNWARN_THUMB(WEB_UNWARN, "thumbnail", ""),
   WEB_KICK(WEB_EVENT, "embed_kick"),
   WEB_KICK_TITLE(WEB_KICK, "title", "Kicked"),
   WEB_KICK_COLOR(WEB_KICK, "color", "#999900"),
   WEB_KICK_CONTENT(WEB_KICK, "content", "$playerName has been kicked!\n• Kicked by: $executor\n• Reason: $reason"),
   WEB_KICK_FIELDS(WEB_KICK, "fields", ""),
   WEB_KICK_THUMB(WEB_KICK, "thumbnail", ""),
   WEB_ALTS(WEB_EVENT, "embed_alts"),
   WEB_ALTS_TITLE(WEB_ALTS, "title", "Scanned $playerName"),
   WEB_ALTS_COLOR(WEB_ALTS, "color", "#999999"),
   WEB_ALTS_THUMB(WEB_ALTS, "thumbnail", "");
   public final String string;
   @Nullable
   public CharSequence charSequence;
   private static final Message[] messageArray = getMessageArray();

   public static Message getMessage(String var0) {
      return Enum.valueOf(Message.class, var0);
   }

   private Message(Message var3, String var4) {
      this(var3, var4, null);
   }

   private Message(Message var3, String var4, @Nullable CharSequence var5) {
      this(var3.getString() + '.' + var4, var5);
   }

   private Message(String var3) {
      this.string = var3;
   }

   private Message(String var3, @Nullable CharSequence var4) {
      this(var3);
      this.handleCharSequence(var4);
   }

   public static String getString(Object var0, boolean var1, boolean var2) {
      if (var0 instanceof String) {
         return (String)var0;
      } else if (var0 instanceof CharSequence) {
         return var0.toString();
      } else if (var0 instanceof Character) {
         return new String(new char[]{(Character)var0});
      } else if (var1) {
         return var2 ? var0.toString().trim() : var0.toString();
      } else {
         return null;
      }
   }

   public static Message getMessage2(String var0) {
      var0 = var0.toUpperCase(Locale.ROOT);

      for (Message var4 : values()) {
         if (var4.name().equals(var0)) {
            return var4;
         }
      }

      throw new AssertionError(var0);
   }

   public static String getString(String var0) {
      return getMessage2(var0).toString();
   }

   @NotNull
   public static CharSequence getCharSequence(@NotNull PunishmentType var0) {
      switch (MessageWhenMappings.switchMap[var0.getPunishmentType().ordinal()]) {
         case 1:
            return HISTORY_BAN;
         case 2:
            return HISTORY_MUTE;
         case 3:
            return HISTORY_WARN;
         case 4:
            return HISTORY_KICK;
         default:
            throw new AssertionError();
      }
   }

   @NotNull
   public static String getString(CharSequence var0, boolean var1) {
      String var2 = ChatColorCode.getString('&', var0.toString().replace("\\n", "\n"));
      if (var1) {
         var2 = JsonMessageParser.formatSplitter.getString(var2);
      }

      if (var2.contains("\n{hoverText:")) {
         var2 = var2.replace("\n{hoverText:", "\n {hoverText:");
      }

      if (var2.contains("\n{run:")) {
         var2 = var2.replace("\n{run:", "\n {run:");
      }

      if (var2.contains("\n{url:")) {
         var2 = var2.replace("\n{url:", "\n {url:");
      }

      return var2;
   }

   @Nullable
   public static String getString(Message var0, @Nullable CharSequence var1) {
      if (var1 == null) {
         return null;
      } else {
         if (!var0.string.startsWith("webhook") && var1.toString().contains("#$id")) {
            var1 = var1.toString().replace("#$id", "#&b$id");
         }

         return EntryPlaceholders.getString((CharSequence)var1);
      }
   }

   public String getString(String var1, @Nullable Object var2) {
      return this.getString(EntryPlaceholders.getCharSequence(var1), var2);
   }

   public String getString(CharSequence var1) {
      return this + "\n" + var1;
   }

   @Override
   public char charAt(int var1) {
      return this.charSequence == null ? '?' : this.charSequence.charAt(var1);
   }

   public boolean isEmpty() {
      return this.length() == 0;
   }

   @Override
   public int length() {
      return this.charSequence == null ? 0 : this.charSequence.length();
   }

   @NotNull
   public String getString(CharSequence var1, @Nullable Object var2) {
      Object var3 = var2;
      if (var2 == null) {
         var3 = "";
      }

      return this.charSequence.toString().replace(var1.toString(), var3.toString());
   }

   public void handleCharSequence(@Nullable CharSequence var1) {
      String var2 = var1 == null ? null : getString(var1.toString(), false);
      var2 = getString(this, var2);
      this.charSequence = var2;
   }

   @Override
   public CharSequence subSequence(int var1, int var2) {
      return this.charSequence.subSequence(var1, var2);
   }

   @Override
   public String toString() {
      return String.valueOf(this.charSequence);
   }

   public String getString() {
      return this.string;
   }

   private static final Message[] getMessageArray() {
      return new Message[]{
         CMD,
         C_BAN,
         C_MUTE,
         C_WARN,
         C_KICK,
         C_HISTORY,
         C_STAFFHISTORY,
         C_WARNINGS,
         C_PRUNEHISTORY,
         C_STAFFROLLBACK,
         C_BANLIST,
         C_IPHISTORY,
         C_NAMEHISTORY,
         C_DUPEIP,
         C_IPREPORT,
         C_CHECKBAN,
         C_CHECKMUTE,
         C_CHECKWARN,
         C_LASTUUID,
         C_LOCKDOWN,
         C_GEOIP,
         C_LITEBANS,
         HEX_FORMATS,
         DATE_FORMAT,
         DEFAULT_BAN_REASON,
         DEFAULT_MUTE_REASON,
         DEFAULT_UNBAN_REASON,
         DEFAULT_UNMUTE_REASON,
         DEFAULT_UNWARN_REASON,
         BANNED_MESSAGE_BASE,
         BANNED_MESSAGE,
         BANNED_MESSAGE_PERMANENT,
         BANNED_MESSAGE_APPEAL,
         BANNED_MESSAGE_GEOIP,
         BANNED_MESSAGE_SWITCH,
         BANNED_MESSAGE_SWITCH_TEMP,
         MUTED,
         MUTED_PERMANENT,
         WARNED_JOIN_START,
         WARNED_JOIN_ENTRY,
         NOTIFY,
         NOTIFY_BANNED_PLAYER_JOIN,
         NOTIFY_BANNED_BLACKLIST,
         ERROR_NO_PERMISSION,
         ERROR_NO_TEMPLATE,
         ERROR_NO_REASON,
         ERROR_NO_REASON_REMOVAL,
         ERROR_NO_DATABASE,
         ERROR_NO_UUID,
         ERROR_NO_DURATION,
         ERROR_STACK_OVERFLOW,
         ERROR_CONSOLE,
         ERROR_DURATION_LIMIT,
         ERROR_INTERNAL,
         BAN_USAGE,
         BAN_EXAMPLE,
         BAN_SILENT_PREFIX,
         BAN_BROADCAST,
         BAN_BROADCAST_TEMP,
         BAN_BROADCAST_IP,
         BAN_BROADCAST_TEMP_IP,
         BAN_PREVIOUS_BAN_REMOVED,
         BAN_PREVIOUS_BAN_EXISTING,
         BAN_ERROR_UNBAN_FAIL,
         BAN_ERROR_NO_OVERRIDE,
         BAN_ALTS_GUARDRAIL_DISCLAIMER,
         BAN_ALTS_GUARDRAIL_BAN_SELF,
         BAN_ALTS_GUARDRAIL_BAN_MANY,
         BAN_EXEMPT,
         BAN_COOLDOWN,
         BAN_RESPONSE,
         BAN_MODIFY_SUCCESS,
         BAN_DELETE_SUCCESS,
         BAN_HIDDEN_IP,
         UNBAN_USAGE,
         UNBAN_BROADCAST,
         UNBAN_QUEUED,
         MUTE_USAGE,
         MUTE_EXAMPLE,
         MUTE_BROADCAST,
         MUTE_BROADCAST_TEMP,
         MUTE_BROADCAST_IP,
         MUTE_BROADCAST_TEMP_IP,
         MUTE_MESSAGE,
         MUTE_MESSAGE_PERMANENT,
         MUTE_ERROR_UNMUTE_FAIL,
         MUTE_ERROR_NO_OVERRIDE,
         MUTE_PREVIOUS_MUTE_REMOVED,
         MUTE_PREVIOUS_MUTE_EXISTING,
         MUTE_EXEMPT,
         MUTE_NOTIFY,
         MUTE_DISABLED,
         MUTE_RESPONSE,
         UNMUTE_USAGE,
         UNMUTE_BROADCAST,
         WARN_USAGE,
         WARN_EXAMPLE,
         WARN_BROADCAST,
         WARN_MESSAGE,
         WARN_LIST_ENTRY,
         WARN_UNWARN_SUCCESS,
         WARN_ERROR_UNWARN_FAIL,
         WARN_EXEMPT,
         WARN_RESPONSE,
         UNWARN_USAGE,
         UNWARN_BROADCAST,
         KICK_USAGE,
         KICK_NO_MATCH,
         KICK_REQUEST,
         KICK_REASON,
         KICK_REASON_EMPTY,
         KICK_RESPONSE,
         KICK_BROADCAST,
         KICK_BROADCAST_REASON_EMPTY,
         KICK_EXEMPT,
         HISTORY_USAGE,
         HISTORY_START,
         HISTORY_BAN,
         HISTORY_MUTE,
         HISTORY_WARN,
         HISTORY_KICK,
         HISTORY_UNBAN,
         HISTORY_UNMUTE,
         HISTORY_UNWARN,
         HISTORY_SUFFIX_ACTIVE,
         HISTORY_SUFFIX_ACTIVE_TEMP,
         HISTORY_SUFFIX_EXPIRED,
         HISTORY_ERROR_NO_LOGINRECORD,
         HISTORY_ERROR_NO_USER,
         WARNINGS_USAGE,
         WARNINGS_START,
         PRUNEHISTORY_USAGE,
         PRUNEHISTORY_MESSAGE,
         STAFFHISTORY_USAGE,
         STAFFHISTORY_START,
         STAFFROLLBACK_USAGE,
         STAFFROLLBACK_MESSAGE,
         BANLIST_START,
         IPHISTORY_USAGE,
         IPHISTORY_START,
         IPHISTORY_ENTRY,
         IPHISTORY_ERROR_NO_HISTORY,
         NAMEHISTORY_START,
         NAMEHISTORY_USAGE,
         NAMEHISTORY_ENTRY,
         DUPEIP_USAGE,
         DUPEIP_START,
         DUPEIP_START_NO_IP,
         DUPEIP_END,
         DUPEIP_MULTIPLE_ADDRESSES,
         DUPEIP_SEPARATOR,
         DUPEIP_ONLINE,
         DUPEIP_OFFLINE,
         DUPEIP_BANNED,
         DUPEIP_IP_BANNED,
         DUPEIP_MUTED,
         IPREPORT_START,
         IPREPORT_ENTRY,
         CHECKBAN_USAGE,
         CHECKBAN_NO_BAN,
         CHECKBAN_BANNED,
         CHECKMUTE_USAGE,
         CHECKMUTE_NO_MUTE,
         CHECKMUTE_MUTED,
         CHECKWARN_USAGE,
         CHECKWARN_NO_WARN,
         CHECKWARN_WARNED,
         LASTUUID_USAGE,
         LASTUUID_MESSAGE,
         GEOIP_USAGE,
         GEOIP_MESSAGE,
         GEOIP_DISABLED,
         GEOIP_UNAVAILABLE,
         GEOIP_NONE_FOUND,
         LOCKDOWN_USAGE,
         LOCKDOWN_MESSAGE,
         LOCKDOWN_END,
         LOCKDOWN_KICK_MESSAGE,
         LOCKDOWN_KICK_MESSAGE_GLOBAL,
         LOCKDOWN_ERROR_NOT_ACTIVE,
         RELOAD_SUCCESS,
         RELOAD_FAIL,
         RELOAD_FAIL_CONNECT,
         RELOAD_FAIL_CONFIG,
         RELOAD_FAIL_CONFIG_MESSAGES,
         RELOAD_FAIL_CONFIG_TEMPLATES,
         FIXHISTORY_START,
         FIXHISTORY_END,
         FIXHISTORY_OFFLINE_UUIDS,
         IMPORT_USAGE,
         IMPORT_START,
         IMPORT_FINISH,
         IMPORT_FINISH_LITEBANS,
         IMPORT_FAIL,
         IMPORT_UNSUPPORTED,
         ALLOW_USAGE,
         ALLOW_ADDED,
         ALLOW_REMOVED,
         ALLOW_CHECK_TRUE,
         ALLOW_CHECK_FALSE,
         ALLOW_ERROR_ADD,
         ALLOW_ERROR_REMOVE,
         RESET_DATABASE_CONFIRM,
         RESET_DATABASE_RESULT,
         DURATION,
         EXPIRED,
         FOREVER,
         YEAR,
         YEARS,
         MONTH,
         MONTHS,
         WEEK,
         WEEKS,
         DAY,
         DAYS,
         HOUR,
         HOURS,
         MINUTE,
         MINUTES,
         SECOND,
         SECONDS,
         DURATION_FORMAT,
         DURATION_SEPARATOR,
         STRINGS,
         STRINGS_GLOBAL,
         STRINGS_NULL,
         STRINGS_TRUE,
         STRINGS_FALSE,
         WEB,
         WEB_EVENT,
         WEB_BAN,
         WEB_BAN_TITLE,
         WEB_BAN_COLOR,
         WEB_BAN_CONTENT,
         WEB_BAN_FIELDS,
         WEB_BAN_THUMB,
         WEB_UNBAN,
         WEB_UNBAN_TITLE,
         WEB_UNBAN_COLOR,
         WEB_UNBAN_CONTENT,
         WEB_UNBAN_FIELDS,
         WEB_UNBAN_THUMB,
         WEB_MUTE,
         WEB_MUTE_TITLE,
         WEB_MUTE_COLOR,
         WEB_MUTE_CONTENT,
         WEB_MUTE_FIELDS,
         WEB_MUTE_THUMB,
         WEB_UNMUTE,
         WEB_UNMUTE_TITLE,
         WEB_UNMUTE_COLOR,
         WEB_UNMUTE_CONTENT,
         WEB_UNMUTE_FIELDS,
         WEB_UNMUTE_THUMB,
         WEB_WARN,
         WEB_WARN_TITLE,
         WEB_WARN_COLOR,
         WEB_WARN_CONTENT,
         WEB_WARN_FIELDS,
         WEB_WARN_THUMB,
         WEB_UNWARN,
         WEB_UNWARN_TITLE,
         WEB_UNWARN_COLOR,
         WEB_UNWARN_CONTENT,
         WEB_UNWARN_FIELDS,
         WEB_UNWARN_THUMB,
         WEB_KICK,
         WEB_KICK_TITLE,
         WEB_KICK_COLOR,
         WEB_KICK_CONTENT,
         WEB_KICK_FIELDS,
         WEB_KICK_THUMB,
         WEB_ALTS,
         WEB_ALTS_TITLE,
         WEB_ALTS_COLOR,
         WEB_ALTS_THUMB
      };
   }
}
