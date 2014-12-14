package com.example.chaaru.spotter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.ListCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.NotificationTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.SimpleTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.DeckOfCardsManager;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCards;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCardsException;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteResourceStore;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteToqNotification;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.CardImage;

public class ActivityOne extends Activity {
    //Toq stuff
    private DeckOfCardsManager mDeckOfCardsManager;
    private RemoteDeckOfCards mRemoteDeckOfCards;
    private RemoteResourceStore mRemoteResourceStore;
    private DeckOfCardsEventListenerImpl deckOfCardsEventListener;

    public ActivityOne() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_one);
        //Toq stuff
        mDeckOfCardsManager = DeckOfCardsManager.getInstance(getApplicationContext());
        init();
        deckOfCardsEventListener = new DeckOfCardsEventListenerImpl();
        findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                install();
            }
        });
    }

    private void install() {
        updateDeckOfCardsFromUI();
        try {
            mDeckOfCardsManager.installDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Application already installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDeckOfCardsFromUI() {
        if (mRemoteDeckOfCards == null) {
            mRemoteDeckOfCards = createDeckOfCards();
        }
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        // Card #1
        SimpleTextCard c0 = new SimpleTextCard("taskA");
        listCard.add(c0);
        c0.setHeaderText("Exercise: Chest Press");
        c0.setTitleText("Sets: 3\n" + "Reps: 8\n" + "Weight: 200");
        String[] m0 = {""};
        c0.setMessageText(m0);
        c0.setReceivingEvents(true);
        c0.setShowDivider(true);
        Bitmap bmp0 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.start_button), 250, 288, false);
        c0.setCardImage(mRemoteResourceStore, new CardImage("c0img", bmp0));
    }

    // Create some cards with example content
    private RemoteDeckOfCards createDeckOfCards() {
        ListCard listCard = new ListCard();
        SimpleTextCard simpleTextCard = new SimpleTextCard("card0");
        listCard.add(simpleTextCard);
        return new RemoteDeckOfCards(this, listCard);
    }

    //    Initialise
    private void init() {
        // Create the resourse store for icons and images
        mRemoteResourceStore = new RemoteResourceStore();
        // Try to retrieve a stored deck of cards
        try {
            mRemoteDeckOfCards = createDeckOfCards();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * @see android.app.Activity#onStart()
     */
    protected void onStart() {
        super.onStart();

        // If not connected, try to connect
        if (!mDeckOfCardsManager.isConnected()) {
            try {
                mDeckOfCardsManager.connect();
            } catch (RemoteDeckOfCardsException e) {
                e.printStackTrace();
            }
        }

        mDeckOfCardsManager.addDeckOfCardsEventListener(deckOfCardsEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_one, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle card events triggered by the user interacting with a card in the installed deck of cards
    private class DeckOfCardsEventListenerImpl implements DeckOfCardsEventListener {

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardOpen(java.lang.String)
         */
        public void onCardOpen(final String cardId) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //The screen on which to scroll down and press start
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //walking through reps
                    for(int i = 8; i > 0; i--) {
                        RemoteToqNotification notification = new RemoteToqNotification(ActivityOne.this, 1000, "Chest Press", new String[]{"Sets: 3\nReps: " + i + "\nWeight: 200"});
                        try {
                            mDeckOfCardsManager.sendNotification(notification);
                        } catch (RemoteDeckOfCardsException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //The countdown
                    RemoteToqNotification waitNotification = new RemoteToqNotification(ActivityOne.this, 1000, "Rest\n2:00\n", new String[]{"Next: 3x12 Pullups"});
                    try {
                        mDeckOfCardsManager.sendNotification(waitNotification);
                    } catch (RemoteDeckOfCardsException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for(int i = 59; i > 40; i--) {
                        NotificationTextCard nCard = new NotificationTextCard(1000, "Rest\n1:" + i + "\n", new String[]{"Next: 3x12 Pullups"});
                        nCard.setVibeAlert(false);
                        RemoteToqNotification waitNotification2 = new RemoteToqNotification(ActivityOne.this, nCard);
                        try {
                            mDeckOfCardsManager.sendNotification(waitNotification2);
                        } catch (RemoteDeckOfCardsException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardVisible(java.lang.String)
         */
        public void onCardVisible(final String cardId) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(FsmActivity.this, "visible " + cardId, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardInvisible(java.lang.String)
         */
        public void onCardInvisible(final String cardId) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(FsmActivity.this, "invisible " + cardId, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardClosed(java.lang.String)
         */
        public void onCardClosed(final String cardId) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(FsmActivity.this, "closed " + cardId, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onMenuOptionSelected(java.lang.String, java.lang.String)
         */
        public void onMenuOptionSelected(final String cardId, final String menuOption) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(FsmActivity.this, cardId + " [" + menuOption + "]", Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onMenuOptionSelected(java.lang.String, java.lang.String, java.lang.String)
         */
        public void onMenuOptionSelected(final String cardId, final String menuOption, final String quickReplyOption) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(FsmActivity.this, cardId + " [" + menuOption + ":" + quickReplyOption + "]", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}