package com.example.homework02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView myListView;
    private Button b;
    private File musicFile= new File(Environment.getExternalStorageDirectory(),"music");
    private ArrayList<File> musicInfoList=new ArrayList<>();
    private ArrayList<String> musicNameList=new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //System.out.println(musicFile);

        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyStoragePermissions(MainActivity.this);
        }

        myListView=(ListView)findViewById(R.id.Listview);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File path=new File(musicInfoList.get(position).getPath());
                //System.out.println("path:"+path);

                //判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.example.homework02.fileProvider", path);
                    intent.setDataAndType(contentUri, "audio/mp3");
                } else {
                    intent.setDataAndType(Uri.fromFile(path), "audio/mp3");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }
        });

        searchMusic();

        final ArrayAdapter musicAdapter=new ArrayAdapter(MainActivity.this,R.layout.list_item,musicNameList);
        musicAdapter.notifyDataSetChanged();
        myListView.setAdapter(musicAdapter);

        b=(Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusic();
                musicAdapter.notifyDataSetChanged();

                /*
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File path=new File(musicFile,"test.mp3");
                //System.out.println("path:"+path);

                //判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.example.homework02.fileProvider", path);
                    intent.setDataAndType(contentUri, "audio/mp3");
                } else {
                    intent.setDataAndType(Uri.fromFile(path), "audio/mp3");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);

                 */
            }
        });
    }

    public void searchMusic(){
        musicInfoList.clear();
        musicNameList.clear();

        File[] files=musicFile.listFiles();

        if(files==null){
            System.out.println("No music found");
            return;
        }
        for(File file:files){
            String name=file.getName();
            if(name.endsWith(".mp3")||name.endsWith("m4a")) {
                musicInfoList.add(file);
                musicNameList.add(name);
            }
        }
    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void verifyStoragePermissions(Activity activity){
        //检测是否有写的权限
        int permission = activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        // 没有写的权限，去申请写的权限，会弹出对话框
        if (permission != PackageManager.PERMISSION_GRANTED)
            activity.requestPermissions(PERMISSIONS_STORAGE, 1);
    }
}
