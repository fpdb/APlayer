package remix.myplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import remix.myplayer.R;
import remix.myplayer.adapters.RecentlyAdapter;
import remix.myplayer.infos.MP3Info;
import remix.myplayer.services.MusicService;
import remix.myplayer.utils.Constants;
import remix.myplayer.utils.DBUtil;
import remix.myplayer.utils.Global;

/**
 * Created by taeja on 16-3-4.
 */

/**
 * 最近添加歌曲的界面
 * 目前为最近7天添加
 */
public class RecetenlyActivity extends ToolbarActivity implements MusicService.Callback{
    private RecentlyAdapter mAdapter;
    private Toolbar mToolBar;
    private ListView mListView;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mListView.setAdapter(mAdapter);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently);
        MusicService.addCallback(RecetenlyActivity.this);
        initListView();

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(mToolBar,getString(R.string.recently));
    }

    private void initListView() {
        new Thread(){
            @Override
            public void run() {
                mAdapter = new RecentlyAdapter(RecetenlyActivity.this, DBUtil.getMP3ListByIds(Global.mWeekList));
                mHandler.sendEmptyMessage(0);
            }
        }.start();
        mListView = (ListView)findViewById(R.id.recently_listview);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Constants.CTL_ACTION);
                Bundle arg = new Bundle();
                arg.putInt("Control", Constants.PLAYSELECTEDSONG);
                arg.putInt("Position", position);
                intent.putExtras(arg);
                Global.setPlayingList(Global.mWeekList);
                sendBroadcast(intent);
                view.setSelected(true);
            }
        });

    }

    //随机播放
    public void onPlayShuffle(View v){
        if(Global.mWeekList == null || Global.mWeekList.size() == 0){
            Toast.makeText(RecetenlyActivity.this,getString(R.string.no_song),Toast.LENGTH_SHORT).show();
            return;
        }
        MusicService.setPlayModel(Constants.PLAY_SHUFFLE);
        Intent intent = new Intent(Constants.CTL_ACTION);
        intent.putExtra("Control", Constants.NEXT);
        Global.setPlayingList(Global.mWeekList);
        sendBroadcast(intent);
    }

//    private void initToolbar() {
//        mToolBar = (Toolbar) findViewById(R.id.toolbar);
//        mToolBar.setTitle("最近添加");
//        mToolBar.setTitleTextColor(Color.parseColor("#ffffffff"));
//        setSupportActionBar(mToolBar);
//        mToolBar.setNavigationIcon(R.drawable.common_btn_back);
//        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.toolbar_search:
//                        startActivity(new Intent(RecetenlyActivity.this, SearchActivity.class));
//                        break;
//                    case R.id.toolbar_timer:
//                        startActivity(new Intent(RecetenlyActivity.this, TimerDialog.class));
//                        break;
//                }
//                return true;
//            }
//        });
//    }


    @Override
    public void UpdateUI(MP3Info MP3info, boolean isplay) {
//        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
//        for(Fragment fragment : fragmentList){
//            ((RecentlyFragment) fragment).getAdapter().notifyDataSetChanged();
//        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getType() {
        return Constants.RECENTLYACTIVITY;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}