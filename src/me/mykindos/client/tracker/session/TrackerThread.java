package me.mykindos.client.tracker.session;

import me.mykindos.client.tracker.Tracker;
import org.osbot.rs07.api.Client;
import org.osbot.rs07.api.model.Item;

/**
 * Handles Session Tracking on a seperate thread to prevent any script performance loss.
 * Includes Inventory tracking and session length management
 */
public class TrackerThread extends Thread {

    private SessionTracker sessionTracker;
    private Item[] inventoryItems, equippedItems;

    /**
     * Create the Tracker Thread
     * @param tracker Session Tracker
     */
    public TrackerThread(SessionTracker tracker){
        super();
        this.sessionTracker = tracker;
        inventoryItems = tracker.getTracker().getInventory().getItems();
        equippedItems = tracker.getTracker().getEquipment().getItems();
    }

    /**
     * Tracks the session length (as specified), and monitors the inventory
     */
    @Override
    public void run(){
            while(sessionTracker.getTracker().isRunning()){

                try {
               sessionTracker.getTracker().getClient().isLoggedIn();
                    if(sessionTracker.getTracker().getClient().getGameState() == Client.GameState.LOGGED_IN) {
                        trackInventory();
                        if ( (sessionTracker.getSession().getStartTime()
                                + (60_000 * sessionTracker.getTracker().getUpdateInterval())) - System.currentTimeMillis() <= 0) {
                            sessionTracker.endSession();
                        }
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * Track items inventory and equipment tabs.
     * Ignores items withdrawn or put into a bank, or received / lost to the grand exchange.
     */
    private void trackInventory(){
        Tracker tracker = sessionTracker.getTracker();


        Item[] currentInventoryItems = tracker.getInventory().getItems();
        Item[] currentEquippedItems = tracker.getEquipment().getItems();

        if(!tracker.getBank().isOpen() && !tracker.getDepositBox().isOpen() && !tracker.getGrandExchange().isOpen()) {
            checkItems(currentEquippedItems, 10);
            checkItems(currentInventoryItems, 27);
        }

        inventoryItems =currentInventoryItems.clone();
        equippedItems = currentEquippedItems.clone();


    }

    /**
     * Test to see if items have been received, lost, or spent
     * If the item still exists but the stack size has decreased, item will be marked as 'Spent'
     * @param items Item set to test
     * @param loopSize Max size of item set (27 for inventory, 10 for equipment)
     */
    private void checkItems(Item[] items, int loopSize){
        Item[] testCase = loopSize > 10 ? inventoryItems : equippedItems;

        Item oldItem, currentItem;
        for(int i = 0; i < loopSize; i++){
            currentItem = items[i];
            oldItem = testCase[i];

            if(currentItem != null && oldItem == null){
                System.out.println(currentItem.getName());
                //sessionTracker.getSession().getItemData().add(new ItemData(currentItem.getName(), currentItem.getAmount(), "Received"));
            }else if(currentItem == null && oldItem != null){
                boolean moved = false;
                for(int x = 0; x < loopSize; x++){
                    Item temp = items[x];
                    if(temp != null){
                        // Assume item was just moved to a different slot
                        if(temp.getAmount() == oldItem.getAmount() && temp.getName().equals(oldItem.getName())){
                            moved = true;
                        }
                    }
                }
                if(!moved) {
                    sessionTracker.getSession().getItemData().add(new ItemData(oldItem.getName(), oldItem.getAmount(), "Lost"));
                }
            }else if(currentItem != null && oldItem != null){
                if(currentItem.getAmount() < oldItem.getAmount()){
                    sessionTracker.getSession().getItemData().add(new ItemData(oldItem.getName(),
                            oldItem.getAmount() - currentItem.getAmount(), "Spent"));
                }else{
                    sessionTracker.getSession().getItemData().add(new ItemData(oldItem.getName(),
                            currentItem.getAmount() - oldItem.getAmount(), "Received"));
                }
            }
        }
    }
}
