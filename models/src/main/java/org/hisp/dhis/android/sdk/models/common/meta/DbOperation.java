/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.models.common.meta;

import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.IdentifiableObject;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

/**
 * This class is intended to implement partial
 * functionality of ContentProviderOperation for DbFlow.
 */
public final class DbOperation<T extends IdentifiableObject> {
    private final DbAction mDbAction;
    private final T mModel;
    private final IIdentifiableObjectStore<T> mModelStore;

    private DbOperation(DbAction dbAction, T model, IIdentifiableObjectStore<T> store) {
        mModel = isNull(model, "IdentifiableObject object must nto be null,");
        mDbAction = isNull(dbAction, "BaseModel.DbAction object must not be null");
        mModelStore = isNull(store, "IStore object must not be null");
    }

    public static <T extends IdentifiableObject> DbOperationBuilder<T> with(IIdentifiableObjectStore<T> store) {
        return new DbOperationBuilder<>(store);
    }

    public T getModel() {
        return mModel;
    }

    public DbAction getAction() {
        return mDbAction;
    }

    public IIdentifiableObjectStore<T> getStore() {
        return mModelStore;
    }

    public void execute() {
        switch (mDbAction) {
            case INSERT: {
                mModelStore.insert(mModel);
                break;
            }
            case UPDATE: {
                mModelStore.update(mModel);
                break;
            }
            case SAVE: {
                mModelStore.save(mModel);
                break;
            }
            case DELETE: {
                mModelStore.delete(mModel);
                break;
            }
        }
    }

    public static class DbOperationBuilder<T extends IdentifiableObject> {
        private final IIdentifiableObjectStore<T> mStore;

        DbOperationBuilder(IIdentifiableObjectStore<T> store) {
            mStore = store;
        }

        public DbOperation insert(T model) {
            return new DbOperation<>(DbAction.INSERT, model, mStore);
        }

        public DbOperation update(T model) {
            return new DbOperation<>(DbAction.UPDATE, model, mStore);
        }

        public DbOperation save(T model) {
            return new DbOperation<>(DbAction.SAVE, model, mStore);
        }

        public DbOperation delete(T model) {
            return new DbOperation<>(DbAction.DELETE, model, mStore);
        }
    }
}