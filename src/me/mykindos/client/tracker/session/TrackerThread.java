package me.mykindos.client.tracker.session;

import me.mykindos.client.tracker.Tracker;
import org.osbot.rs07.api.Client;
import org.osbot.rs07.api.model.Item;

import java.util.Arrays;

/**
 * Handles Session Tracking on a seperate thread to prevent any script performance loss.
 * Includes Inventory tracking and session length management
 */
public class TrackerThread extends Thread {

    private SessionTracker sessionTracker;
    private Item[] inventoryItems, equippedItems;

    /**
     * Create the Tracker Thread
     *
     * @param tracker Session Tracker
     */
    public TrackerThread(SessionTracker tracker) {
        super();
        this.sessionTracker = tracker;
        inventoryItems = tracker.getTracker().getInventory().getItems();
        equippedItems = tracker.getTracker().getEquipment().getItems();
    }

    /**
     * Tracks the session length (as specified), and monitors the inventory
     */
    @Override
    public void run() {
        while (sessionTracker.getTracker().isRunning()) {

            try {
                sessionTracker.getTracker().getClient().isLoggedIn();
                if (sessionTracker.getTracker().getClient().getGameState() == Client.GameState.LOGGED_IN) {
                    trackInventory();
                    if ((sessionTracker.getSession().getStartTime()
                            + (60_000 * sessionTracker.getTracker().getUpdateInterval())) - System.currentTimeMillis() <= 0) {
                        sessionTracker.endSession();
                    }
                }
                Thread.sleep(333);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Track items inventory and equipment tabs.
     * Ignores items withdrawn or put into a bank, or received / lost to the grand exchange.
     */
    private void trackInventory() {
        Tracker tracker = sessionTracker.getTracker();


        Item[] currentInventoryItems = tracker.getInventory().getItems();
        Item[] currentEquippedItems = tracker.getEquipment().getItems();

        if (!tracker.getBank().isOpen() && !tracker.getDepositBox().isOpen() && !tracker.getGrandExchange().isOpen()) {
            checkItems(currentEquippedItems, 10);
            checkItems(currentInventoryItems, 27);
        }

        inventoryItems = currentInventoryItems.clone();
        equippedItems = currentEquippedItems.clone();


    }

    /**
     * Test to see if items have been received, lost, or spent
     * If the item still exists but the stack size has decreased, item will be marked as 'Spent'
     *
     * @param items    Item set to test
     * @param loopSize Max size of item set (27 for inventory, 10 for equipment)
     */
    private void checkItems(Item[] items, int loopSize) {
        Item[] testCase = loopSize > 10 ? inventoryItems : equippedItems;

        Item oldItem, currentItem;
        for (int i = 0; i < loopSize; i++) {
            currentItem = items[i];
            oldItem = testCase[i];

            if (currentItem != null && oldItem == null) {
                if (!wasMoved(currentItem, items, testCase)) {
                    sessionTracker.getSession().getItemData().add(new ItemData(currentItem.getName(), currentItem.getAmount(), "Received"));
                }
            } else if (currentItem == null && oldItem != null) {
                if (!wasMoved(oldItem, items, testCase)) {
                    sessionTracker.getSession().getItemData().add(new ItemData(oldItem.getName(), oldItem.getAmount(), "Lost"));
                }
            } else if (currentItem != null && oldItem != null) {
                if (currentItem.getAmount() < oldItem.getAmount()) {
                    sessionTracker.getSession().getItemData().add(new ItemData(oldItem.getName(),
                            oldItem.getAmount() - currentItem.getAmount(), "Spent"));
                } else if (currentItem.getAmount() > oldItem.getAmount()) {
                    sessionTracker.getSession().getItemData().add(new ItemData(oldItem.getName(),
                            currentItem.getAmount() - oldItem.getAmount(), "Received"));
                }
            }
        }
    }

    /**
     * Check if an item changed inventory slot
     * @param item Item to check
     * @param setA Current Dataset
     * @param setB Old Dataset
     * @return True amount of an item hasn't changed
     */
    private boolean wasMoved(Item item, Item[] setA, Item[] setB) {
        Item finalOldItem = item;
        int countA = (int) Arrays.stream(setA).filter(f -> f != null && f.getName().equals(finalOldItem.getName())).count();
        int countB = (int) Arrays.stream(setB).filter(f -> f != null && f.getName().equalsIgnoreCase(finalOldItem.getName())).count();
        if (countA == countB) {
            return true;
        }

        return false;
    }
}
