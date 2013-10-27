package net.digiex.magiccarpet.commands;

import net.digiex.magiccarpet.Carpet;
import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Storage;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.3 Copyright (C) 2012-2013 Android, Celtic Minstrel, xzKinGzxBuRnzx
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class Light implements CommandExecutor {

	private final MagicCarpet plugin;

	public Light(MagicCarpet plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getLogger().info("Sorry, only players can use the carpet!");
			return true;
		}
		Player player = (Player) sender;
		if (plugin.getVault().isEnabled()) {
			if (plugin.getMCConfig().getDefaultChargeTimeBased()) {
				if (getCarpets().getTime(player) <= 0L) {
					player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
					return true;
				}
			} else {
				if (!getCarpets().hasPaidFee(player)) {
					player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb");
					return true;
				}
			}
		} else {
			if (!canLight(player)) {
				player.sendMessage("You do not have permission to use the magic light.");
				return true;
			}
		}
		Carpet carpet = getCarpets().getCarpet(player);
		if (carpet == null || !carpet.isVisible()) {
			player.sendMessage("You do not have a carpet yet, use /mc");
			return true;
		}
		if (args.length < 1) {
			if (getCarpets().hasLight(player)) {
				carpet.lightOff();
			} else {
				carpet.lightOn();
			}
		} else {
			if (getCarpets().hasLight(player)) {
				String word = "";
				for (String a : args) {
					if (word.isEmpty()) {
						word = a;
					} else {
						word += " " + a;
					}
				}
				Material m = Material.getMaterial(word.toUpperCase().replace(
						" ", "_"));
				if (m != null) {
					carpet.setLight(m);
				} else {
					player.sendMessage("Material error; Material may be entered as JACK_O_LANTERN or jack o lantern");
				}
			} else {
				player.sendMessage("You have not enabled the magic light yet.");
			}
		}
		return true;
	}

	private Storage getCarpets() {
		return plugin.getCarpets();
	}

	private boolean canLight(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player
				.hasPermission("magiccarpet.ml");
	}
}
