package com.sybiload.recoverx;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityRecovery extends AppCompatActivity
{
    private TextView textViewRecovery;
    private TextView textViewVersion;
    private TextView textViewDevice;
    private TextView textViewUpdate;
    private TextView textViewMaintainer;
    private TextView textViewInfo;

    private LinearLayout llTrusted;
    private LinearLayout llBeta;
    private LinearLayout llInfo;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        textViewRecovery = (TextView) findViewById(R.id.textViewRecoveryTitle);
        textViewVersion = (TextView) findViewById(R.id.textViewRecoveryVersion);
        textViewDevice = (TextView) findViewById(R.id.textViewRecoveryDevice);
        textViewUpdate = (TextView) findViewById(R.id.textViewRecoveryUpdate);
        textViewMaintainer = (TextView) findViewById(R.id.textViewRecoveryMaintainer);
        textViewInfo = (TextView) findViewById(R.id.textViewRecoveryInfo);

        llTrusted = (LinearLayout) findViewById(R.id.llTrusted);
        llBeta = (LinearLayout) findViewById(R.id.llBeta);
        llInfo = (LinearLayout) findViewById(R.id.llInfo);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButtonRecoveryInstall);

        final int id = getIntent().getIntExtra("recovery", -1);
        Recovery recovery = Static.allRecovery.get(id);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Installation");

        textViewRecovery.setText(recovery.getRecovery());
        textViewVersion.setText(recovery.getVersion());
        textViewDevice.setText(recovery.getName());
        textViewUpdate.setText(recovery.getDate());
        textViewMaintainer.setText(recovery.getMaintainer());
        textViewInfo.setText(recovery.getInfo());

        if (!recovery.getTrusted())
            llTrusted.setVisibility(View.GONE);

        if (!recovery.getDev())
            llBeta.setVisibility(View.GONE);

        if (recovery.getInfo().isEmpty())
            llInfo.setVisibility(View.GONE);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ActivityRecovery.this, ActivityInstall.class);
                intent.putExtra("recovery", id);
                ActivityRecovery.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}