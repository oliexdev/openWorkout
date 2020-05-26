/*
 * Copyright (C) 2020 olie.xdev <olie.xdev@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.health.openworkout.gui.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;

import java.io.FileNotFoundException;
import java.io.IOException;

import timber.log.Timber;

public class VideoProvider extends ContentProvider {

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        AssetManager am = getContext().getAssets();
        String file_name = uri.getPath().substring(1);

        if(file_name == null)
            throw new FileNotFoundException();

        AssetFileDescriptor afd = null;

        try {
            afd = am.openFd(file_name);
        } catch (IOException ex) {
            Timber.e(ex);
        }

        return afd;
    }

    @Override
    public String getType( Uri p1 )
    {
        return null;
    }

    @Override
    public int delete( Uri p1, String p2, String[] p3 )
    {
        return 0;
    }

    @Override
    public Cursor query(Uri p1, String[] p2, String p3, String[] p4, String p5 )
    {
        return null;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal )
    {
        return super.query( uri, projection, selection, selectionArgs, sortOrder, cancellationSignal );
    }

    @Override
    public Uri insert( Uri p1, ContentValues p2 )
    {
        return null;
    }

    @Override
    public boolean onCreate( )
    {
        return false;
    }

    @Override
    public int update(Uri p1, ContentValues p2, String p3, String[] p4 )
    {
        return 0;
    }
}
