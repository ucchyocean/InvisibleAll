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
        
        boolean isOn = true;
        
        if ( args.length >= 1 && args[0].equalsIgnoreCase("off") ) {
            isOn = false;
        }
        
        if ( !(sender instanceof Player) ) {
            sender.sendMessage("このコマンドはゲーム内からしか実行できません。");
            return true;
        }
        Player player = (Player)sender;
        
        Player[] otherPlayers = getServer().getOnlinePlayers();
        
        if ( isOn ) {
            // 全てのプレイヤーを非表示にする
            for ( Player other : otherPlayers ) {
                player.hidePlayer(other);
            }
            
            // コマンドを実行したユーザーを記録する
            if ( !commandOnPlayers.contains(player.getName()) ) {
                commandOnPlayers.add(player.getName());
            }
            
            sender.sendMessage("他のプレイヤーを全員非表示に設定しました。");
            return true;
            
        } else {
            // 全てのプレイヤーを表示にする
            for ( Player other : otherPlayers ) {
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
        
        // 既にコマンドを実行しているプレイヤーからは、非表示に設定する
        Player logined = event.getPlayer();
        
        for ( String name : commandOnPlayers ) {
            Player commanded = getServer().getPlayerExact(name);
            if ( commanded != null ) {
                commanded.hidePlayer(logined);
            }
        }
    }
}
