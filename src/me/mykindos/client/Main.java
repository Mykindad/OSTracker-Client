package me.mykindos.client;

import me.mykindos.client.tracker.Tracker;
import me.mykindos.client.tracker.exceptions.InvalidSetupException;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "Tom", info = "test1123", name = "test1234", version = 0.1, logo = "")
public class Main extends Script {


    private Tracker tracker;

    @Override
    public void onStart(){
        try {
            tracker = new Tracker(getBot(), getName()).establishConnection("127.0.0.1", 1337) // Mandatory
            .setupMysql() // Mandatory
            .setUpdateInterval(60) // Defaults to an hour
            .start(); // Optional
        } catch (InvalidSetupException e) {
            if(tracker != null){
                tracker.stop();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onExit(){
        if(tracker != null) {
            tracker.stop();
        }
    }

    @Override
    public int onLoop() throws InterruptedException {
        return 0;
    }

}
