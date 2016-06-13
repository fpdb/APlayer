package remix.myplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import remix.myplayer.R;
import remix.myplayer.fragments.AllSongFragment;
import remix.myplayer.infos.MP3Info;
import remix.myplayer.services.MusicService;
import remix.myplayer.ui.customviews.CircleImageView;
import remix.myplayer.ui.customviews.ColumnView;
import remix.myplayer.ui.dialog.OptionDialog;
import remix.myplayer.utils.CommonUtil;
import remix.myplayer.utils.DBUtil;
import remix.myplayer.utils.Global;

/**
 * Created by Remix on 2015/11/30.
 */
public class AllSongAdapter extends SimpleCursorAdapter implements ImpAdapter{
    public static AllSongAdapter mInstance;
    private Context mContext;
    private Cursor mCursor;
    private ColumnView mColumnView;

    public AllSongAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInstance = this;
        mCursor = c;
        mContext = context;
    }

    public void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //查看是否有缓存
        if(convertView == null) {
            convertView = super.getView(position, convertView, parent);
            holder = new ViewHolder();
            holder.mImage = (CircleImageView)convertView.findViewById(R.id.song_head_image);
            holder.mName = (TextView)convertView.findViewById(R.id.displayname);
            holder.mOther = (TextView)convertView.findViewById(R.id.detail);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder)convertView.getTag();

        if(!mCursor.moveToPosition(position))
            return convertView;

        //获得当前播放的歌曲
        final MP3Info currentMP3 = MusicService.getCurrentMP3();
        //判断该歌曲是否是正在播放的歌曲
        //如果是,高亮该歌曲，并显示动画
        if(currentMP3 != null){
            boolean flag = mCursor.getInt(AllSongFragment.mSongId) == MusicService.getCurrentMP3().getId();
            holder.mName.setTextColor(flag ? Color.parseColor("#782899") : Color.parseColor("#ffffffff"));
            mColumnView = (ColumnView)convertView.findViewById(R.id.columnview);
            mColumnView.setVisibility(flag ? View.VISIBLE : View.GONE);
//            if(flag){
//                Log.d("AllSongAdapter","song:" + name);
//                Log.d("AllSongAdapter","isplay:" + MusicService.getIsplay());
//            }
            //根据当前播放状态以及动画是否在播放，开启或者暂停的高亮动画
            if(MusicService.getIsplay() && !mColumnView.getStatus() && flag){
                mColumnView.startAnim();
            }

            else if(!MusicService.getIsplay() && mColumnView.getStatus()){
                Log.d("AllSongAdapter","停止动画 -- 歌曲名字:" + mCursor.getString(AllSongFragment.mDisPlayNameIndex));
                mColumnView.stopAnim();
            }
        }

        try {
            //设置歌曲名
            String name = CommonUtil.processInfo(mCursor.getString(AllSongFragment.mDisPlayNameIndex),CommonUtil.SONGTYPE);
            holder.mName.setText(name);

            //艺术家与专辑
            String artist = CommonUtil.processInfo(mCursor.getString(AllSongFragment.mArtistIndex),CommonUtil.ARTISTTYPE);
            String album = CommonUtil.processInfo(mCursor.getString(AllSongFragment.mAlbumIndex),CommonUtil.ALBUMTYPE);
            //封面
            holder.mOther.setText(artist + "-" + album);
        } catch (Exception e){
            e.printStackTrace();
        }


        ImageLoader.getInstance().displayImage("content://media/external/audio/albumart/" + mCursor.getString(AllSongFragment.mAlbumIdIndex),
                holder.mImage);
        //选项Dialog
        final ImageView mItemButton = (ImageView)convertView.findViewById(R.id.allsong_item_button);
        mItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MP3Info temp = DBUtil.getMP3InfoById(Global.mAllSongList.get(position));
                Intent intent = new Intent(mContext, OptionDialog.class);
//                intent.putExtra("Position",position);
                intent.putExtra("MP3Info",temp);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public void UpdateColumnView(boolean isplay) {
        if(mColumnView != null){
            if(isplay)
                mColumnView.startAnim();
            else
                mColumnView.stopAnim();
        }
    }

    class ViewHolder {
        public TextView mName;
        public TextView mOther;
        public CircleImageView mImage;
    }
}



