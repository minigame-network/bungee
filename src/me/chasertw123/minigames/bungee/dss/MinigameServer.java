package me.chasertw123.minigames.bungee.dss;

import com.mongodb.client.model.Filters;
import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.queue.v2.ServerInstance;
import me.chasertw123.minigames.bungee.queue.v2.ServerState;
import me.chasertw123.minigames.shared.database.Database;
import me.chasertw123.minigames.shared.framework.GeneralServerStatus;
import me.chasertw123.minigames.shared.framework.ServerGameType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MinigameServer {

    private static int portBank = 26000;
    private static boolean WINDOWS = true;
    private static List<Integer> freedPorts = new ArrayList<>();

    private ServerGameType gameType;
    private int port;
    private Process process;
    private BufferedWriter processIn;
    private File serverFolder;
    private String serverId;

    public MinigameServer(int port, ServerGameType serverGameType) {
        this.port = port;
        this.gameType = serverGameType;

        // Create the server instance
        try {
            createServer();
        } catch (IOException e) {
            System.out.println("Failed to create Minigame server. GameType = " + serverGameType + ", port = " + port);

            e.printStackTrace();
        }
    }

    public String getServerId() {
        return serverId;
    }

    public MinigameServer(ServerGameType serverGameType) {
        this(generatePort(), serverGameType);
    }

    public int getPort() {
        return port;
    }

    public ServerGameType getGameType() {
        return gameType;
    }

    private void createServer() throws IOException {
        serverId = gameType.toString() + "_" + port;

        serverFolder = new File("../minigameservers/" + gameType.toString() + "_" + port);
        serverFolder.mkdirs();

        // Copy the prefab content in
        FileUtils.copyDirectory(new File("../prefabs/" + gameType.getPrefabName()), serverFolder);

        // Modify the server.properties file, and the servername.txt file
        replaceInFile(new File(serverFolder.getPath() + "/server.properties"), "server-port=%PORT%", "server-port=" + port);
        replaceInFile(new File(serverFolder.getPath() + "/servername.txt"), "%SERVERNAME%", gameType.toString() + "_" + port);

        // Start the server
        System.out.println("Attempting to start server " + gameType.toString() + "_" + port);

        ProcessBuilder builder = new ProcessBuilder(WINDOWS ? "C:/Windows/System32/cmd.exe" : "/bin/bash");
        process = builder.start();

        processIn = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        runCommand("echo Preparing to run minigame server " + serverId + ".");
        runCommand("cd ../minigameservers/" + serverId);
        runCommand("java -Xmx1G -Xms1G -jar spigot.jar");

        Scanner s = new Scanner(process.getInputStream());
        new Thread(() -> {
            while(s.hasNextLine()) {
                System.out.println("[MINIGAME SERVER " + serverId + "] " + s.nextLine());
            }
        }).start();

        // Register the server in the proxy.
        ServerInfo info = ProxyServer.getInstance().constructServerInfo(serverId, new InetSocketAddress("localhost", port), "DSS " + serverId, false);
        ProxyServer.getInstance().getServers().put(serverId, info);

        // Add the server to the queue system
        Main.getInstance().getQueueController().getServers().add(new ServerInstance(serverId, ServerState.RESTARTING,
                this.gameType, 0, 0)); // Add it to the queue
    }

    private void runCommand(String command) throws IOException {
        processIn.write(command);
        processIn.newLine();
        processIn.flush();
    }

    public void deleteServer() throws IOException {
        // Remove from the queue roster
        Main.getInstance().getQueueController().removeServer(serverId);

        destroyProcess();

        // Delete the folder
        FileUtils.deleteDirectory(serverFolder);

        // Remove from proxy
        ProxyServer.getInstance().getServers().remove(serverId);

        // Add the freed port
        freedPorts.add(this.port);

        // Update the server status to prevent dupe deleting
        Document statusDocument = new Document();
        statusDocument.put("servername", serverId);
        statusDocument.put("status", GeneralServerStatus.RESTARTING.getId());
        statusDocument.put("maxplayers", 0);
        statusDocument.put("currentplayers", 0);

        Main.getInstance().getDatabase().getMongoCollection(Database.Collection.SERVER_STATUS)
                .replaceOne(Filters.eq("servername", serverId), statusDocument, Database.upsert());
    }

    public void destroyProcess() {
        if(process == null)
            return;

        process.destroy();

        process = null;
    }

    private void replaceInFile(File file, String toReplace, String replacement) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));

        for (int i = 0; i < fileContent.size(); i++) {
            if (fileContent.get(i).equalsIgnoreCase(toReplace)) {
                fileContent.set(i, replacement);
                break;
            }
        }

        Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
    }

    private static int generatePort() {
        if(freedPorts.isEmpty())
            return portBank++;

        int thisPort = freedPorts.get(0);
        freedPorts.remove(Integer.valueOf(thisPort));

        return thisPort;
    }
}
