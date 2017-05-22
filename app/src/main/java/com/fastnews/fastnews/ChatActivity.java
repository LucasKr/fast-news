package com.fastnews.fastnews;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fastnews.fastnews.actions.Actions;
import com.fastnews.fastnews.actions.RenderNewsAction;
import com.fastnews.fastnews.models.ListNewsModel;
import com.fastnews.fastnews.models.NewsModel;

import java.util.ArrayList;
import java.util.List;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;


public class ChatActivity extends AppCompatActivity implements AIListener {

    private EditText msgToBot;
    private Button btnSendMessage;
    private ListView msgsListView;
    private List<NewsModel> newsFound = new ArrayList<>();

    private boolean gravando = false;


    private List<String> msgs;
    private RequestQueue queue;


    private final AIConfiguration config = new AIConfiguration("eedb4f43cf6b4c5cb1302d9d7cea7d89",
            AIConfiguration.SupportedLanguages.PortugueseBrazil,
            AIConfiguration.RecognitionEngine.System);

    private AIDataService aiDataService;

    private AIService service;

    private void log(String msgLog) {
        Log.d("ChatActivity", msgLog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_chat_bot);

        msgs = new ArrayList<>();

        msgToBot = (EditText) findViewById(R.id.msgToBot);
        msgsListView = (ListView) findViewById(R.id.msgsListView);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);

        ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgs);
        msgsListView.setAdapter(listAdapter);

        aiDataService = new AIDataService(config);


        ListNewsModel listNewsModel = new ListNewsModel(newsFound);

        Intent intent = getIntent();
        intent.putExtra("news", listNewsModel);
        setResult(ActivityActions.CHAT.ordinal(), intent);

        service = AIService.getService(this, config);
        service.setListener(this);

        gravando = false;
    }

    public void sendRequest(View view) {

        String msg = msgToBot.getText().toString();

        sendRequest(msg);
    }

    public void sendRequest(String msg) {
        if(msg == null || msg.isEmpty())
            return;

        btnSendMessage.setEnabled(false);
        msgToBot.setEnabled(false);

        addMsgToListView(msg);

        final AIRequest request = new AIRequest();
        request.setQuery(msg);

        new AsyncTask<AIRequest, Void, AIResponse>() {

            @Override
            protected AIResponse doInBackground(AIRequest... params) {
                try  {
                    AIResponse aiResponse = aiDataService.request(params[0]);
                    return aiResponse;
                } catch (AIServiceException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                super.onPostExecute(aiResponse);
                if(aiResponse != null) {
                    Result result = aiResponse.getResult();
                    String botMsg = result.getFulfillment().getSpeech();
                    addMsgToListView(botMsg);
                    Actions.chatBotTalking(newsFound, result.getAction(), queue, new RenderNewsAction() {
                        @Override
                        public void renderNewsDay() {
                            finish();
                        }
                    });

                    msgToBot.setText("");
                    btnSendMessage.setEnabled(true);
                }
            }
        }.execute(request);

    }

    public void watchRecord(View view) {
        if(!gravando) {
            service.startListening();
            btnSendMessage.setEnabled(false);
            msgToBot.setEnabled(false);
        }
        else {
            btnSendMessage.setEnabled(true);
            msgToBot.setEnabled(true);
            service.stopListening();
        }

        gravando = !gravando;
    }

    private void addMsgToListView(String msg) {
        msgs.add(msg);
        ((BaseAdapter) msgsListView.getAdapter()).notifyDataSetChanged();

        msgsListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                msgsListView.setSelection(msgsListView.getCount() - 1);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(Actions.REQUEST_NEWS);
        }
    }

    @Override
    public void onResult(final AIResponse response) {
        Log.d("ChatActivity", response.toString());
        String msg = response.getResult().getResolvedQuery();
        sendRequest(msg);
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ChatActivity", error.toString());
            }
        });
    }

    @Override
    public void onListeningFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ChatActivity", "Finishing the listenner");
            }
        });
    }

    @Override
    public void onListeningCanceled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ChatActivity", "On listening canceled...");
            }
        });
    }

    @Override
    public void onListeningStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ChatActivity", "Listening started...");
            }
        });
    }

    @Override
    public void onAudioLevel(float level) {

    }
}
