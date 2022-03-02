package com.apps6283.shorts_app_sql_php_api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PagerAdapter  extends RecyclerView.Adapter<PagerAdapter.ViewHolder> {


    private ArrayList<DataHandler> dataHandler;
    private Activity activity;
    private DocumentFile documentFile;

    public PagerAdapter(ArrayList<DataHandler> dataHandler, Activity activity, DocumentFile documentFile) {
        this.dataHandler = dataHandler;
        this.activity = activity;
        this.documentFile = documentFile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false);
    return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.title.setText(dataHandler.get(position).title);
        holder.videoView.setVideoURI(Uri.parse(dataHandler.get(position).url));
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                holder.progressBar.setVisibility(View.GONE);
                mediaPlayer.start();

                float  vidRatio = mediaPlayer.getVideoWidth()/(float)mediaPlayer.getVideoHeight();
                float screenRatio = holder.videoView.getWidth()/(float)holder.videoView.getHeight();

                float scale = vidRatio/screenRatio;

                if (scale>=1){

                    holder.videoView.setScaleX(scale);
                }else {

                    holder.videoView.setScaleY(1f/scale);
                }


            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                mediaPlayer.start();

            }
        });


        //// new code

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT,"Watch this funny short video - "+dataHandler.get(position).url);
//                activity.startActivity(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // download for android 11

                    downloadAndroid11ForSharing(position,documentFile);



                }else{
                    // download for android less than 11

                    downloadForLessThan11ForSharing(position);


                }





            }
        });

        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(activity, "Downloading..", Toast.LENGTH_SHORT).show();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // download for android 11

                    downloadAndroid11(position,documentFile);



                }else{
                    // download for android less than 11

                    downloadForLessThan11(position);


                }


                }
        });





    }

    @Override
    public int getItemCount() {
        return dataHandler.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        ProgressBar progressBar;
        TextView title;
        ImageView downloadBtn,shareBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.prog);
            title = itemView.findViewById(R.id.titleTxt);
            downloadBtn = itemView.findViewById(R.id.imageView);
            shareBtn = itemView.findViewById(R.id.shareImg);


        }
    }



    /*--Download Video in Storage--*/
    public void downloadForLessThan11(int position) {

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dataHandler.get(position).url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("Downloading : "+dataHandler.get(position).title+".mp4");


        request.setDestinationInExternalFilesDir(activity,Environment.DIRECTORY_DOWNLOADS,dataHandler.get(position).title+".mp4");

        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


       long reference = downloadManager.enqueue(request);



        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(activity, "Download Completed", Toast.LENGTH_SHORT).show();

                    try {
                        downloadManager.openDownloadedFile(id);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        };



        activity.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    public void downloadAndroid11(int position,DocumentFile fileDoc) {

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dataHandler.get(position).url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("Downloading : "+dataHandler.get(position).title+".mp4");


        request.setDestinationInExternalFilesDir(activity,fileDoc.getUri().getPath(),dataHandler.get(position).title+".mp4");

        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


       long reference = downloadManager.enqueue(request);



        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(activity, "Download Completed", Toast.LENGTH_SHORT).show();

                    try {
                        downloadManager.openDownloadedFile(id);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        };



        activity.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    // to share after download
    public void downloadForLessThan11ForSharing(int position) {


        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Downloading Before Sharing..");
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dataHandler.get(position).url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("Downloading : "+dataHandler.get(position).title+".mp4");


        request.setDestinationInExternalFilesDir(activity,Environment.DIRECTORY_DOWNLOADS,dataHandler.get(position).title+".mp4");

        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        long reference = downloadManager.enqueue(request);



        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(activity, "Download Completed", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                    Uri vidUri =    downloadManager.getUriForDownloadedFile(id);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("video/mp4");
                    shareIntent.putExtra(Intent.EXTRA_STREAM,vidUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,"Check This Video - ");
                    activity.startActivity(shareIntent);



                }
            }
        };



        activity.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    public void downloadAndroid11ForSharing(int position,DocumentFile fileDoc) {


        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Downloading Before Sharing..");
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dataHandler.get(position).url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("Downloading : "+dataHandler.get(position).title+".mp4");


        request.setDestinationInExternalFilesDir(activity,fileDoc.getUri().getPath(),dataHandler.get(position).title+".mp4");

        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


       long reference = downloadManager.enqueue(request);



        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //Checking if the received broadcast is for our enqueued download by matching download id

                if (reference == id) {


                    Toast.makeText(activity, "Download Completed", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();

                    Uri vidUri =    downloadManager.getUriForDownloadedFile(id);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("video/mp4");
                    shareIntent.putExtra(Intent.EXTRA_STREAM,vidUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,"Check This Video - ");
                    activity.startActivity(shareIntent);




                }

            }
        };



        activity.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }





}
