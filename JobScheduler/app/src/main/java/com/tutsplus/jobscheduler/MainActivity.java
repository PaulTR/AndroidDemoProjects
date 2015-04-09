package com.tutsplus.jobscheduler;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private JobScheduler mJobScheduler;
    private Button mScheduleJobButton;
    private Button mCancelAllJobsButton;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mJobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE );
        mScheduleJobButton = (Button) findViewById( R.id.schedule_job );
        mCancelAllJobsButton = (Button) findViewById( R.id.cancel_all );

        mScheduleJobButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobInfo.Builder builder = new JobInfo.Builder( 1,
                        new ComponentName( getPackageName(), JobSchedulerService.class.getName() ) );

                builder.setPeriodic( 3000 );


                if( mJobScheduler.schedule( builder.build() ) <= 0 ) {
                    //If something goes wrong
                }
            }
        });

        mCancelAllJobsButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                mJobScheduler.cancelAll();
            }
        });
    }
}
