/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
/**
 * The {@code javafx} package contains classes that provide utility methods for
 * JavaFX applications.
 *
 * Running multiple javafx applications with different class loaders is not
 * supported out of the box by the JavaFX runtime. There's only one javafx
 * thread that must be shared between applications. To handle class loader
 * switching effectively in a JavaFX application, we need to identify key points
 * where the context class loader should be set appropriately. The main events
 * and interactions where class loader switching is necessary typically
 * include:<br/>
 * <br/>
 * - <b>Application Startup</b>: When the JavaFX application is initially
 * launched.<br/>
 * - <b>Event Handling</b>: When UI events are processed.<br/>
 * - <b>FXML Loading</b>: When loading FXML files if they belong to different
 * class loaders.<br/>
 * - <b>Task Execution</b>: When running background tasks using Service or
 * Task.<br/>
 * - <b>Scheduled Tasks</b>: When scheduling tasks with Timeline or
 * Animation.<br/>
 * - <b>Controller Initialization</b>: When initializing controllers for FXML
 * files.<br/>
 * - <b>CSS Loading</b>: When applying different stylesheets.<br/>
 * - <b>Resource Access</b>: When accessing resources like images, files, etc.,
 * from different class loaders.<br/>
 * <br/>
 * <br/>
 * <b>Application Startup</b>:<br/>
 * When the JavaFX application is initially launched<br/>
 * - we set the javafx thread's classloader with a singleton instance of
 * {@link JavafxThreadClassloaderDispatcherImpl}<br/>
 * This class is responsible of switching the context class loader according
 * to the focused window<br/>
 * - we set any new window event dispatcher with a singleton instance of
 * {@link ContextClassLoaderEventDispatcher}<br/>
 * This class is responsible of switching the context class loader according
 * to the window owning the event source<br/>
 * <br/>
 * <b>Event Handling</b>:<br/>
 * When UI events are processed it is expected that the according window is focused.
 * Classloader is then handled by {@link ContextClassLoaderEventDispatcher}<br/>
 * <br/>
 * <b>FXML Loading</b>:<br/>
 * To be defined<br/>
 * <br/>
 * <b>Task Execution</b>:<br/>
 * To be defined<br/>
 * <br/>
 * <b>Scheduled Tasks</b>:<br/>
 * To be defined<br/>
 * <br/>
 * <b>Controller Initialization</b>:<br/>
 * To be defined<br/>
 * <br/>
 * <b>CSS Loading</b>:<br/>
 * To be defined<br/>
 * <br/>
 * <b>Resource Access</b>:<br/>
 * To be defined<br/>
 * <br/>
 */
package com.gluonhq.jfxapps.core.api.javafx;

import com.gluonhq.jfxapps.core.api.javafx.internal.ContextClassLoaderEventDispatcher;
import com.gluonhq.jfxapps.core.api.javafx.internal.JavafxThreadClassloaderDispatcherImpl;