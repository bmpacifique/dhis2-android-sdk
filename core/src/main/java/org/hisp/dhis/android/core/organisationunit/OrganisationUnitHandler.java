/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class OrganisationUnitHandler {
    private final OrganisationUnitStore organisationUnitStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    public OrganisationUnitHandler(OrganisationUnitStore organisationUnitStore,
                                   UserOrganisationUnitLinkStore userOrganisationUnitLinkStore) {
        this.organisationUnitStore = organisationUnitStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
    }

    public void handleOrganisationUnits(@NonNull List<OrganisationUnit> organisationUnits,
                                        @Nullable OrganisationUnitModel.Scope scope,
                                        @NonNull String userUid) {
        int size = organisationUnits.size();
        for (int i = 0; i < size; i++) {
            OrganisationUnit organisationUnit = organisationUnits.get(i);

            if (isDeleted(organisationUnit)) {
                organisationUnitStore.delete(organisationUnit.uid());
            } else {
                String uid = null;
                if (organisationUnit.parent() != null) {
                    uid = organisationUnit.parent().uid();
                }
                int updatedRow = organisationUnitStore.update(organisationUnit.uid(),
                        organisationUnit.code(),
                        organisationUnit.name(),
                        organisationUnit.displayName(),
                        organisationUnit.created(),
                        organisationUnit.lastUpdated(),
                        organisationUnit.shortName(),
                        organisationUnit.displayShortName(),
                        organisationUnit.description(),
                        organisationUnit.displayDescription(),
                        organisationUnit.path(),
                        organisationUnit.openingDate(),
                        organisationUnit.closedDate(),
                        uid,
                        organisationUnit.level(), organisationUnit.uid());
                if (updatedRow <= 0) {
                    organisationUnitStore.insert(
                            organisationUnit.uid(),
                            organisationUnit.code(),
                            organisationUnit.name(),
                            organisationUnit.displayName(),
                            organisationUnit.created(),
                            organisationUnit.lastUpdated(),
                            organisationUnit.shortName(),
                            organisationUnit.displayShortName(),
                            organisationUnit.description(),
                            organisationUnit.displayDescription(),
                            organisationUnit.path(),
                            organisationUnit.openingDate(),
                            organisationUnit.closedDate(),
                            uid,
                            organisationUnit.level()
                    );
                }
                if(scope != null) {
                    int updatedLinkRow = userOrganisationUnitLinkStore.update(
                            userUid, organisationUnit.uid(),
                            scope.name(), userUid, organisationUnit.uid()
                    );
                    if (updatedLinkRow <= 0) {
                        userOrganisationUnitLinkStore.insert(userUid, organisationUnit.uid(), scope.name());
                    }
                }
            }
        }
    }
}
