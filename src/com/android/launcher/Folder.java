/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.airshiplay.launcher.R;

/**
 * Represents a set of icons chosen by the user or generated by the system.
 */
public class Folder extends LinearLayout implements DragSource, OnItemLongClickListener,
        OnItemClickListener, OnClickListener, View.OnLongClickListener {

    protected AbsListView mContent;
    protected DragController mDragger;
    
    protected Launcher mLauncher;

    protected Button mCloseButton;
    
    protected FolderInfo mInfo;
    
    /**
     * Which item is being dragged
     */
    protected ApplicationInfo mDragItem;

    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     */
    public Folder(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAlwaysDrawnWithCacheEnabled(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mContent = (AbsListView) findViewById(R.id.folder_content);
        mContent.setOnItemClickListener(this);
        mContent.setOnItemLongClickListener(this);
        
        mCloseButton = (Button) findViewById(R.id.folder_close);
        mCloseButton.setOnClickListener(this);
        mCloseButton.setOnLongClickListener(this);
    }
    
    public void onItemClick(AdapterView parent, View v, int position, long id) {
        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
        mLauncher.startActivitySafely(app.intent);
    }

    public void onClick(View v) {
        mLauncher.closeFolder(this);
    }

    public boolean onLongClick(View v) {
        mLauncher.closeFolder(this);
        mLauncher.showRenameDialog(mInfo);
        return true;
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!view.isInTouchMode()) {
            return false;
        }

        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);

        mDragger.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
        mLauncher.closeFolder(this);
        mDragItem = app;

        return true;
    }

    public void setDragger(DragController dragger) {
        mDragger = dragger;
    }

    public void onDropCompleted(View target, boolean success) {
    }

    /**
     * Sets the adapter used to populate the content area. The adapter must only
     * contains ApplicationInfo items.
     *
     * @param adapter The list of applications to display in the folder.
     */
    void setContentAdapter(BaseAdapter adapter) {
        mContent.setAdapter(adapter);
    }

    void notifyDataSetChanged() {
        ((BaseAdapter) mContent.getAdapter()).notifyDataSetChanged();
    }

    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }
    
    /**
     * @return the FolderInfo object associated with this folder
     */
    FolderInfo getInfo() {
        return mInfo;
    }

    // When the folder opens, we need to refresh the GridView's selection by
    // forcing a layout
    void onOpen() {
        mContent.requestLayout();
    }

    void onClose() {
        final Workspace workspace = mLauncher.getWorkspace();
        workspace.getChildAt(workspace.getCurrentScreen()).requestFocus();
    }

    void bind(FolderInfo info) {
        mInfo = info;
        mCloseButton.setText(info.title);
    }
}
