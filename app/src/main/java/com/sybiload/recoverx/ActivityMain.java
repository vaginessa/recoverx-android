package com.sybiload.recoverx;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sybiload.recoverx.Adapter.MyAdapter;


public class ActivityMain extends AppCompatActivity
{

    private ImageView imageViewLogo;
    private CardView cardViewInfo;
    private TextView textViewInfo;
    private Button buttonSettings;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewLogo = (ImageView) findViewById(R.id.imageViewMainLogo);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMainRecovery);
        cardViewInfo = (CardView) findViewById(R.id.cardViewMainInfo);
        textViewInfo = (TextView) findViewById(R.id.textViewMainInfo);
        buttonSettings = (Button) findViewById(R.id.buttonMainSettings);

        new loadAsync().execute();
    }

    public class loadAsync extends AsyncTask<Void, Void, Void>
    {
        boolean error = false;

        @Override
        protected Void doInBackground(Void... params)
        {
            error = !(Misc.isConnected(getApplicationContext()) && Misc.parseJson());

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            Animation translateAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
            imageViewLogo.startAnimation(translateAnim);

            Animation fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            buttonSettings.startAnimation(fadeInAnim);
            buttonSettings.setVisibility(View.VISIBLE);

            if (!error)
            {
                if (Static.allRecovery.size() > 0)
                {
                    recyclerView.setHasFixedSize(true);

                    recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(recyclerViewLayoutManager);

                    recyclerViewAdapter = new MyAdapter(ActivityMain.this);
                    recyclerView.setAdapter(recyclerViewAdapter);

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.startAnimation(fadeInAnim);
                }
                else
                {
                    cardViewInfo.setVisibility(View.VISIBLE);
                    cardViewInfo.startAnimation(fadeInAnim);

                    textViewInfo.setText("Your device isn't supported");
                }
            }
            else
            {
                cardViewInfo.setVisibility(View.VISIBLE);
                cardViewInfo.startAnimation(fadeInAnim);

                textViewInfo.setText("Check your connection");
            }
        }
    }
}
