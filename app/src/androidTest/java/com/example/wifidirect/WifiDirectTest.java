package com.example.wifidirect;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.wifidirect.controller.ChatActivityController;
import com.example.wifidirect.controller.MainActivityController;
import com.example.wifidirect.db.ChatDatabase;
import com.example.wifidirect.db.Macaddress;
import com.example.wifidirect.db.MacaddressDao;
import com.example.wifidirect.db.Message;
import com.example.wifidirect.db.MessageDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class WifiDirectTest {

    private ChatDatabase db;
    private MessageDao messageDao;
    private MacaddressDao macaddressDao;

    @Before
    public void createDb() {

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, ChatDatabase.class).build();
        macaddressDao = db.macaddressDao();
        messageDao = db.messageDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void addMacaddressAndRetrieveConversationId() {
        Macaddress macaddress1 = new Macaddress();
        macaddress1.setPartnermacaddress("12345");
        macaddressDao.createMacaddress(macaddress1);
        Integer id1 = 1;
        Integer conversationId1 = macaddressDao.getIdIfExists("12345");

        Macaddress macaddress2 = new Macaddress();
        macaddress2.setPartnermacaddress("2");
        macaddressDao.createMacaddress(macaddress2);
        Integer id2 = 2;
        Integer conversationId2 = macaddressDao.getIdIfExists("2");


        assertEquals(id1,  conversationId1);
        assertEquals(id2,  conversationId2);
    }

    @Test
    public void testThatNewDevicesAreNotFound(){
        Integer conversationId = macaddressDao.getIdIfExists("333");
        assertEquals(null,  conversationId);
    }

    @Test
    public void testThatCanAddAndRetrieveMessage() {

        Message message1 = new Message(1, "hallo", "00:02", true);
        messageDao.createMessage(message1);

        Message message2 = new Message(1, "hey", "00:03", false);
        messageDao.createMessage(message2);

        List<Message> messages = messageDao.loadChatHistory(1);
        assertFalse(messages.isEmpty());
        assertEquals(2, messages.size());
        assertEquals(messages.get(0).getText(),"hallo");
        assertEquals(messages.get(0).getRole(),true);
        assertEquals(messages.get(0).getTimestamp(),"00:02");
    }

    @Test
    public void TestMacAddress(){
        ChatActivityController mChatActivityController = ChatActivityController.getSC();
        assertTrue(mChatActivityController.getMacAddr().toUpperCase().matches("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$"));
    }


}
