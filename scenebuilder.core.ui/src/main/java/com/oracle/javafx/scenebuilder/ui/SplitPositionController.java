/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.preferences.type.DoublePreference;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class SplitPositionController {
    
    //private static final int MIN_DIVIDER_DIFFERENCE = 20;
    
    public static final AllocationStrategy MAIN_LEFT_RIGHT = new AllocationStrategy() {
        
        @Override
        public Map<Divider, DoublePreference> allocate(SplitPositionController controller) {
            Map<Divider, DoublePreference> result = new HashMap<>();
            var dividers = controller.getSplitPane().getDividers();
            var items = controller.getSplitPane().getItems();
            
            boolean leftInserted = items.contains(controller.getSplitContents()[0].getContent());
            boolean rightInserted = items.contains(controller.getSplitContents()[2].getContent());
            
            if (leftInserted && rightInserted) {
                result.put(dividers.get(0), controller.getSplitDividers()[0].getDividerPosition());
                result.put(dividers.get(1), controller.getSplitDividers()[1].getDividerPosition());
            } else if (leftInserted) {
                result.put(dividers.get(0), controller.getSplitDividers()[0].getDividerPosition());
            } else if (rightInserted) {
                result.put(dividers.get(0), controller.getSplitDividers()[1].getDividerPosition());
            }
            
            return result;
        }
    };
    
    public static final AllocationStrategy MAIN_TOP_BOTTOM = new AllocationStrategy() {
        
        @Override
        public Map<Divider, DoublePreference> allocate(SplitPositionController controller) {
            Map<Divider, DoublePreference> result = new HashMap<>();
            var dividers = controller.getSplitPane().getDividers();
            var items = controller.getSplitPane().getItems();
            
            boolean bottomInserted = items.contains(controller.getSplitContents()[1].getContent());
            
            if (bottomInserted) {
                result.put(dividers.get(0), controller.getSplitDividers()[0].getDividerPosition());
            }
            
            return result;
        }
    };
    
    private static Logger logger = LoggerFactory.getLogger(SplitPositionController.class);
    
    int splitContentCount;
    
    int contentIndex = 0;
    int dividerIndex = 0;
    
    private final @Getter SplitContent[] splitContents;
    private final @Getter SplitDivider[] splitDividers; 
    private final @Getter SplitPane splitPane;
    
    private AllocationStrategy strategy;
    private List<Listeners> currentListenerAllocations = new ArrayList<>();

    private Map<Divider, DoublePreference> allocations;
    
    public static SplitDivider of(SplitPane splitPane, int splitPartCount) {
        SplitPositionController spc = new SplitPositionController(splitPane, splitPartCount);
        return spc.new SplitDivider();
    }

    private SplitPositionController(SplitPane splitPane, int splitPartCount) {
        this.splitPane = splitPane;
        this.splitContents = new SplitContent[splitPartCount];
        this.splitDividers = new SplitDivider[splitPartCount - 1];
    }
    
    public boolean validateSplitPaneContent() {
        return splitPane.getItems().stream()
                .allMatch(c -> Arrays.stream(splitContents).anyMatch(sc -> sc.getContent() == c));
    }
    public void createListeners() {
        splitPane.getItems().addListener((Change<? extends Node> c)-> update());
    }
    
    public void updateAllocations() {
        try {
            currentListenerAllocations.clear();
            allocations = strategy.allocate(this);
            allocations.entrySet().forEach(e -> {
                Divider divider = e.getKey();
                DoublePreference preference = e.getValue();
                
                ChangeListener<? super Number> dividerListener = (ob, o, n) -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("SET this: " + this + " ob: " + ob + " c: " + preference.getClass().getSimpleName() + " o:" + o + " n:" + n);
                    }
                    preference.setValue(n.doubleValue());
                };
                
                ChangeListener<? super Double> preferenceListener = (ob, o, n) -> {
                    if (logger.isDebugEnabled()) {
                        System.out.println("SET this: " + this + " ob: " + ob + " c: " + preference.getClass().getSimpleName() + " o:" + o + " n:" + n);
                    }
                    divider.setPosition(n);
                };
                currentListenerAllocations.add(new Listeners(divider, preference, dividerListener, preferenceListener));
            });
        } catch (AllocationStrategyException e) {
            logger.error("Unable to allocation divider position preferences", e);
        }
    }

    public void apply() {
        allocations.entrySet().forEach(e -> {
            Divider divider = e.getKey();
            DoublePreference preference = e.getValue();
            
            logger.debug("Setting divider : {} to position : {}", divider, preference.getValue());
            divider.setPosition(preference.getValue());
        });
    }
    
    public void track() {
        assert currentListenerAllocations != null;
        
        logger.debug("Updating splitPosition controller listeners");
        currentListenerAllocations.forEach(a -> {
            a.getDivider().positionProperty().addListener(a.getDividerListener());
            a.getPreference().getObservableValue().addListener(a.getPreferenceListener());
        });
        logger.debug("Current divider positions : {}", splitPane.getDividerPositions());
    }
    
    public void untrack() {
        currentListenerAllocations.forEach(l -> {
            l.getDivider().positionProperty().removeListener(l.getDividerListener());
            l.getPreference().getObservableValue().removeListener(l.getPreferenceListener());
        });
    }
    
    public void update() {
        untrack();
        updateAllocations();
        apply();
        track();
    }
    
    @RequiredArgsConstructor
    protected class SplitContent {
        private final @Getter int index;
        private final @Getter Node content;
        private final @Getter boolean fixed;
        
        public SplitDivider withDivider(DoublePreference dividerPosition) {
            SplitDivider sd = new SplitDivider(dividerIndex, dividerPosition);
            splitDividers[dividerIndex] = sd;
            dividerIndex++;
            return sd;
        }
        
        public SplitPositionController build(AllocationStrategy strategy) {
            SplitPositionController.this.strategy = strategy;
            SplitPositionController.this.createListeners();
            SplitPositionController.this.update();
            return SplitPositionController.this;
        }
    }
    
    @RequiredArgsConstructor
    protected class SplitDivider {
        private final @Getter int index;
        private final @Getter DoublePreference dividerPosition;
        
        private SplitDivider() {
            index = -1;
            dividerPosition = null;
        }
        
        public SplitContent withContent(Node content, boolean fixed) {
            SplitContent sp = new SplitContent(contentIndex, content, fixed);
            splitContents[contentIndex] = sp;
            contentIndex++;
            return sp;
        }
    }
    
    public interface AllocationStrategy {
        Map<Divider, DoublePreference> allocate(SplitPositionController controller) throws AllocationStrategyException;
    }
    
    public static class AllocationStrategyException extends Exception {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public AllocationStrategyException(String message, Throwable cause) {
            super(message, cause);
        }

        public AllocationStrategyException(String message) {
            super(message);
        }
    }
    
    @RequiredArgsConstructor
    private static class Listeners {
        private final @Getter Divider divider;
        private final @Getter DoublePreference preference;
        private final @Getter ChangeListener<? super Number> dividerListener;
        private final @Getter ChangeListener<? super Double> preferenceListener;
    }
}
