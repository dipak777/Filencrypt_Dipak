package com.example.filencrypt.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.filencrypt.R;
import com.example.filencrypt.models.FileListAdapter;
import com.example.filencrypt.models.FilencryptFile;
import com.example.filencrypt.models.OnChangeListener;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class FileBrowserActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnChangeListener {

    protected FloatingActionButton actionButton;
    protected FileListAdapter listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private Deque<Pair<Integer, Integer>> scrollPositions;

    //private TextView textView;//TextView path

    protected abstract void onFileClicked(FilencryptFile file);

    protected abstract void onActionButtonClicked();

    protected abstract String getRootTitle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.actionButton = this.findViewById(R.id.action_button);
        this.listView = this.findViewById(R.id.file_list_view);
        this.refreshLayout = this.findViewById(R.id.refresh_layout);

        //this.textView = this.findViewById(R.id.filePath);//TextView path

        this.scrollPositions = new ArrayDeque<>();
        this.listAdapter = new FileListAdapter(this, this);

        //Showing actual path to TextView but it is not working right now
        /*textView.setOnClickListener(this);
        File dir = Environment.getExternalStorageDirectory();
        String path = dir.getAbsolutePath() + "/" ;
        textView.setText(path);*/


        actionButton.setOnClickListener(this);
        this.listView.setOnItemClickListener(this);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setEmptyView(this.findViewById(R.id.empty_text_view));
        this.refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!PasscodeActivity.ensurePasscode(this)) return;
        if (!ensurePermission()) return;

        initialize();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FilencryptFile newDirectory = this.listAdapter.up();
            if (newDirectory != null) {
                updateTitle();
                popScrollPosition();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        this.listAdapter.notifyDataSetChanged();
        this.refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                System.exit(0);
            }
        }
        initialize();
    }

    @Override
    public void onClick(View v) {
        onActionButtonClicked();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FilencryptFile file = (FilencryptFile) view.getTag();

        if (file.isDirectory()) {
            pushScrollPosition();
            this.listAdapter.openDirectory(file);
            updateTitle();
        } else {
            onFileClicked(file);
        }
    }

    protected void initialize() {
        this.listAdapter.notifyDataSetChanged();
        updateTitle();
    }

    private void updateTitle() {
        FilencryptFile currentDirectory = this.listAdapter.getCurrentDirectory();
        setTitle(currentDirectory.isRoot() ? getRootTitle() : currentDirectory.getName());
    }

    private boolean ensurePermission() {
        boolean readExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean writeExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!readExternalStorage || !writeExternalStorage) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return false;
        } else {
            return true;
        }
    }

    private void pushScrollPosition() {
        int index = this.listView.getFirstVisiblePosition();
        View v = this.listView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - this.listView.getPaddingTop());
        this.scrollPositions.push(new Pair<>(index, top));
    }

    private void popScrollPosition() {
        if (this.scrollPositions.isEmpty()) return;
        Pair<Integer, Integer> pair = this.scrollPositions.pop();
        this.listView.setSelectionFromTop(pair.first, pair.second);
    }
}
