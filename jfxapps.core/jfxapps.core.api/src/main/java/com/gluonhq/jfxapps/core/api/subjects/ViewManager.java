/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.core.api.subjects;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachment;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public interface ViewManager {

    Subject<DockRequest> dock();

    Subject<View> undock();

    Subject<View> close();

    public static class DockRequest {
        private final ViewAttachment viewAttachment;
        private final View source;
        private final UUID target;
        private boolean select = true;

        public DockRequest(ViewAttachment viewAttachment, View source, UUID target, boolean select) {
            super();
            this.viewAttachment = viewAttachment;
            this.source = source;
            this.target = target;
            this.select = select;
        }

        public DockRequest(ViewAttachment viewAttachment, View source, UUID target) {
            super();
            this.viewAttachment = viewAttachment;
            this.source = source;
            this.target = target;
        }

        public ViewAttachment getViewAttachment() {
            return viewAttachment;
        }

        public View getSource() {
            return source;
        }

        public UUID getTarget() {
            return target;
        }

        public boolean isSelect() {
            return select;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (select ? 1231 : 1237);
            result = prime * result + ((source == null) ? 0 : source.hashCode());
            result = prime * result + ((target == null) ? 0 : target.hashCode());
            result = prime * result + ((viewAttachment == null) ? 0 : viewAttachment.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DockRequest other = (DockRequest) obj;
            if (select != other.select)
                return false;
            if (source == null) {
                if (other.source != null)
                    return false;
            } else if (!source.equals(other.source))
                return false;
            if (target == null) {
                if (other.target != null)
                    return false;
            } else if (!target.equals(other.target))
                return false;
            if (viewAttachment == null) {
                if (other.viewAttachment != null)
                    return false;
            } else if (!viewAttachment.equals(other.viewAttachment))
                return false;
            return true;
        };

    }

    @ApplicationInstanceSingleton
    public class ViewManagerImpl implements ViewManager {

        private ViewSubjects subjects;

        public ViewManagerImpl() {
            subjects = new ViewSubjects();
        }

        @Override
        public Subject<DockRequest> dock() {
            return subjects.getDock();
        }

        @Override
        public Subject<View> undock() {
            return subjects.getUndock();
        }

        @Override
        public Subject<View> close() {
            return subjects.getClose();
        }
    }

    public class ViewSubjects extends SubjectManager {

        private PublishSubject<DockRequest> dock;
        private PublishSubject<View> undock;
        private PublishSubject<View> close;

        public ViewSubjects() {
            dock = wrap(ViewSubjects.class, "dock", PublishSubject.create()); // NOI18N
            undock = wrap(ViewSubjects.class, "undock", PublishSubject.create()); // NOI18N
            close = wrap(ViewSubjects.class, "close", PublishSubject.create()); // NOI18N
        }

        public PublishSubject<DockRequest> getDock() {
            return dock;
        }

        public PublishSubject<View> getUndock() {
            return undock;
        }

        public PublishSubject<View> getClose() {
            return close;
        }

    }
}
