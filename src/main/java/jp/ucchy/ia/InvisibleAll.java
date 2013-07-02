/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package jp.ucchy.ia;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * インビジブルオールプラグイン
 * @author ucchy
 */
public class InvisibleAll extends JavaPlugin implements Listener {

    private static ArrayList<String> commandOnPlayers;
    
    /**
     * プラグイン有効時のイベント
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        // 変数の初期化（reload時は初期化しない）
        if ( commandOnPlayers == null ) {
            commandOnPlayers = new ArrayList<String>();
        }
        
        // イベントの登録
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * コマンド実行時のイベント
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {
        
        if ( !(sender instanceof Player) ) {
            sender.sendMessage("このコマンドはゲーム内からしか実行できません。");
            return true;
        }
        Player player = (Player)sender;
        
        // mute の実行
        if ( args.length >= 1 && args[0].equalsIgnoreCase("mute") ) {
            if ( args.length == 1 ) {
                sender.sendMessage("muteする場合は、非表示にするプレイヤーを指定してください。");
                sender.sendMessage("usage: /" + label + " mute (PlayerName)");
                return true;
            }
            
            for ( int i=1; i<args.length; i++ ) {
                Player target = getServer().getPlayerExact(args[i]);
                if ( target != null ) {
                    if ( target.isOp() ) {
                        sender.sendMessage(target.getName() + "さんはOPなので非表示にできません。");
                    } else if ( player.canSee(target) ) {
                        player.hidePlayer(target);
                        sender.sendMessage(target.getName() + "さんを非表示にしました。");
                    } else {
                        player.showPlayer(target);
                        sender.sendMessage(target.getName() + "さんを表示しました。");
                    }
                } else {
                    sender.sendMessage(args[i] + "というプレイヤーは見つかりません。");
                }
            }
            
            return true;
        } 
        
        
        // mute 以外のコマンドの実行
        
        boolean isOn = true;
        
        if ( args.length >= 1 && args[0].equalsIgnoreCase("off") ) {
            isOn = false;
        }
        
        Player[] onlinePlayers = getServer().getOnlinePlayers();
        
        if ( isOn ) {
            // 全てのプレイヤーを非表示にする
            for ( Player other : onlinePlayers ) {
                if ( !other.isOp() ) {
                    player.hidePlayer(other);
                }
            }
            
            // コマンドを実行したユーザーを記録する
            if ( !commandOnPlayers.contains(player.getName()) ) {
                commandOnPlayers.add(player.getName());
            }
            
            sender.sendMessage("他のプレイヤーを全員非表示に設定しました。");
            return true;
            
        } else {
            // 全てのプレイヤーを表示にする
            for ( Player other : onlinePlayers ) {
                player.showPlayer(other);
            }
            
            // コマンドを実行したユーザーから削除する
            if ( commandOnPlayers.contains(player.getName()) ) {
                commandOnPlayers.remove(player.getName());
            }
            
            sender.sendMessage("他のプレイヤーを全員表示に設定しました。");
            return true;
        }
    }
    
    /**
     * プレイヤー参加時のイベント
     * @param event 
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        Player logined = event.getPlayer();
        
        if ( !logined.isOp() ) {
            // 非OPの場合、既にコマンドを実行しているプレイヤーからは、非表示に設定する
            for ( String name : commandOnPlayers ) {
                Player commanded = getServer().getPlayerExact(name);
                if ( commanded != null ) {
                    commanded.hidePlayer(logined);
                }
            }
            
        } else {
            // OPの場合、全てのプレイヤーから表示されるように設定する
            Player[] onlinePlayers = getServer().getOnlinePlayers();
            for ( Player other : onlinePlayers ) {
                other.showPlayer(logined);
            }
        }
    }
}
