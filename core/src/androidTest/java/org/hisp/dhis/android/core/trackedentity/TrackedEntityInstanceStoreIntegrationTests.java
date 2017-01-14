/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstanceStoreIntegrationTests extends AbsStoreTestCase {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String ORGANISATION_UNIT = "test_organisationUnit";
    private static final State STATE = State.ERROR;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    public static final String[] TRACKED_ENTITY_INSTANCE_PROJECTION = {
            TrackedEntityInstanceModel.Columns.UID,
            TrackedEntityInstanceModel.Columns.CREATED,
            TrackedEntityInstanceModel.Columns.LAST_UPDATED,
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT,
            TrackedEntityInstanceModel.Columns.STATE
    };

    private TrackedEntityInstanceModelStore trackedEntityInstanceStore;

    public static ContentValues create(long id, String uid) {

        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.ID, id);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.UID, uid);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.CREATED, DATE);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.LAST_UPDATED, DATE);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.STATE, STATE.name());
        return trackedEntityInstance;
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        trackedEntityInstanceStore = new TrackedEntityInstanceModelStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws Exception {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ContentValues organisationUnit = CreateOrganisationUnitUtils.create(1L, ORGANISATION_UNIT);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        long rowId = trackedEntityInstanceStore.insert(
                UID,
                date,
                date,
                ORGANISATION_UNIT,
                STATE
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.TRACKED_ENTITY_INSTANCE,
                TRACKED_ENTITY_INSTANCE_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        UID,
                        DATE,
                        DATE,
                        ORGANISATION_UNIT,
                        STATE)
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutForeignKey_shouldThrowException() throws Exception {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        trackedEntityInstanceStore.insert(
                UID,
                date,
                date,
                ORGANISATION_UNIT,
                STATE
        );
    }

    // ToDo: test cascade deletion

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        trackedEntityInstanceStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
