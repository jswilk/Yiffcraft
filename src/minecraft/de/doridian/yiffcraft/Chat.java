package de.doridian.yiffcraft;

import de.doridian.yiffcraft.commands.BaseCommand;
import net.minecraft.src.Packet3Chat;
import org.getspout.spout.client.SpoutClient;

import java.util.HashMap;
import java.util.regex.Pattern;

public final class Chat
{
	public static HashMap<String,BaseCommand> commands = new HashMap<String,BaseCommand>();
	
	public static final char PREFIX = '-';
	
	public static void init()
	{
		commands.clear();
		
		commands.put("help",	    new de.doridian.yiffcraft.commands.Help());
		commands.put("menu",	    new de.doridian.yiffcraft.commands.Menu());
		commands.put("pos",	        new de.doridian.yiffcraft.commands.GetPos());
		commands.put("up",	        new de.doridian.yiffcraft.commands.Up());
		commands.put("cam",		    new de.doridian.yiffcraft.commands.OOBCamTo());
		commands.put("lookup",	    new de.doridian.yiffcraft.commands.MCBansPlayerLookup());
		commands.put("pinfo",	    new de.doridian.yiffcraft.commands.PlayerInfo());
		commands.put("sh",	        new de.doridian.yiffcraft.commands.SpeedHack());
		commands.put("find",	    new de.doridian.yiffcraft.commands.FindPath());
		commands.put("aura",        new de.doridian.yiffcraft.commands.Aura());
		commands.put("gui",         new de.doridian.yiffcraft.commands.Overlay());
		commands.put("oob",         new de.doridian.yiffcraft.commands.OOB());
		commands.put("fly",         new de.doridian.yiffcraft.commands.Fly());
		commands.put("wallh",       new de.doridian.yiffcraft.commands.Wallhack());
		commands.put("fullbr",      new de.doridian.yiffcraft.commands.Fullbright());
		commands.put("macro",       new de.doridian.yiffcraft.commands.Macro());
		commands.put("clear",       new de.doridian.yiffcraft.commands.Clear());
		commands.put("c3d",         new de.doridian.yiffcraft.commands.Compass3D());
		commands.put("abreak",  	new de.doridian.yiffcraft.commands.ABreak());
		commands.put("arun",   		new de.doridian.yiffcraft.commands.AutoRun());
        commands.put("waterw",   	new de.doridian.yiffcraft.commands.Waterwalk());
		if(Yiffcraft.isSpecial) commands.put("cubeshot",	new de.doridian.yiffcraft.commands.CubeShot());
	}

	public static boolean outgoing(String text)
	{
		if(text.charAt(0) == PREFIX)
		{
			try {
				text = text.substring(1).trim();
				int aspace = text.indexOf(' ');
				String cmd; String[] args;
				if(aspace < 0) {
					cmd = text;
					args = new String[0];
				} else {
					cmd = text.substring(0,aspace).toLowerCase();
					args = text.substring(aspace).trim().split(" ");
				}
				if(commands.containsKey(cmd)) {
					BaseCommand baseCommand = commands.get(cmd);
					if(baseCommand == null) {
						addChat("Unknown command!");
					} else {
						baseCommand.run(args);
					}
				} else {
					addChat("Unknown command!");
				}
			}
			catch(Exception e) {
				addChat("Command error: " + e.getMessage());
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
	
	private static boolean waitingForReply = false;
	private static String reply = "";
	private static String mustMatchStart = null;
	private static String mustMatchEnd = null;
	
	public static String incoming(String text)
	{
		if(WorldeditCui.processChat(text)) return "";
	
		String noCC = Util.stripColorCodes(text);

		if(waitingForReply && (mustMatchStart == null || Pattern.matches(mustMatchStart,noCC))) {
			reply += noCC;
			mustMatchStart = null;
			if(mustMatchEnd != null) {
				if(mustMatchEnd.equals("NO_END_OF_LINE")) {
					if(Yiffcraft.minecraft.fontRenderer.getStringWidth(noCC+"#") < 320) waitingForReply = false;
				} else if(Pattern.matches(mustMatchEnd,noCC)) {
					waitingForReply = false;
				}
			} else {
				waitingForReply = false;
			}
			return "";
		}

		return text;
	}
	
	public static void emitChatMsg(String msg)
	{
		if (!SpoutClient.getInstance().getChatManager().handleCommand(msg)) {
			SpoutClient.getInstance().getChatManager().sendChat(msg);	
		}
	}
	
	public static void addChat(String msg, char color)
	{
		Yiffcraft.minecraft.ingameGUI.addChatMessage("\u00a7"+color+"[YC]\u00a7f " + msg);
	}
	
	public static void addChat(String msg)
	{
		addChat(msg,'5');
	}
	
	private static final int TIMEOUT = 50;
	
	public static String chatQueryReply(String msg)
	{
		return chatQueryReply(msg, null, null);
	}
	
	public static String chatQueryReply(String msg, String matchStart)
	{
		return chatQueryReply(msg, matchStart, null);
	}
	
	public synchronized static String chatQueryReply(String msg, String matchStart, String matchEnd)
	{
		mustMatchStart = matchStart;
		mustMatchEnd = matchEnd;
		waitingForReply = true;
		Yiffcraft.SendPacket(new Packet3Chat(msg));
		int i = 0;
		while(waitingForReply) {
			if(i++ > TIMEOUT) {
				waitingForReply = false;
				reply = "";
				return "";
			}
			try {
				Thread.sleep(100);
			}
			catch(Exception e) { }
		}
		String rep = reply;
		reply = "";
		return rep;
	}
}